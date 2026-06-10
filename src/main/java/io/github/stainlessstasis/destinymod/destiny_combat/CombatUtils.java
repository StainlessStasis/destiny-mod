package io.github.stainlessstasis.destinymod.destiny_combat;

import io.github.stainlessstasis.destinymod.destiny_classes.DestinyElement;
import io.github.stainlessstasis.destinymod.destiny_combat.damage.DestinyDamageBuilder;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jspecify.annotations.Nullable;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Predicate;

public class CombatUtils {
    public static void triggerExplosion(ServerLevel level, Vec3 pos, float radius, float maxDamage, ResourceKey<DamageType> damageType, DestinyElement element) {
        triggerExplosion(level, pos, radius, maxDamage, damageType, element, null, null, null);
    }
    public static void triggerExplosion(ServerLevel level, Vec3 pos, float radius, float maxDamage, ResourceKey<DamageType> damageType, DestinyElement element, @Nullable Entity directEntity, @Nullable Entity causingEntity) {
        triggerExplosion(level, pos, radius, maxDamage, damageType, element, directEntity, causingEntity, null);
    }
    public static void triggerExplosion(ServerLevel level, Vec3 pos, float radius, float maxDamage,
                                        ResourceKey<DamageType> damageType, DestinyElement element,
                                        @Nullable Entity directEntity, @Nullable Entity causingEntity,
                                        @Nullable Consumer<LivingEntity> victimLogic)
    {
        float radiusSq = radius * radius;
        AABB searchArea = AABB.ofSize(pos, 1f, 1f, 1f).inflate(radius);
        Predicate<LivingEntity> filter = target -> target.distanceToSqr(pos) <= radiusSq;

        List<LivingEntity> victims = level.getEntitiesOfClass(LivingEntity.class, searchArea, filter);

        for (LivingEntity victim : victims) {
            float distSq = (float) victim.distanceToSqr(pos);
            float falloff = Math.clamp(1f - (distSq / radiusSq), 0f, 1f);
            float damage = maxDamage * falloff;

            if (damage <= 0.01f) continue;

            DestinyDamageBuilder.create(damageType, victim)
                    .directSource(directEntity)
                    .attacker(causingEntity)
                    .element(element)
                    .damage(damage)
                    .invulnerabilityTicks(0)
                    .knockback(false)
                    .execute();

            if (victimLogic != null) {
                victimLogic.accept(victim);
            }
        }
    }
}
