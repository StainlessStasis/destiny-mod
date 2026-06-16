package io.github.stainlessstasis.destinymod.registry.damage_type;

import io.github.stainlessstasis.destinymod.DestinyMod;
import io.github.stainlessstasis.destinymod.destiny_combat.ability.AbilityType;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagKey;
import net.minecraft.world.damagesource.DamageType;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public class DMDamageTypes {
    private static final Set<RegisteredDamageType> DAMAGE_TYPES = new HashSet<>();

    private static RegisteredDamageType register(String name, AbilityType abilityType, @Nullable Collection<TagKey<DamageType>> additionalTags) {
        var resourceKey = ResourceKey.create(Registries.DAMAGE_TYPE, DestinyMod.id(name));
        Set<TagKey<DamageType>> additionalTagsSet = new HashSet<>();
        if (additionalTags != null) additionalTagsSet.addAll(additionalTags);
        RegisteredDamageType damageType = new RegisteredDamageType(resourceKey, abilityType, additionalTagsSet);
        DAMAGE_TYPES.add(damageType);
        return damageType;
    }
    private static RegisteredDamageType register(String name, AbilityType abilityType) {
        return register(name, abilityType, null);
    }
    private static RegisteredDamageType register(String name) {
        return register(name, AbilityType.NONE, null);
    }

    public static Set<RegisteredDamageType> getDamageTypes() {
        return Set.copyOf(DAMAGE_TYPES);
    }

    public static final RegisteredDamageType NONE = register("none");
    public static final RegisteredDamageType THROWING_HAMMER = register("throwing_hammer", AbilityType.MELEE);
    public static final RegisteredDamageType THERMITE_GRENADE = register("thermite_grenade", AbilityType.GRENADE);
    public static final RegisteredDamageType SCORCH = register("scorch", AbilityType.DEBUFF);
    public static final RegisteredDamageType IGNITION = register("ignition", AbilityType.PASSIVE);
    public static final RegisteredDamageType SUNSPOT = register("sunspot", AbilityType.PASSIVE);

    public static class Tags {
        public static final TagKey<DamageType> IS_SUBCLASS_ABILITY = TagKey.create(Registries.DAMAGE_TYPE, DestinyMod.id("is_subclass_ability"));
        public static final TagKey<DamageType> IS_ABILITY = TagKey.create(Registries.DAMAGE_TYPE, DestinyMod.id("is_ability"));
        public static final TagKey<DamageType> IS_MELEE_ABILITY = TagKey.create(Registries.DAMAGE_TYPE, DestinyMod.id("is_melee_ability"));
        public static final TagKey<DamageType> IS_GRENADE_ABILITY = TagKey.create(Registries.DAMAGE_TYPE, DestinyMod.id("is_grenade_ability"));
        public static final TagKey<DamageType> IS_CLASS_ABILITY = TagKey.create(Registries.DAMAGE_TYPE, DestinyMod.id("is_class_ability"));
        public static final TagKey<DamageType> IS_SUPER_ABILITY = TagKey.create(Registries.DAMAGE_TYPE, DestinyMod.id("is_super_ability"));
        public static final TagKey<DamageType> IS_DEBUFF = TagKey.create(Registries.DAMAGE_TYPE, DestinyMod.id("is_debuff"));
    }
}
