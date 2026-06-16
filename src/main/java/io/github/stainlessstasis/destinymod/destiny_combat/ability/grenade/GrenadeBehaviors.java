package io.github.stainlessstasis.destinymod.destiny_combat.ability.grenade;

import com.mojang.math.Constants;
import io.github.stainlessstasis.destinymod.destiny_classes.player_equipped.PlayerSubclassData;
import io.github.stainlessstasis.destinymod.entity.DestinyModEntities;
import io.github.stainlessstasis.destinymod.entity.ThermiteGrenadeEntity;
import io.github.stainlessstasis.destinymod.registry.DestinyRegistries;
import io.github.stainlessstasis.destinymod.registry.datapack.Aspects;
import io.github.stainlessstasis.destinymod.registry.property.aspect.ThermalVentProperty;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;

import java.util.Optional;
import java.util.function.Supplier;

public class GrenadeBehaviors {
    public static DeferredHolder<GrenadeBehavior, GrenadeBehavior> register(String id, Supplier<GrenadeBehavior> behavior) {
        return DestinyRegistries.GRENADE_BEHAVIORS_REGISTER.register(id, behavior);
    }

    public static Optional<GrenadeBehavior> getOptional(Identifier id) {
        return DestinyRegistries.GRENADE_BEHAVIORS_REGISTRY.getOptional(id);
    }

    public static final DeferredHolder<GrenadeBehavior, GrenadeBehavior> THERMITE = register("thermite", () -> (entity, context) ->  {
        if (!(entity.level() instanceof ServerLevel level)) return;
        LivingEntity owner = entity.getOwner() instanceof LivingEntity _owner ? _owner : null;

        Vec3 pos = entity.position();
        float yaw = entity.getYRot();
        if (context != null) {
            pos = context.result().getLocation();
            Vec3 vel = context.sourceVelocity();
            if (vel.horizontalDistanceSqr() > Constants.EPSILON) {
                yaw = (float) Math.toDegrees(Mth.atan2(-vel.x, vel.z));
            }
        }

        if (owner instanceof Player player && PlayerSubclassData.isAspectEquipped(player, Aspects.THERMAL_VENT)) {
            boolean success = spawnFromThermalVent(level, pos, yaw, player);
            if (success) return;
        }

        ThermiteGrenadeEntity grenade = new ThermiteGrenadeEntity(DestinyModEntities.THERMITE_GRENADE.get(), level, pos, yaw, owner);
        level.addFreshEntity(grenade);
    });

    private static boolean spawnFromThermalVent(ServerLevel level, Vec3 pos, float initialYaw, Player player) {
        var optional = PlayerSubclassData.getEquippedProperty(player, ThermalVentProperty.class);
        if (optional.isEmpty()) return false;

        var props = optional.get();
        int waveAmount = Math.max(1, 1 + props.bonusWaves());
        float angleBetween = props.angleBetweenWaves();
        float totalAngle = (waveAmount-1) * angleBetween;
        float startYaw = initialYaw - (totalAngle/2);

        for (int i = 0; i < waveAmount; i++) {
            float yaw = startYaw + (i * angleBetween);
            yaw = Mth.wrapDegrees(yaw);

            ThermiteGrenadeEntity grenade = new ThermiteGrenadeEntity(DestinyModEntities.THERMITE_GRENADE.get(), level, pos, yaw, player);
            grenade.setHasThermalVent(true);
            grenade.setDamageMultiplier(props.waveDamageMultiplier());
            grenade.setScorchMultiplier(props.waveScorchMultiplier());
            level.addFreshEntity(grenade);
        }

        return true;
    }

    public static void registerRegistry(IEventBus bus) {
        DestinyRegistries.GRENADE_BEHAVIORS_REGISTER.register(bus);
    }
}
