package io.github.stainlessstasis.destinymod.registry.datapack;

import io.github.stainlessstasis.destinymod.DestinyMod;
import io.github.stainlessstasis.destinymod.data.DestinyModRegistries;
import net.minecraft.resources.ResourceKey;

public class Aspects {
    private static RegisteredAspect register(String name) {
        return new RegisteredAspect(ResourceKey.create(DestinyModRegistries.ASPECT_REGISTRY_KEY, DestinyMod.id(name)));
    }

    public static final RegisteredAspect NONE = register("none");
    public static final RegisteredAspect MELTING_POINT = register("melting_point");
    public static final RegisteredAspect HEATSEEKER = register("heatseeker");
    public static final RegisteredAspect ANVIL_DROP = register("anvil_drop");
}
