package io.github.stainlessstasis.destinymod.destiny_combat.ability.registry.property;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

public record HeatseekerProperty(float homingStrength, float homingRange, float homingConeAngle, int bonusScorch) implements AbilityProperty {
    public static final MapCodec<HeatseekerProperty> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            Codec.FLOAT.fieldOf("homingStrength").forGetter(HeatseekerProperty::homingStrength),
            Codec.FLOAT.fieldOf("homingRange").forGetter(HeatseekerProperty::homingRange),
            Codec.FLOAT.fieldOf("homingConeAngle").forGetter(HeatseekerProperty::homingConeAngle),
            Codec.INT.fieldOf("bonusScorch").forGetter(HeatseekerProperty::bonusScorch)
    ).apply(instance, HeatseekerProperty::new));

    @Override
    public String type() {
        return "heatseeker";
    }
}
