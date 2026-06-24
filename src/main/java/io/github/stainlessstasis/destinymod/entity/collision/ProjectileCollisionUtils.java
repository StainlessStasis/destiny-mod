package io.github.stainlessstasis.destinymod.entity.collision;

import com.mojang.math.Constants;
import io.github.stainlessstasis.destinymod.entity.ability.DestinyAbilityEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.entity.PartEntity;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public final class ProjectileCollisionUtils {
    private ProjectileCollisionUtils() {}

    private static final Direction.Axis[] AXES = {
            Direction.Axis.X, Direction.Axis.Y, Direction.Axis.Z
    };

    /**
     * Tests the entity for collisions against blocks and entities. Returns whichever collision is earliest.
     */
    public static Optional<CollisionContext> checkCollisions(Entity entity, Set<Entity> alreadyCollidedThisTick, boolean skipSameOwnerProjectiles) {
        return checkCollisions(entity, alreadyCollidedThisTick, entity.getDeltaMovement(), skipSameOwnerProjectiles);
    }

    /**
     * Alternative method for checkCollisions which uses a custom movement vector instead of delta movement.
     */
    public static Optional<CollisionContext> checkCollisions(Entity entity, Set<Entity> alreadyCollidedThisTick, Vec3 movement, boolean skipSameOwnerProjectiles) {
        AABB preTick = entity.getBoundingBox();
        AABB postTick = preTick.move(movement);
        AABB sweepVolume = union(preTick, postTick);

        Optional<CollisionContext> blockHit = checkBlockCollisions(entity, sweepVolume);
        Optional<CollisionContext> entityHit = checkEntityCollisions(entity, sweepVolume, alreadyCollidedThisTick, skipSameOwnerProjectiles);

        if (blockHit.isEmpty()) return entityHit;
        if (entityHit.isEmpty()) return blockHit;

        return blockHit.get().timeOfImpact() <= entityHit.get().timeOfImpact() ? blockHit : entityHit;
    }

    // --- Blocks ---

    private static Optional<CollisionContext> checkBlockCollisions(Entity entity, AABB sweepVolume) {
        Set<BlockPos> candidates = getBlockCandidates(entity.level(), sweepVolume);
        if (candidates.isEmpty()) return Optional.empty();
        return collideBlocks(entity, candidates);
    }

    private static Set<BlockPos> getBlockCandidates(Level level, AABB sweepVolume) {
        return BlockPos.betweenClosedStream(sweepVolume)
                .filter(blockPos -> {
                    BlockState state = level.getBlockState(blockPos);
                    return !state.isAir() && state.blocksMotion(); // ignore that this is deprecated, it's fine for now
                })
                .map(BlockPos::immutable)
                .collect(Collectors.toSet());
    }

    private static Optional<CollisionContext> collideBlocks(Entity entity, Set<BlockPos> candidates) {
        AABB projectileBox = entity.getBoundingBox();
        Vec3 velocity = entity.getDeltaMovement();
        Level level = entity.level();

        double bestTime = Double.POSITIVE_INFINITY;
        BlockHitResult bestHit = null;
        Vec3 bestNormal = Vec3.ZERO;

        for (BlockPos pos : candidates) {
            BlockState state = level.getBlockState(pos);
            var shape = state.getCollisionShape(level, pos);
            List<AABB> blockBoxes = shape.isEmpty()
                    ? List.of(AABB.ofSize(pos.getCenter(), 1, 1, 1))
                    : shape.toAabbs();
            for (AABB localBox : blockBoxes) {
                AABB blockBox = localBox.move(pos.getX(), pos.getY(), pos.getZ());

                SweepTestResult result = sweepTest(projectileBox, blockBox, velocity, Vec3.ZERO);

                if (result.hit() && result.tEntry() < bestTime) {
                    bestTime = result.tEntry();
                    bestNormal = result.normal();
                    Vec3 hitPos = entity.position().add(velocity.scale(bestTime));
                    Vec3i bestNormalVec3i = new Vec3i((int) bestNormal.x, (int) bestNormal.y, (int) bestNormal.z);
                    bestHit = new BlockHitResult(
                            hitPos,
                            Direction.getNearest(bestNormalVec3i, Direction.getApproximateNearest(bestNormal)),
                            pos, false);
                }
            }
        }

        if (bestHit == null) return Optional.empty();
        return Optional.of(new CollisionContext(bestHit, bestNormal, entity.getDeltaMovement(), Vec3.ZERO,
                0, // In my other project I grabbed this code from, the projectiles can actually have mass, but I'm leaving it unimplemented in this mod for now.
                              // This is just so that we can implement it in the future if we so choose
                0, bestTime));
    }

    // --- Entities ---

    private static Optional<CollisionContext> checkEntityCollisions(Entity entity, AABB sweepVolume, Set<Entity> alreadyCollidedThisTick, boolean skipSameOwnerProjectiles) {
        Set<Entity> candidates = getEntityCandidates(entity, sweepVolume, alreadyCollidedThisTick, skipSameOwnerProjectiles);
        if (candidates.isEmpty()) return Optional.empty();
        return collideEntities(entity, candidates);
    }

    private static Set<Entity> getEntityCandidates(Entity entity, AABB searchArea, Set<Entity> alreadyCollidedThisTick, boolean skipSameOwnerProjectiles) {
        Entity owner = entity instanceof Projectile projectile ? projectile.getOwner() : null;

        return new HashSet<>(entity.level().getEntities(entity, searchArea, candidate -> {
            if (alreadyCollidedThisTick.stream().anyMatch(candidate::is)) return false;

            Entity checkTarget = candidate instanceof PartEntity<?> part ? part.getParent() : candidate;
            boolean isValidType = checkTarget instanceof LivingEntity
                    || checkTarget instanceof Projectile
                    || (checkTarget instanceof DestinyAbilityEntity ability && ability.isValidAbilityTarget());
            if (!isValidType) return false;

            if (owner != null && checkTarget.getUUID().equals(owner.getUUID())) return false;

            // skip other projectiles from the same owner
            if (skipSameOwnerProjectiles && owner != null && checkTarget instanceof Projectile other) {
                Entity otherOwner = other.getOwner();
                return otherOwner == null || !otherOwner.getUUID().equals(owner.getUUID());
            }
            return true;
        }))
        .stream()
        .flatMap(candidate -> candidate.isMultipartEntity()
                ? Arrays.stream(candidate.getParts()).filter(part -> part != null && part.getBoundingBox().intersects(searchArea))
                : Stream.of(candidate))
        .collect(Collectors.toSet());
    }

    private static Optional<CollisionContext> collideEntities(Entity entity, Set<Entity> candidates) {
        AABB projectileBox = entity.getBoundingBox();
        Vec3 velocity = entity.getDeltaMovement();

        double bestTime = Double.POSITIVE_INFINITY;
        EntityHitResult bestHit = null;
        Vec3 bestNormal = Vec3.ZERO;
        Vec3 bestVictimVelocity = Vec3.ZERO;
        float bestVictimMass = 0;

        for (Entity candidate : candidates) {
            Vec3 entityVelocity = candidate.getDeltaMovement();
            SweepTestResult result = sweepTest(projectileBox, candidate.getBoundingBox(), velocity, entityVelocity);

            if (result.hit() && result.tEntry() < bestTime) {
                bestTime = result.tEntry();
                bestNormal = result.normal();
                bestVictimVelocity = entityVelocity;
                if (candidate instanceof Projectile proj) bestVictimMass = /* proj.getEnergy(); */ 0;
                Vec3 hitPos = entity.position().add(velocity.scale(bestTime));
                bestHit = new EntityHitResult(candidate, hitPos);
            }
        }

        if (bestHit == null) return Optional.empty();
        return Optional.of(new CollisionContext(bestHit, bestNormal, entity.getDeltaMovement(), bestVictimVelocity, /*projectile.getEnergy()*/ 0, bestVictimMass, bestTime));
    }

    // --- the rest of the shit ---

    /**
     * Swept AABB test between a moving AABB and a target AABB.
     * Uses relative velocity for entities colliding with each other.
     */
    private static SweepTestResult sweepTest(AABB moving, AABB target, Vec3 movingVel, Vec3 targetVel) {
        Vec3 relVel = movingVel.subtract(targetVel);

        double tEntry = 0;
        double tExit = 1;
        Vec3 normal = Vec3.ZERO;

        for (Direction.Axis axis : AXES) {
            double vel = component(relVel, axis);
            double movingMin = moving.min(axis);
            double movingMax = moving.max(axis);
            double targetMin = target.min(axis);
            double targetMax = target.max(axis);

            if (Math.abs(vel) < Constants.EPSILON) {
                // No relative movement on this axis - must already be overlapping
                if (movingMax < targetMin || movingMin > targetMax) {
                    return SweepTestResult.MISS;
                }
                // Already overlapping - this axis never enters or exits
                continue;
            }

            double t1 = (targetMin - movingMax) / vel;
            double t2 = (targetMax - movingMin) / vel;
            double tEntryAxis = Math.min(t1, t2);
            double tExitAxis = Math.max(t1, t2);

            if (tEntryAxis > tEntry + Constants.EPSILON) {
                tEntry = tEntryAxis;
                double n = vel > 0 ? -1 : 1;
                normal = switch (axis) {
                    case X -> new Vec3(n, 0, 0);
                    case Y -> new Vec3(0, n, 0);
                    case Z -> new Vec3(0, 0, n);
                };
            }

            tExit = Math.min(tExit, tExitAxis);

            if (tEntry > tExit + Constants.EPSILON) {
                return SweepTestResult.MISS;
            }
        }

        if (tEntry < 0 || tEntry >= 1) return SweepTestResult.MISS;
        return new SweepTestResult(true, tEntry, normal);
    }

    private static double component(Vec3 v, Direction.Axis axis) {
        return switch (axis) {
            case X -> v.x;
            case Y -> v.y;
            case Z -> v.z;
        };
    }

    private static AABB union(AABB a, AABB b) {
        return new AABB(
                Math.min(a.minX, b.minX), Math.min(a.minY, b.minY), Math.min(a.minZ, b.minZ),
                Math.max(a.maxX, b.maxX), Math.max(a.maxY, b.maxY), Math.max(a.maxZ, b.maxZ)
        );
    }

    private record SweepTestResult(boolean hit, double tEntry, Vec3 normal) {
        static final SweepTestResult MISS = new SweepTestResult(false, Double.POSITIVE_INFINITY, Vec3.ZERO);
    }
}
