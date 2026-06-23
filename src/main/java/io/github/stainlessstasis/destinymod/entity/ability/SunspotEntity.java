package io.github.stainlessstasis.destinymod.entity.ability;

import io.github.stainlessstasis.destinymod.client.effects.ClientAudioAndVFX;
import io.github.stainlessstasis.destinymod.registry.datapack.Abilities;
import io.github.stainlessstasis.destinymod.destiny_combat.damage.DestinyDamageBuilder;
import io.github.stainlessstasis.destinymod.registry.damage_type.DMDamageTypes;
import io.github.stainlessstasis.destinymod.destiny_combat.status_effect.StatusEffectManager;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.*;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SunspotEntity extends DestinyAbilityEntity {
    public static final float RADIUS = 2f;
    public static final int HIT_INTERVAL = 10;
    public static final int MAX_LIFETIME = 160;

    private final Map<LivingEntity, Integer> attackCooldowns = new HashMap<>();

    private SunspotEntity(EntityType<? extends DestinyAbilityEntity> type, Level level) {
        super(type, level, Abilities.SUNSPOT.get(level));
    }

    public static SunspotEntity createDefault(EntityType<? extends DestinyAbilityEntity> entityType, Level level) {
        return new SunspotEntity(entityType, level);
    }

    public SunspotEntity(EntityType<? extends DestinyAbilityEntity> type, Level level, Vec3 pos, @Nullable LivingEntity owner) {
        this(type, level);
        setPos(pos);
        setOwner(owner);
    }

    @Override
    public void tick() {
        super.tick();

        if (tickCount >= MAX_LIFETIME) {
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
        LivingEntity owner = getOwner();

        List<LivingEntity> victims = level.getEntitiesOfClass(LivingEntity.class, getBoundingBox());
        for (LivingEntity victim : victims) {
            if (attackCooldowns.containsKey(victim)) continue;
            if (!victim.isAlive()) continue;

            if (victim == owner) {
                StatusEffectManager.applySolInvictus(owner);
                continue;
            }

            attackCooldowns.put(victim, tickCount + HIT_INTERVAL);

            DestinyDamageBuilder.create(DMDamageTypes.SUNSPOT.resourceKey(), victim)
                    .directSource(owner)
                    .attacker(owner)
                    .element(ability.element())
                    .damage(ability.damage())
                    .invulnerabilityTicks(0)
                    .knockback(false)
                    .executeDamage();
            StatusEffectManager.applyScorch(victim, owner, DMDamageTypes.SUNSPOT, ability.scorch());
        }
    }

    private void tickClient() {
        ClientAudioAndVFX.sunspot(level(), position(), random, tickCount);
    }

    @Override
    public @NonNull EntityDimensions getDimensions(@NonNull Pose pose) {
        return EntityDimensions.fixed(RADIUS*2, 2.5f);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.@NonNull Builder builder) {}
}
