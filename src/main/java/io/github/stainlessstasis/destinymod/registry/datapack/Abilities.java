package io.github.stainlessstasis.destinymod.registry.datapack;

import io.github.stainlessstasis.destinymod.DestinyMod;
import io.github.stainlessstasis.destinymod.registry.DestinyRegistries;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class Abilities {
    private static final Map<Identifier, RegisteredAbility> BY_ID = new HashMap<>();

    private static RegisteredAbility register(String name) {
        Identifier id = DestinyMod.id(name);
        RegisteredAbility ability = new RegisteredAbility(ResourceKey.create(DestinyRegistries.ABILITY_REGISTRY_KEY, id));
        BY_ID.put(id, ability);
        return ability;
    }

    public static RegisteredAbility get(Identifier id) {
        return BY_ID.getOrDefault(id, NONE);
    }

    public static RegisteredAbility get(String name) {
        return get(DestinyMod.id(name));
    }

    public static final RegisteredAbility NONE = register("none");

    // MAIN ABILITIES
    public static final RegisteredAbility THROWING_HAMMER = register("throwing_hammer");
    public static final RegisteredAbility THERMITE_GRENADE = register("thermite_grenade");

    // PASSIVES
    public static final RegisteredAbility SOL_INVICTUS = register("sol_invictus");

    // MISC (sub abilities or abilities triggered via status effect)
    public static final RegisteredAbility SUNSPOT = register("sunspot");
    public static final RegisteredAbility IGNITION = register("ignition");
}
