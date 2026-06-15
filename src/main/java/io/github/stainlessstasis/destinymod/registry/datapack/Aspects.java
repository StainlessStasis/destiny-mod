package io.github.stainlessstasis.destinymod.registry.datapack;

import io.github.stainlessstasis.destinymod.DestinyMod;
import io.github.stainlessstasis.destinymod.registry.DestinyRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;

import java.util.HashMap;
import java.util.Map;

public class Aspects {
    private static final Map<Identifier, RegisteredAspect> BY_ID = new HashMap<>();

    private static RegisteredAspect register(String name) {
        Identifier id = DestinyMod.id(name);
        RegisteredAspect aspect = new RegisteredAspect(ResourceKey.create(DestinyRegistries.ASPECT_REGISTRY_KEY, id));
        BY_ID.put(id, aspect);
        return aspect;
    }

    public static RegisteredAspect get(Identifier id) {
        return BY_ID.getOrDefault(id, NONE);
    }

    public static RegisteredAspect get(String name) {
        return get(DestinyMod.id(name));
    }

    public static final RegisteredAspect NONE = register("none");
    public static final RegisteredAspect MELTING_POINT = register("melting_point");
    public static final RegisteredAspect HEATSEEKER = register("heatseeker");
    public static final RegisteredAspect ANVIL_DROP = register("anvil_drop");
    public static final RegisteredAspect REKINDLED_FLAMES = register("rekindled_flames");
}
