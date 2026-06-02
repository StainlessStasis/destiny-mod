package io.github.stainlessstasis.destinymod.data;

import io.github.stainlessstasis.destinymod.DestinyMod;
import io.github.stainlessstasis.destinymod.destiny_classes.ability.Ability;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.registries.DataPackRegistryEvent;

@EventBusSubscriber
public class DestinyModRegistries {
    public static final ResourceKey<Registry<Ability>> ABILITY_REGISTRY_KEY = ResourceKey.createRegistryKey(DestinyMod.id("abilities"));

    @SubscribeEvent
    public static void registerDatapackRegistries(DataPackRegistryEvent.NewRegistry event) {
        event.dataPackRegistry(
                ABILITY_REGISTRY_KEY,
                Ability.CODEC, Ability.CODEC,
                builder -> builder.maxId(256)
        );
    }
}
