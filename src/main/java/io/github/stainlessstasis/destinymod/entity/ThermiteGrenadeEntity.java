package io.github.stainlessstasis.destinymod.entity;

import io.github.stainlessstasis.destinymod.destiny_combat.CombatUtils;
import io.github.stainlessstasis.destinymod.destiny_combat.damage.DMDamageTypes;
import io.github.stainlessstasis.destinymod.destiny_combat.damage.DestinyDamageBuilder;
import io.github.stainlessstasis.destinymod.registry.datapack.Abilities;
import io.github.stainlessstasis.destinymod.registry.property.ability.ThermiteGrenadeProperty;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
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
import java.util.function.Predicate;

public class ThermiteGrenadeEntity extends AbstractAbilityEntity {
    private static final EntityDataAccessor<Integer> CURRENT_PULSE = SynchedEntityData.defineId(ThermiteGrenadeEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> MAX_PULSES = SynchedEntityData.defineId(ThermiteGrenadeEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> PULSE_INTERVAL = SynchedEntityData.defineId(ThermiteGrenadeEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Float> DISTANCE_PER_TICK = SynchedEntityData.defineId(ThermiteGrenadeEntity.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Float> MAX_DISTANCE = SynchedEntityData.defineId(ThermiteGrenadeEntity.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Float> MAX_STEP_HEIGHT = SynchedEntityData.defineId(ThermiteGrenadeEntity.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Float> TRAVELED_DISTANCE = SynchedEntityData.defineId(ThermiteGrenadeEntity.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Float> WIDTH = SynchedEntityData.defineId(ThermiteGrenadeEntity.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Float> HEIGHT = SynchedEntityData.defineId(ThermiteGrenadeEntity.class, EntityDataSerializers.FLOAT);
    public static final float SUBSTEP_DISTANCE = 0.25f;

    private final Set<Entity> hitEntitiesThisPulse = new HashSet<>();
    private float lastObservedDistance = 0f;
    private int lastObservedPulse = 0;

    private ThermiteGrenadeEntity(EntityType<? extends AbstractAbilityEntity> type, Level level) {
        super(type, level, Abilities.THERMITE_GRENADE.get(level));
    }

    public static ThermiteGrenadeEntity createDefault(EntityType<? extends AbstractAbilityEntity> entityType, Level level) {
        return new ThermiteGrenadeEntity(entityType, level);
    }

    public ThermiteGrenadeEntity(EntityType<? extends AbstractAbilityEntity> type, Level level, Vec3 pos, @Nullable LivingEntity owner) {
        super(type, level, pos, owner, Abilities.THERMITE_GRENADE.get(level));
        if (! level.isClientSide()) {
            this.ability.getProperty(ThermiteGrenadeProperty.class).ifPresent(props -> {
                setCurrentPulse(0);
                setMaxPulses(props.pulses());
                setPulseInterval(props.pulseIntervalTicks());
                setDistancePerTick(props.distancePerTick());
                setMaxDistance(props.maxDistance());
                setMaxStepHeight(props.maxStepHeight());
                setWidth(props.width());
                setHeight(props.height());
            });
        }
        if (owner != null) {
            setYRot(owner.getYRot());
        }
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

        if (interval > 0 && tickCount % interval == 0) {
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
                marchPulsePath(currentDist, nextDist, pos -> damageEntitiesAtPosition(pos, this.hitEntitiesThisPulse));
                setTraveledDistance(nextDist);
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
            float halfWidth = getWidth() / 2f;
            float height = getHeight();

            marchPulsePath(lastObservedDistance, currentDist, pos -> {
                for (float wOffset = -halfWidth; wOffset <= halfWidth; wOffset += 0.5f) {
                    Vec3 particlePos = pos.add(rightDir.scale(wOffset));

                    level().addParticle(
                            ParticleTypes.FLAME,
                            particlePos.x, particlePos.y + 0.1, particlePos.z,
                            0, 0, 0
                    );
                }

                for (float hOffset = 0.5f; hOffset <= height; hOffset += 0.5f) {
                    Vec3 leftEdge = pos.add(rightDir.scale(-halfWidth));
                    Vec3 rightEdge = pos.add(rightDir.scale(halfWidth));

                    level().addParticle(
                            ParticleTypes.SMALL_FLAME,
                            leftEdge.x, leftEdge.y + hOffset, leftEdge.z,
                            0, 0, 0
                    );
                    level().addParticle(
                            ParticleTypes.SMALL_FLAME,
                            rightEdge.x, rightEdge.y + hOffset, rightEdge.z,
                            0, 0, 0
                    );
                }
            });
            lastObservedDistance = currentDist;
        }
    }

    private void marchPulsePath(float startDist, float endDist, PulseStepCallback callback) {
        float yawRad = (float) Math.toRadians(getYRot());
        Vec3 forwardDir = new Vec3(-Math.sin(yawRad), 0, Math.cos(yawRad)).normalize();
        Vec3 currentPos = position().add(forwardDir.scale(startDist));
        float maxStep = getMaxStepHeight();
        float totalStepDistance = endDist - startDist;
        float distanceThisStep = 0f;

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
                    break; // wall is too high to step up
                }
            } else {
                // path is clear
                currentPos = nextPos;
            }

            callback.onStep(currentPos);
            distanceThisStep += substep;
        }
    }

    @FunctionalInterface
    public interface PulseStepCallback {
        void onStep(Vec3 position);
    }

    private void damageEntitiesAtPosition(Vec3 pos, Set<Entity> hitEntities) {
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
            // TODO: scorch
        }
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.@NonNull Builder builder) {
        builder.define(CURRENT_PULSE, 0);
        builder.define(MAX_PULSES, 0);
        builder.define(PULSE_INTERVAL, 0);
        builder.define(DISTANCE_PER_TICK, 0f);
        builder.define(MAX_DISTANCE, 0f);
        builder.define(MAX_STEP_HEIGHT, 0f);
        builder.define(TRAVELED_DISTANCE, 0f);
        builder.define(WIDTH, 0f);
        builder.define(HEIGHT, 0f);
    }

    public int getCurrentPulse() {
        return entityData.get(CURRENT_PULSE);
    }

    public void setCurrentPulse(int pulse) {
        entityData.set(CURRENT_PULSE, pulse);
    }

    public int getMaxPulses() {
        return entityData.get(MAX_PULSES);
    }

    public void setMaxPulses(int maxPulses) {
        entityData.set(MAX_PULSES, maxPulses);
    }

    public int getPulseInterval() {
        return entityData.get(PULSE_INTERVAL);
    }

    public void setPulseInterval(int intervalTicks) {
        entityData.set(PULSE_INTERVAL, intervalTicks);
    }

    public float getDistancePerTick() {
        return entityData.get(DISTANCE_PER_TICK);
    }

    public void setDistancePerTick(float distancePerTick) {
        entityData.set(DISTANCE_PER_TICK, distancePerTick);
    }

    public float getMaxDistance() {
        return entityData.get(MAX_DISTANCE);
    }

    public void setMaxDistance(float maxDistance) {
        entityData.set(MAX_DISTANCE, maxDistance);
    }

    public float getMaxStepHeight() {
        return entityData.get(MAX_STEP_HEIGHT);
    }

    public void setMaxStepHeight(float maxStepHeight) {
        entityData.set(MAX_STEP_HEIGHT, maxStepHeight);
    }

    public float getTraveledDistance() {
        return entityData.get(TRAVELED_DISTANCE);
    }

    public void setTraveledDistance(float distance) {
        entityData.set(TRAVELED_DISTANCE, distance);
    }

    public float getWidth() {
        return entityData.get(WIDTH);
    }

    public void setWidth(float width) {
        entityData.set(WIDTH, width);
    }

    public float getHeight() {
        return entityData.get(HEIGHT);
    }

    public void setHeight(float height) {
        entityData.set(HEIGHT, height);
    }
}
