package io.github.stainlessstasis.destinymod.destiny_combat.ability;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.stainlessstasis.destinymod.destiny_classes.DestinyElement;

public record Ability(
        AbilityType abilityType, DestinyElement element, int cooldownTicks, int maxCharges, float activationChance, float damage, int scorch
) {
    public static final Codec<Ability> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                    AbilityType.CODEC.fieldOf("abilityType").forGetter(Ability::abilityType),
                    DestinyElement.CODEC.fieldOf("element").forGetter(Ability::element),
                    Codec.INT.optionalFieldOf("cooldownTicks", -1).forGetter(Ability::cooldownTicks),
                    Codec.INT.optionalFieldOf("maxCharges", -1).forGetter(Ability::maxCharges),
                    Codec.FLOAT.optionalFieldOf("activationChance", -1f).forGetter(Ability::activationChance),
                    Codec.FLOAT.optionalFieldOf("damage", -1f).forGetter(Ability::damage),
                    Codec.INT.optionalFieldOf("scorch", -1).forGetter(Ability::scorch)
            ).apply(instance, Ability::new)
    );
}
