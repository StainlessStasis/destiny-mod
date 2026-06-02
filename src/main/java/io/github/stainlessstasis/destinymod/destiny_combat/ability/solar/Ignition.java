package io.github.stainlessstasis.destinymod.destiny_combat.ability.solar;

import io.github.stainlessstasis.destinymod.destiny_classes.DestinyElement;
import io.github.stainlessstasis.destinymod.destiny_combat.damage.DestinyDamageBuilder;
import io.github.stainlessstasis.destinymod.destiny_combat.damage.DMDamageTypes;
import io.github.stainlessstasis.destinymod.network.clientbound.IgnitionEffectsPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.network.PacketDistributor;
import org.jspecify.annotations.Nullable;

import java.util.List;
import java.util.function.Predicate;

public class Ignition {
    public static final float RANGE = 5f;
    public static final float RANGE_SQUARED = RANGE*RANGE;
    public static final float DAMAGE = 25f;

    public static void ignite(LivingEntity entity, @Nullable Entity directEntity, @Nullable Entity causingEntity) {
        if (!(entity.level() instanceof ServerLevel level)) return;

        Vec3 ignitionPos = entity.getEyePosition();
        AABB searchArea = AABB.ofSize(entity.getEyePosition(), 1f, 1f, 1f).inflate(RANGE);
        Predicate<LivingEntity> filter = target -> target.distanceToSqr(ignitionPos) <= RANGE_SQUARED;
        List<LivingEntity> victims = level.getEntitiesOfClass(LivingEntity.class, searchArea, filter);

        for (LivingEntity victim : victims) {
            float distSq = (float) victim.distanceToSqr(ignitionPos);
            float falloff = 1f - (distSq / RANGE_SQUARED);
            falloff = Math.clamp(falloff, 0f, 1f);
            float damage = DAMAGE * falloff;
            if (damage <= 0.01f) continue;
            DestinyDamageBuilder.create(DMDamageTypes.IGNITION, victim)
                    .directSource(directEntity)
                    .attacker(causingEntity)
                    .element(DestinyElement.SOLAR)
                    .damage(damage)
                    .invulnerabilityTicks(0)
                    .knockback(false)
                    .execute();
        }

        PacketDistributor.sendToPlayersTrackingEntity(entity, new IgnitionEffectsPacket(entity.getEyePosition().toVector3f()));
    }
}
