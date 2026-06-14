package io.github.stainlessstasis.destinymod.registry.datapack;

import io.github.stainlessstasis.destinymod.DestinyMod;
import io.github.stainlessstasis.destinymod.registry.DestinyRegistries;
import net.minecraft.resources.ResourceKey;

public class Abilities {
    private static RegisteredAbility register(String name) {
        return new RegisteredAbility(ResourceKey.create(DestinyRegistries.ABILITY_REGISTRY_KEY, DestinyMod.id(name)));
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
