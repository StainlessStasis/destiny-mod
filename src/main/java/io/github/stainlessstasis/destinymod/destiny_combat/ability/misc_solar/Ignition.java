package io.github.stainlessstasis.destinymod.destiny_combat.ability.misc_solar;

import io.github.stainlessstasis.destinymod.destiny_classes.DestinyElement;
import io.github.stainlessstasis.destinymod.destiny_combat.CombatUtils;
import io.github.stainlessstasis.destinymod.registry.damage_type.DMDamageTypes;
import io.github.stainlessstasis.destinymod.registry.damage_type.RegisteredDamageType;
import io.github.stainlessstasis.destinymod.registry.datapack.Abilities;
import io.github.stainlessstasis.destinymod.registry.property.AbilityProperties;
import io.github.stainlessstasis.destinymod.registry.property.ability.IgnitionProperty;
import io.github.stainlessstasis.destinymod.network.clientbound.IgnitionEffectsPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.neoforged.neoforge.network.PacketDistributor;
import org.jspecify.annotations.Nullable;

public class Ignition {
    public static void ignite(LivingEntity entity, @Nullable Entity directEntity, @Nullable Entity causingEntity, @Nullable RegisteredDamageType attributedDamageType) {
        if (!(entity.level() instanceof ServerLevel level)) return;

        var ability = Abilities.IGNITION.get(entity);
        var property = ability.getProperty(IgnitionProperty.class).orElseGet(AbilityProperties.IGNITION);
        float damage = ability.damage();
        float range = property.range();

        CombatUtils.triggerExplosion(level, entity.getEyePosition(), range, damage, DMDamageTypes.IGNITION.resourceKey(), DestinyElement.SOLAR, directEntity, causingEntity);
        PacketDistributor.sendToPlayersTrackingEntity(entity, new IgnitionEffectsPacket(entity.getEyePosition().toVector3f(), range));
    }
}
