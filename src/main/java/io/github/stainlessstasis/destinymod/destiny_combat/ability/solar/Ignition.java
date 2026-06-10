package io.github.stainlessstasis.destinymod.destiny_combat.ability.solar;

import io.github.stainlessstasis.destinymod.destiny_classes.DestinyElement;
import io.github.stainlessstasis.destinymod.destiny_combat.CombatUtils;
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
    public static final float DAMAGE = 25f;

    public static void ignite(LivingEntity entity, @Nullable Entity directEntity, @Nullable Entity causingEntity) {
        if (!(entity.level() instanceof ServerLevel level)) return;
        CombatUtils.triggerExplosion(level, entity.getEyePosition(), RANGE, DAMAGE, DMDamageTypes.IGNITION, DestinyElement.SOLAR, directEntity, causingEntity);
        PacketDistributor.sendToPlayersTrackingEntity(entity, new IgnitionEffectsPacket(entity.getEyePosition().toVector3f()));
    }
}
