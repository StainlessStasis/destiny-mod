package io.github.stainlessstasis.destinymod.registry.property.aspect;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.stainlessstasis.destinymod.registry.property.AbilityProperty;

public record BlazingPyreProperty(float energyRefundPercent) implements AbilityProperty {
    public static final MapCodec<BlazingPyreProperty> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            Codec.FLOAT.fieldOf("energyRefundPercent").forGetter(BlazingPyreProperty::energyRefundPercent)
    ).apply(instance, BlazingPyreProperty::new));
}


