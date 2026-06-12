package io.github.stainlessstasis.destinymod.destiny_combat.ability.grenade;

import net.minecraft.server.level.ServerLevel;

import java.util.HashMap;
import java.util.Map;

public class GrenadeBehaviors {
    private static final Map<String, GrenadeBehavior> REGISTRY = new HashMap<>();

    public static final GrenadeBehavior THERMITE = (grenade, context) -> {
        if (!(grenade.level() instanceof ServerLevel serverLevel)) return;
        System.out.println("THERMITE GRENADE");
    };

    static {
        REGISTRY.put("thermite", THERMITE);
    }

    public static GrenadeBehavior get(String type) {
        return REGISTRY.getOrDefault(type, (g, c) -> {});
    }
}
