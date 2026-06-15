package io.github.stainlessstasis.destinymod.registry.damage_type;

import io.github.stainlessstasis.destinymod.destiny_combat.ability.AbilityType;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagKey;
import net.minecraft.world.damagesource.DamageType;

import java.util.Set;

public record RegisteredDamageType(ResourceKey<DamageType> resourceKey, AbilityType abilityType, Set<TagKey<DamageType>> additionalTags) {
}
