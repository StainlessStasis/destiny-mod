package io.github.stainlessstasis.destinymod.entity;

import io.github.stainlessstasis.destinymod.destiny_classes.DestinyElement;
import io.github.stainlessstasis.destinymod.destiny_classes.damage.DestinyDamageBuilder;
import io.github.stainlessstasis.destinymod.destiny_classes.damage.DestinyModDamageTypes;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.phys.Vec3;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SunspotEntity extends Entity implements TraceableEntity {
    public static final float RADIUS = 1.5f;
    public static final float RADIUS_SQUARED = RADIUS*RADIUS;
    public static final int HIT_INTERVAL = 10;

    private int maxLifetime = 160;
    private final Map<LivingEntity, Integer> attackCooldowns = new HashMap<>();
    private @Nullable EntityReference<LivingEntity> owner;

    private SunspotEntity(EntityType<? extends Entity> type, Level level) {
        super(type, level);
        noPhysics = true;
        refreshDimensions();
    }

    public static SunspotEntity createDefault(EntityType<? extends Entity> entityType, Level level) {
        return new SunspotEntity(entityType, level);
    }

    public SunspotEntity(EntityType<? extends Entity> type, Level level, Vec3 pos, @Nullable LivingEntity owner) {
        this(type, level);
        setPos(pos);
        setOwner(owner);
    }

    @Override
    public void tick() {
        super.tick();

        if (tickCount >= maxLifetime) {
            discard();
            return;
        }

        if (level() instanceof ServerLevel serverLevel) {
            tickServer(serverLevel);
        } else {
            tickClient();
        }
    }

    private void tickServer(ServerLevel level) {
        attackCooldowns.entrySet().removeIf(entry -> tickCount >= entry.getValue());

        List<LivingEntity> victims = level.getEntitiesOfClass(LivingEntity.class, getBoundingBox());
        for (LivingEntity victim : victims) {
            if (attackCooldowns.containsKey(victim)) continue;
            if (!victim.isAlive() || victim == getOwner()) continue;
            if (victim.distanceToSqr(position()) > RADIUS_SQUARED) continue;

            attackCooldowns.put(victim, tickCount + HIT_INTERVAL);

            Entity owner = getOwner();
            // TODO: sunspot damage type
            DestinyDamageBuilder.create(DestinyModDamageTypes.SCORCH, victim)
                    .directSource(owner)
                    .attacker(owner)
                    .element(DestinyElement.SOLAR)
                    .damage(100f)
                    .invulnerabilityTicks(0)
                    .knockback(false)
                    .execute();
        }
    }

    private void tickClient() {

    }

    public void setOwner(@Nullable LivingEntity owner) {
        this.owner = EntityReference.of(owner);
    }
    @Override
    public @Nullable Entity getOwner() {
        return EntityReference.getLivingEntity(owner, level());
    }

    @Override
    public @NonNull EntityDimensions getDimensions(@NonNull Pose pose) {
        return EntityDimensions.fixed(RADIUS*2, 0.5f);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.@NonNull Builder builder) {}
    @Override
    public boolean hurtServer(@NonNull ServerLevel serverLevel, @NonNull DamageSource damageSource, float v) {return false;}
    @Override
    protected void readAdditionalSaveData(@NonNull ValueInput valueInput) {
        discard();
    }
    @Override
    protected void addAdditionalSaveData(@NonNull ValueOutput valueOutput) {}
}
