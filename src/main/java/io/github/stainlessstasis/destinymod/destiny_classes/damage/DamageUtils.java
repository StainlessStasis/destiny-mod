package io.github.stainlessstasis.destinymod.destiny_classes.damage;

import io.github.stainlessstasis.destinymod.destiny_classes.DestinyElement;
import io.github.stainlessstasis.destinymod.mixin_api.DestinyModDamageSource;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import org.jspecify.annotations.Nullable;

public class DamageUtils {
    public static DamageSource createDamageSource(ResourceKey<DamageType> type, Level level, @Nullable Entity directEntity, @Nullable Entity causingEntity, DestinyElement element) {
        var vanillaSources = level.damageSources();
        DamageSource source = new DamageSource(vanillaSources.damageTypes.getOrThrow(type), directEntity, causingEntity);
        ((DestinyModDamageSource)source).destinymod$setElement(element);
        return source;
    }
}
