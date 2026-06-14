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

public class ThermiteGrenadeEntity extends AbstractAbilityEntity {
    private static final EntityDataAccessor<Integer> CURRENT_PULSE = SynchedEntityData.defineId(ThermiteGrenadeEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> MAX_PULSES = SynchedEntityData.defineId(ThermiteGrenadeEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> PULSE_INTERVAL = SynchedEntityData.defineId(ThermiteGrenadeEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Float> DISTANCE_PER_TICK = SynchedEntityData.defineId(ThermiteGrenadeEntity.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Float> MAX_DISTANCE = SynchedEntityData.defineId(ThermiteGrenadeEntity.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Float> MAX_STEP_HEIGHT = SynchedEntityData.defineId(ThermiteGrenadeEntity.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Float> TRAVELED_DISTANCE = SynchedEntityData.defineId(ThermiteGrenadeEntity.class, EntityDataSerializers.FLOAT);
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
            marchPulsePath(lastObservedDistance, currentDist, pos -> {
                level().addParticle(
                        ParticleTypes.FLAME,
                        pos.x, pos.y + 0.1, pos.z,
                        0.0, 0.05, 0.0
                );
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
        AABB damageArea = new AABB(
                pos.x - 0.75, pos.y - 0.5, pos.z - 0.75,
                pos.x + 0.75, pos.y + 1.5, pos.z + 0.75
        );

        List<LivingEntity> targets = CombatUtils.getEntitiesInArea(damageArea, level(), LivingEntity.class, getOwner(), hitEntities, null);
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
}
