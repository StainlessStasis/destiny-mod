package io.github.stainlessstasis.destinymod.destiny_combat;

import io.github.stainlessstasis.destinymod.destiny_classes.DestinyElement;
import io.github.stainlessstasis.destinymod.destiny_combat.damage.DestinyDamageBuilder;
import io.github.stainlessstasis.destinymod.registry.damage_type.RegisteredDamageType;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.OwnableEntity;
import net.minecraft.world.entity.TraceableEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jspecify.annotations.Nullable;

import java.util.Collection;
import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.function.Predicate;

public class CombatUtils {
    public static void triggerExplosion(ServerLevel level, Vec3 pos, float radius, float maxDamage, ResourceKey<DamageType> damageType, DestinyElement element) {
        triggerExplosion(level, pos, radius, maxDamage, damageType, element, null, null, null, null);
    }
    public static void triggerExplosion(ServerLevel level, Vec3 pos, float radius, float maxDamage, ResourceKey<DamageType> damageType, DestinyElement element, @Nullable Entity directEntity, @Nullable Entity causingEntity) {
        triggerExplosion(level, pos, radius, maxDamage, damageType, element, directEntity, causingEntity, null, null);
    }
    public static void triggerExplosion(ServerLevel level, Vec3 pos, float radius, float maxDamage,
                                        ResourceKey<DamageType> damageType, DestinyElement element,
                                        @Nullable Entity directEntity, @Nullable Entity causingEntity,
                                        @Nullable RegisteredDamageType attributedDamageType, @Nullable Consumer<LivingEntity> victimLogic)
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
                    .attributedDamageType(attributedDamageType)
                    .directSource(directEntity)
                    .attacker(causingEntity)
                    .element(element)
                    .damage(damage)
                    .invulnerabilityTicks(0)
                    .knockback(false)
                    .executeDamage();

            if (victimLogic != null) {
                victimLogic.accept(victim);
            }
        }
    }

    /**
     * Gets entities in the specified AABB.
     * If an owner is provided, then any entities sharing the same owner will be excluded.
     * If a collection of already hit entities is provided, then any entities in the list will be excluded.
     */
    public static <T extends Entity> List<T> getEntitiesInArea(AABB area, Level level, Class<T> clazz, @Nullable Entity owner, @Nullable Collection<UUID> alreadyHit, @Nullable Predicate<T> filter) {
        return  level.getEntitiesOfClass(clazz, area,
                entity -> {
                    if (owner != null) {
                        if (entity == owner) return false;
                        if (entity instanceof OwnableEntity ownable && ownable.getOwner() == owner) return false;
                        if (entity instanceof TraceableEntity traceable && traceable.getOwner() == owner) return false;
                    }
                    if (alreadyHit != null && alreadyHit.contains(entity.getUUID())) return false;
                    if (filter != null) return filter.test(entity);
                    return true;
                }
        );
    }
}
