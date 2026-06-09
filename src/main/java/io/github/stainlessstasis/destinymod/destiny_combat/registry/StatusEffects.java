package io.github.stainlessstasis.destinymod.destiny_combat.registry;

import io.github.stainlessstasis.destinymod.DestinyMod;
import io.github.stainlessstasis.destinymod.data.DestinyModRegistries;
import io.github.stainlessstasis.destinymod.destiny_combat.registry.property.AbilityProperty;
import net.minecraft.resources.ResourceKey;

public class StatusEffects {
    private static <T extends AbilityProperty> RegisteredStatusEffect register(String name) {
        return new RegisteredStatusEffect(ResourceKey.create(DestinyModRegistries.STATUS_EFFECT_REGISTRY_KEY, DestinyMod.id(name)));
    }

    public static final RegisteredStatusEffect NONE = register("none");
    public static final RegisteredStatusEffect SCORCH = register("scorch");
    public static final RegisteredStatusEffect MELTING_POINT = register("melting_point");
    public static final RegisteredStatusEffect SOL_INVICTUS = register("sol_invictus");
}
