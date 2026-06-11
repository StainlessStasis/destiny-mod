package io.github.stainlessstasis.destinymod.destiny_combat.registry.property.ability;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.stainlessstasis.destinymod.destiny_combat.registry.property.AbilityProperty;

public record IgnitionProperty(float range) implements AbilityProperty {
    public static final MapCodec<IgnitionProperty> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            Codec.FLOAT.fieldOf("range").forGetter(IgnitionProperty::range)
    ).apply(instance, IgnitionProperty::new));
}

