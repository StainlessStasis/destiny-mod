package io.github.stainlessstasis.destinymod.destiny_classes.damage;

import io.github.stainlessstasis.destinymod.DestinyMod;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagKey;
import net.minecraft.world.damagesource.DamageType;

public class DestinyModDamageTypes {
    private static ResourceKey<DamageType> register(String name) {
        return ResourceKey.create(Registries.DAMAGE_TYPE, DestinyMod.id(name));
    }

    public static final ResourceKey<DamageType> MELEE_ABILITY = register("melee_ability");
    public static final ResourceKey<DamageType> GRENADE_ABILITY = register("grenade_ability");
    public static final ResourceKey<DamageType> CLASS_ABILITY = register("class_ability");
    public static final ResourceKey<DamageType> SUPER = register("super");
    public static final ResourceKey<DamageType> SCORCH = register("scorch");
    public static final ResourceKey<DamageType> IGNITION = register("ignition");
    public static final ResourceKey<DamageType> SUNSPOT = register("sunspot");

    public static class Tags {
        public static final TagKey<DamageType> IS_SUBCLASS_ABILITY = TagKey.create(Registries.DAMAGE_TYPE, DestinyMod.id("is_subclass_ability"));
        public static final TagKey<DamageType> IS_ABILITY = TagKey.create(Registries.DAMAGE_TYPE, DestinyMod.id("is_ability"));
        public static final TagKey<DamageType> IS_DEBUFF = TagKey.create(Registries.DAMAGE_TYPE, DestinyMod.id("is_debuff"));
    }
}
