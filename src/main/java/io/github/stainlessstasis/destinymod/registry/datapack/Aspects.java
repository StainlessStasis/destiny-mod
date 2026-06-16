package io.github.stainlessstasis.destinymod.registry.datapack;

import io.github.stainlessstasis.destinymod.DestinyMod;
import io.github.stainlessstasis.destinymod.registry.DestinyRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class Aspects {
    private static final Map<Identifier, RegisteredAspect> BY_ID = new HashMap<>();

    private static RegisteredAspect register(String name) {
        Identifier id = DestinyMod.id(name);
        RegisteredAspect aspect = new RegisteredAspect(ResourceKey.create(DestinyRegistries.ASPECT_REGISTRY_KEY, id));
        BY_ID.put(id, aspect);
        return aspect;
    }

    public static Set<RegisteredAspect> getAll() {
        return BY_ID.values().stream().collect(Collectors.toUnmodifiableSet());
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
    public static final RegisteredAspect BLAZING_PYRE = register("blazing_pyre");
    public static final RegisteredAspect THERMAL_VENT = register("thermal_vent");
}
