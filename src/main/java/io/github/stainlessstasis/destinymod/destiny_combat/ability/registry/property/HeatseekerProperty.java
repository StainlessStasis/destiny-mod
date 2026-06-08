package io.github.stainlessstasis.destinymod.destiny_combat.ability.registry.property;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

public record HeatseekerProperty(float homingStrength, int bonusScorch) implements AbilityProperty {
    public static final MapCodec<HeatseekerProperty> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            Codec.FLOAT.fieldOf("homing_strength").forGetter(HeatseekerProperty::homingStrength),
            Codec.INT.fieldOf("bonus_scorch").forGetter(HeatseekerProperty::bonusScorch)
    ).apply(instance, HeatseekerProperty::new));

    @Override
    public String type() {
        return "heatseeker";
    }
}
