package io.github.stainlessstasis.destinymod.destiny_combat.ability;

import io.github.stainlessstasis.destinymod.DestinyMod;
import io.github.stainlessstasis.destinymod.data.DestinyModRegistries;
import net.minecraft.resources.ResourceKey;

public class Abilities {
    private static RegisteredAbility register(String name) {
        return new RegisteredAbility(ResourceKey.create(DestinyModRegistries.ABILITY_REGISTRY_KEY, DestinyMod.id(name)));
    }

    public static final RegisteredAbility NONE = register("none");
    public static final RegisteredAbility THROWING_HAMMER = register("throwing_hammer");
    public static final RegisteredAbility SUNSPOT = register("sunspot");
}
