package io.github.stainlessstasis.destinymod.destiny_combat.ability.grenade;

import io.github.stainlessstasis.destinymod.registry.DestinyRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
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
        System.out.println("THERMITE GRENADE");
    });

    public static void registerRegistry(IEventBus bus) {
        DestinyRegistries.GRENADE_BEHAVIORS_REGISTER.register(bus);
    }
}
