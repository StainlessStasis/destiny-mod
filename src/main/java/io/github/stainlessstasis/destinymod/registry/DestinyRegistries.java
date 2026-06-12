package io.github.stainlessstasis.destinymod.registry;

import io.github.stainlessstasis.destinymod.DestinyMod;
import io.github.stainlessstasis.destinymod.destiny_combat.ability.grenade.GrenadeBehavior;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NewRegistryEvent;
import net.neoforged.neoforge.registries.RegistryBuilder;

@EventBusSubscriber
public class DestinyRegistries {
    private static final ResourceKey<Registry<GrenadeBehavior>> GRENADE_BEHAVIORS_KEY = ResourceKey.createRegistryKey(DestinyMod.id("grenade_behaviors"));
    private static final Registry<GrenadeBehavior> GRENADE_BEHAVIORS_REGISTRY = new RegistryBuilder<>(GRENADE_BEHAVIORS_KEY)
            .sync(true)
            .defaultKey(DestinyMod.id("none"))
            .maxId(256)
            .create();
    public static final DeferredRegister<GrenadeBehavior> GRENADE_BEHAVIORS_REGISTER =
            DeferredRegister.create(GRENADE_BEHAVIORS_KEY, DestinyMod.MODID);

    @SubscribeEvent
    public static void registerRegistries(NewRegistryEvent event) {
        event.register(GRENADE_BEHAVIORS_REGISTRY);
    }
}
