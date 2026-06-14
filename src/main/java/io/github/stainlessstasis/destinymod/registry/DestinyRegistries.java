package io.github.stainlessstasis.destinymod.registry;

import io.github.stainlessstasis.destinymod.DestinyMod;
import io.github.stainlessstasis.destinymod.destiny_combat.ability.Ability;
import io.github.stainlessstasis.destinymod.destiny_combat.ability.Aspect;
import io.github.stainlessstasis.destinymod.destiny_combat.ability.grenade.GrenadeBehavior;
import io.github.stainlessstasis.destinymod.destiny_combat.status_effect.StatusEffect;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.registries.DataPackRegistryEvent;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NewRegistryEvent;
import net.neoforged.neoforge.registries.RegistryBuilder;

@EventBusSubscriber
public class DestinyRegistries {
    public static final ResourceKey<Registry<Ability>> ABILITY_REGISTRY_KEY = ResourceKey.createRegistryKey(DestinyMod.id("abilities"));
    public static final ResourceKey<Registry<Aspect>> ASPECT_REGISTRY_KEY = ResourceKey.createRegistryKey(DestinyMod.id("aspects"));
    public static final ResourceKey<Registry<StatusEffect>> STATUS_EFFECT_REGISTRY_KEY = ResourceKey.createRegistryKey(DestinyMod.id("status_effects"));

    public static final ResourceKey<Registry<GrenadeBehavior>> GRENADE_BEHAVIORS_KEY = ResourceKey.createRegistryKey(DestinyMod.id("grenade_behaviors"));
    public static final Registry<GrenadeBehavior> GRENADE_BEHAVIORS_REGISTRY = new RegistryBuilder<>(GRENADE_BEHAVIORS_KEY)
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

    @SubscribeEvent
    public static void registerDatapackRegistries(DataPackRegistryEvent.NewRegistry event) {
        event.dataPackRegistry(
                ABILITY_REGISTRY_KEY,
                Ability.CODEC, Ability.CODEC,
                builder -> builder.maxId(256)
        );
        event.dataPackRegistry(
                ASPECT_REGISTRY_KEY,
                Aspect.CODEC, Aspect.CODEC,
                builder -> builder.maxId(256)
        );
        event.dataPackRegistry(
                STATUS_EFFECT_REGISTRY_KEY,
                StatusEffect.CODEC, StatusEffect.CODEC,
                builder -> builder.maxId(256)
        );
    }
}
