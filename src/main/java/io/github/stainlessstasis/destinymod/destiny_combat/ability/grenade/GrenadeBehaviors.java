package io.github.stainlessstasis.destinymod.destiny_combat.ability.grenade;

import io.github.stainlessstasis.destinymod.entity.DestinyModEntities;
import io.github.stainlessstasis.destinymod.entity.ThermiteGrenadeEntity;
import io.github.stainlessstasis.destinymod.registry.DestinyRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
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
        Vec3 pos = context != null ? context.result().getLocation() : entity.position();
        LivingEntity owner = entity.getOwner() instanceof LivingEntity _owner ? _owner : null;
        ThermiteGrenadeEntity grenade = new ThermiteGrenadeEntity(DestinyModEntities.THERMITE_GRENADE.get(), level, pos, owner);
        level.addFreshEntity(grenade);
    });

    public static void registerRegistry(IEventBus bus) {
        DestinyRegistries.GRENADE_BEHAVIORS_REGISTER.register(bus);
    }
}
