package io.github.stainlessstasis.destinymod.data;

import io.github.stainlessstasis.destinymod.destiny_combat.ability.AbilityType;
import io.github.stainlessstasis.destinymod.registry.damage_type.DMDamageTypes;
import io.github.stainlessstasis.destinymod.registry.damage_type.RegisteredDamageType;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.DamageTypeTagsProvider;
import net.minecraft.tags.TagKey;
import net.minecraft.world.damagesource.DamageType;
import org.jspecify.annotations.NonNull;

import java.util.concurrent.CompletableFuture;

public class DMDamageTypeTagProvider extends DamageTypeTagsProvider {
    public DMDamageTypeTagProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider, String modId) {
        super(output, lookupProvider, modId);
    }

    @Override
    protected void addTags(HolderLookup.@NonNull Provider registries) {
        for (RegisteredDamageType registeredDamageType : DMDamageTypes.getDamageTypes()) {
            var resourceKey = registeredDamageType.resourceKey();
            AbilityType abilityType = registeredDamageType.abilityType();

            boolean isMelee = abilityType == AbilityType.MELEE;
            boolean isGrenade = abilityType == AbilityType.GRENADE;
            boolean isClass = abilityType == AbilityType.CLASS_ABILITY;
            boolean isSuper = abilityType == AbilityType.SUPER;
            if (isMelee || isGrenade || isClass || isSuper) {
                tag(DMDamageTypes.Tags.IS_SUBCLASS_ABILITY).add(resourceKey);
            }

            if (isMelee) {
                tag(DMDamageTypes.Tags.IS_MELEE_ABILITY).add(resourceKey);
            }
            if (isGrenade) {
                tag(DMDamageTypes.Tags.IS_GRENADE_ABILITY).add(resourceKey);
            }
            if (isClass) {
                tag(DMDamageTypes.Tags.IS_CLASS_ABILITY).add(resourceKey);
            }
            if (isSuper) {
                tag(DMDamageTypes.Tags.IS_SUPER_ABILITY).add(resourceKey);
            }

            if (abilityType != AbilityType.NONE) {
                tag(DMDamageTypes.Tags.IS_ABILITY).add(resourceKey);
            }

            if (abilityType == AbilityType.DEBUFF) {
                tag(DMDamageTypes.Tags.IS_DEBUFF).add(resourceKey);
            }

            for (TagKey<DamageType> tag : registeredDamageType.additionalTags()) {
                tag(tag).add(resourceKey);
            }
        }
    }
}
