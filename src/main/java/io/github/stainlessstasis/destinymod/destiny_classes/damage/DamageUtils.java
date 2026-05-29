package io.github.stainlessstasis.destinymod.destiny_classes.damage;

import io.github.stainlessstasis.destinymod.destiny_classes.DestinyElement;
import io.github.stainlessstasis.destinymod.mixin_api.DestinyModDamageSource;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import org.jspecify.annotations.Nullable;

public class DamageUtils {
    public static DamageSource createDamageSource(ResourceKey<DamageType> damageTypeKey, Level level, @Nullable Entity directEntity, @Nullable Entity causingEntity, DestinyElement element) {
        var vanillaSources = level.damageSources();
        DamageSource source = new DamageSource(vanillaSources.damageTypes.getOrThrow(damageTypeKey), directEntity, causingEntity);
        ((DestinyModDamageSource)source).destinymod$setElement(element);
        return source;
    }

    /**
     * Does nothing when called from the client
     * @param directEntity The entity directly causing the damage (e.g. a Throwing Hammer, or Arrow)
     * @param causingEntity The root cause entity of the damage (e.g. the player who threw the Throwing Hammer, or skeleton who shot the arrow)
     */
    public static boolean hurt(ResourceKey<DamageType> damageTypeKey, LivingEntity victim, @Nullable Entity directEntity, @Nullable Entity causingEntity, DestinyElement element, float baseDamage) {
        if (victim.level().isClientSide()) return false;
        DamageSource source = createDamageSource(damageTypeKey, victim.level(), directEntity, causingEntity, element);
        return hurt(source, victim, baseDamage);
    }

    public static boolean hurt(DamageSource damageSource, LivingEntity victim, float baseDamage) {
        if (!(victim.level() instanceof ServerLevel level)) return false;
        boolean wasHurt = victim.hurtServer(level, damageSource, baseDamage);
        victim.invulnerableTime = 0;
        return wasHurt;
    }
}
