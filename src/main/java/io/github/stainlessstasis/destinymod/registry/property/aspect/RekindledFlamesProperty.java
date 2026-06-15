package io.github.stainlessstasis.destinymod.registry.property.aspect;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.stainlessstasis.destinymod.registry.property.AbilityProperty;

public record RekindledFlamesProperty(float pulseSpeedPercent, float maxDistanceMultiplier, int hitsPerAdditionalPulse, int maxAdditionalPulses) implements AbilityProperty {
    public static final MapCodec<RekindledFlamesProperty> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            Codec.FLOAT.fieldOf("pulseSpeedPercent").forGetter(RekindledFlamesProperty::pulseSpeedPercent),
            Codec.FLOAT.fieldOf("maxDistanceMultiplier").forGetter(RekindledFlamesProperty::maxDistanceMultiplier),
            Codec.INT.fieldOf("hitsPerAdditionalPulse").forGetter(RekindledFlamesProperty::hitsPerAdditionalPulse),
            Codec.INT.fieldOf("maxAdditionalPulses").forGetter(RekindledFlamesProperty::maxAdditionalPulses)
    ).apply(instance, RekindledFlamesProperty::new));
}

