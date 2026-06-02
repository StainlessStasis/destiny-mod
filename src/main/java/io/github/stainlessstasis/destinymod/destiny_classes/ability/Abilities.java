package io.github.stainlessstasis.destinymod.destiny_classes.ability;

import io.github.stainlessstasis.destinymod.DestinyMod;
import io.github.stainlessstasis.destinymod.data.DestinyModRegistries;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceKey;

import javax.annotation.Nullable;
import java.util.Optional;

public class Abilities {
    private static ResourceKey<Ability> register(String name) {
        return ResourceKey.create(DestinyModRegistries.ABILITY_REGISTRY_KEY, DestinyMod.id(name));
    }

    public static @Nullable Ability get(ResourceKey<Ability> key, RegistryAccess access) {
        Optional<Registry<Ability>> registryOptional = access.lookup(DestinyModRegistries.ABILITY_REGISTRY_KEY);
        if (registryOptional.isEmpty()) {
            return null;
        }
        var reference = registryOptional.get().get(key);
        return reference.map(Holder.Reference::value).orElse(null);

    }

    public static final ResourceKey<Ability> NONE = register("none");
    public static final ResourceKey<Ability> THROWING_HAMMER = register("throwing_hammer");
}
