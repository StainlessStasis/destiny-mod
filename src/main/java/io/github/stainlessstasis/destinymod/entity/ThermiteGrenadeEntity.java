package io.github.stainlessstasis.destinymod.entity;

import io.github.stainlessstasis.destinymod.client.effects.ClientAudioAndVFX;
import io.github.stainlessstasis.destinymod.destiny_combat.CombatUtils;
import io.github.stainlessstasis.destinymod.destiny_combat.damage.DMDamageTypes;
import io.github.stainlessstasis.destinymod.destiny_combat.damage.DestinyDamageBuilder;
import io.github.stainlessstasis.destinymod.destiny_combat.status_effect.StatusEffectManager;
import io.github.stainlessstasis.destinymod.registry.datapack.Abilities;
import io.github.stainlessstasis.destinymod.registry.property.ability.ThermiteGrenadeProperty;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.function.Predicate;

public class ThermiteGrenadeEntity extends AbstractAbilityEntity {
    private static final EntityDataAccessor<Integer> CURRENT_PULSE = SynchedEntityData.defineId(ThermiteGrenadeEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Float> TRAVELED_DISTANCE = SynchedEntityData.defineId(ThermiteGrenadeEntity.class, EntityDataSerializers.FLOAT);
    public static final float SUBSTEP_DISTANCE = 0.25f;

    private int maxPulses;
    private int pulseInterval;
    private float distancePerTick;
    private float maxDistance;
    private float maxStepHeight;
    private float width;
    private float height;
    private final Set<UUID> hitEntitiesThisPulse = new HashSet<>();
    private float lastObservedDistance = 0f;
    private int lastObservedPulse = 0;

    private ThermiteGrenadeEntity(EntityType<? extends AbstractAbilityEntity> type, Level level) {
        super(type, level, Abilities.THERMITE_GRENADE.get(level));
        this.ability.getProperty(ThermiteGrenadeProperty.class).ifPresent(this::applyProperties);
    }

    public static ThermiteGrenadeEntity createDefault(EntityType<? extends AbstractAbilityEntity> entityType, Level level) {
        return new ThermiteGrenadeEntity(entityType, level);
    }

    public ThermiteGrenadeEntity(EntityType<? extends AbstractAbilityEntity> type, Level level, Vec3 pos, float yaw, @Nullable LivingEntity owner) {
        super(type, level, pos, owner, Abilities.THERMITE_GRENADE.get(level));
        this.ability.getProperty(ThermiteGrenadeProperty.class).ifPresent(this::applyProperties);
        setYRot(yaw);
    }

    public void applyProperties(ThermiteGrenadeProperty props) {
        setCurrentPulse(0);
        this.maxPulses = props.pulses();
        this.pulseInterval = props.pulseIntervalTicks();
        this.distancePerTick = props.distancePerTick();
        this.maxDistance = props.maxDistance();
        this.maxStepHeight = props.maxStepHeight();
        this.width = props.width();
        this.height = props.height();
    }

    @Override
    public void tick() {
        super.tick();
        executeServerPulseLogic();
        spawnClientPulseVisuals();
    }

    private void executeServerPulseLogic() {
        if (level().isClientSide()) return;

        int interval = getPulseInterval();
        int maxPulses = getMaxPulses();

        boolean shouldPulse = (getCurrentPulse() == 0) || (interval > 0 && tickCount % interval == 0);
        if (shouldPulse) {
            int nextPulse = getCurrentPulse() + 1;
            setCurrentPulse(nextPulse);
            setTraveledDistance(0f);
            hitEntitiesThisPulse.clear();

            if (nextPulse > maxPulses) {
                discard();
                return;
            }
        }

        if (getCurrentPulse() > 0 && getCurrentPulse() <= maxPulses) {
            float currentDist = getTraveledDistance();
            float maxDist = getMaxDistance();

            if (currentDist < maxDist) {
                float nextDist = Math.min(currentDist + getDistancePerTick(), maxDist);
                MarchResult result = marchPulsePath(currentDist, nextDist, (pos, _) -> damageEntitiesAtPosition(pos, this.hitEntitiesThisPulse));
                if (result.isBlocked()) {
                    setTraveledDistance(maxDist);
                } else {
                    setTraveledDistance(nextDist);
                }
            }
        }
    }
    
    private void spawnClientPulseVisuals() {
        if (!level().isClientSide()) return;

        int currentPulse = getCurrentPulse();
        float currentDist = getTraveledDistance();

        if (currentPulse > lastObservedPulse) {
            lastObservedPulse = currentPulse;
            lastObservedDistance = 0f;
        }

        if (currentDist > lastObservedDistance) {
            float yawRad = (float) Math.toRadians(getYRot());
            Vec3 forwardDir = new Vec3(-Math.sin(yawRad), 0, Math.cos(yawRad)).normalize();
            Vec3 rightDir = new Vec3(-forwardDir.z, 0, forwardDir.x);
            MarchResult result = marchPulsePath(lastObservedDistance, currentDist, (pos, firstStep) -> {
                ClientAudioAndVFX.thermitePulseStep(level(), pos, forwardDir, rightDir, width, height, firstStep);
            });
            ClientAudioAndVFX.addFadingLight(result.finalPos(), Math.round(width)+1, 20);

            lastObservedDistance = currentDist;
        }
    }

    private MarchResult marchPulsePath(float startDist, float endDist, PulseStepCallback callback) {
        float yawRad = (float) Math.toRadians(getYRot());
        Vec3 forwardDir = new Vec3(-Math.sin(yawRad), 0, Math.cos(yawRad)).normalize();
        Vec3 currentPos = position().add(forwardDir.scale(startDist));
        float maxStep = getMaxStepHeight();
        float totalStepDistance = endDist - startDist;
        float distanceThisStep = 0f;
        boolean isBlocked = false;

        while (distanceThisStep < totalStepDistance) {
            float substep = Math.min(SUBSTEP_DISTANCE, totalStepDistance - distanceThisStep);
            Vec3 nextPos = currentPos.add(forwardDir.scale(substep));

            BlockPos blockPos = BlockPos.containing(nextPos);
            BlockState blockState = level().getBlockState(blockPos);
            if (!blockState.isAir() && blockState.isCollisionShapeFullBlock(level(), blockPos)) {
                // something is in the way, check if it can step up
                double originalY = currentPos.y;
                double obstacleTopY = blockPos.getY() + blockState.getShape(level(), blockPos).max(Direction.Axis.Y);
                double stepHeightNeeded = obstacleTopY - originalY;

                if (stepHeightNeeded <= maxStep) {
                    currentPos = new Vec3(nextPos.x, obstacleTopY, nextPos.z);
                } else {
                    isBlocked = true;
                    break; // wall is too high to step up
                }
            } else {
                // nothing is in the way, but we need to check if it can step down
                BlockPos floorPos = blockPos.below();
                BlockState floorState = level().getBlockState(floorPos);

                float dropChecked = 1f;
                boolean foundFloor = false;
                double solidFloorY = currentPos.y;

                while (dropChecked <= Math.ceil(maxStep) + 1f) {
                    if (!floorState.isAir() && floorState.isCollisionShapeFullBlock(level(), floorPos)) {
                        double floorTopY = floorPos.getY() + floorState.getShape(level(), floorPos).max(Direction.Axis.Y);
                        double dropHeight = currentPos.y - floorTopY;

                        if (dropHeight <= maxStep) {
                            solidFloorY = floorTopY;
                            foundFloor = true;
                        }
                        break; // hit a block
                    }

                    floorPos = floorPos.below();
                    floorState = level().getBlockState(floorPos);
                    dropChecked += 1f;
                }

                if (foundFloor) {
                    currentPos = new Vec3(nextPos.x, solidFloorY, nextPos.z);
                } else {
                    isBlocked = true;
                    break;
                }
            }

            boolean isFirstStep = (startDist == 0f) && (distanceThisStep == 0f);
            callback.onStep(currentPos, isFirstStep);
            distanceThisStep += substep;
        }

        return new MarchResult(currentPos, isBlocked);
    }

    @FunctionalInterface
    public interface PulseStepCallback {
        void onStep(Vec3 position, boolean isFirstStep);
    }

    private record MarchResult(Vec3 finalPos, boolean isBlocked) {}

    private void damageEntitiesAtPosition(Vec3 pos, Set<UUID> hitEntities) {
        float width = getWidth();
        float halfWidth = width / 2f;
        float height = getHeight();
        float yPadding = 0.25f;

        double searchRadius = Math.sqrt(halfWidth * halfWidth + SUBSTEP_DISTANCE * SUBSTEP_DISTANCE);
        AABB searchArea = new AABB(
                pos.x - searchRadius, pos.y - yPadding, pos.z - searchRadius,
                pos.x + searchRadius, pos.y + height, pos.z + searchRadius
        );

        float yawRad = (float) Math.toRadians(getYRot());
        Vec3 forwardDir = new Vec3(-Math.sin(yawRad), 0, Math.cos(yawRad)).normalize();
        Vec3 rightDir = new Vec3(-forwardDir.z, 0, forwardDir.x);

        Predicate<LivingEntity> areaFilter = target -> {
            Vec3 relativePos = target.position().subtract(pos);

            double distanceAlongWidth = relativePos.dot(rightDir);
            if (Math.abs(distanceAlongWidth) > halfWidth) {
                return false;
            }

            double distanceAlongLength = relativePos.dot(forwardDir);
            if (Math.abs(distanceAlongLength) > (SUBSTEP_DISTANCE / 2f)) {
                return false;
            }

            return relativePos.y >= -yPadding && relativePos.y <= height;
        };

        List<LivingEntity> targets = CombatUtils.getEntitiesInArea(searchArea, level(), LivingEntity.class, getOwner(), hitEntities, areaFilter);
        for (LivingEntity target : targets) {
            DestinyDamageBuilder.create(DMDamageTypes.GRENADE_ABILITY, target)
                    .damage(this.ability.damage())
                    .knockback(false)
                    .directSource(this)
                    .attacker(getOwner())
                    .invulnerabilityTicks(0)
                    .element(this.ability.element())
                    .execute();
            StatusEffectManager.applyScorch(target, getOwner(), this.ability.scorch());

            this.hitEntitiesThisPulse.add(target.getUUID());
        }
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.@NonNull Builder builder) {
        builder.define(CURRENT_PULSE, 0);
        builder.define(TRAVELED_DISTANCE, 0f);
    }

    public int getCurrentPulse() { return entityData.get(CURRENT_PULSE); }
    public void setCurrentPulse(int pulse) { entityData.set(CURRENT_PULSE, pulse); }
    public float getTraveledDistance() { return entityData.get(TRAVELED_DISTANCE); }
    public void setTraveledDistance(float distance) { entityData.set(TRAVELED_DISTANCE, distance); }

    public int getMaxPulses() { return maxPulses; }
    public int getPulseInterval() { return pulseInterval; }
    public float getDistancePerTick() { return distancePerTick; }
    public float getMaxDistance() { return maxDistance; }
    public float getMaxStepHeight() { return maxStepHeight; }
    public float getWidth() { return width; }
    public float getHeight() { return height; }
}
