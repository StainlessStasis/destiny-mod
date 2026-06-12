package io.github.stainlessstasis.destinymod.entity;

import io.github.stainlessstasis.destinymod.client.effects.ClientAudioAndVFX;
import io.github.stainlessstasis.destinymod.registry.datapack.Abilities;
import io.github.stainlessstasis.destinymod.destiny_combat.ability.Ability;
import io.github.stainlessstasis.destinymod.destiny_combat.ability.DestinyAbility;
import io.github.stainlessstasis.destinymod.destiny_combat.damage.DestinyDamageBuilder;
import io.github.stainlessstasis.destinymod.destiny_combat.damage.DMDamageTypes;
import io.github.stainlessstasis.destinymod.destiny_combat.status_effect.StatusEffectManager;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SunspotEntity extends AbstractAbilityEntity {
    public static final float RADIUS = 2f;
    public static final int HIT_INTERVAL = 10;
    public static final int MAX_LIFETIME = 160;

    private final Map<LivingEntity, Integer> attackCooldowns = new HashMap<>();

    private SunspotEntity(EntityType<? extends Entity> type, Level level) {
        super(type, level, Abilities.SUNSPOT.get(level));
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

            DestinyDamageBuilder.create(DMDamageTypes.SUNSPOT, victim)
                    .directSource(owner)
                    .attacker(owner)
                    .element(ability.element())
                    .damage(ability.damage())
                    .invulnerabilityTicks(0)
                    .knockback(false)
                    .execute();
            StatusEffectManager.applyScorch(victim, owner, ability.scorch());
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
