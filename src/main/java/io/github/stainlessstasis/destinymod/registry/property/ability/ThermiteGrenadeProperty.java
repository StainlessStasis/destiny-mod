package io.github.stainlessstasis.destinymod.registry.property.ability;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.stainlessstasis.destinymod.registry.property.AbilityProperty;

public record ThermiteGrenadeProperty(int pulses, int pulseIntervalTicks, float distancePerTick, float maxDistance, float maxStepHeight) implements AbilityProperty {
    public static final MapCodec<ThermiteGrenadeProperty> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            Codec.INT.fieldOf("pulses").forGetter(ThermiteGrenadeProperty::pulses),
            Codec.INT.fieldOf("pulseIntervalTicks").forGetter(ThermiteGrenadeProperty::pulseIntervalTicks),
            Codec.FLOAT.fieldOf("distancePerTick").forGetter(ThermiteGrenadeProperty::distancePerTick),
            Codec.FLOAT.fieldOf("maxDistance").forGetter(ThermiteGrenadeProperty::maxDistance),
            Codec.FLOAT.fieldOf("maxStepHeight").forGetter(ThermiteGrenadeProperty::maxStepHeight)
    ).apply(instance, ThermiteGrenadeProperty::new));

    public static ThermiteGrenadeProperty getDefault() {
        return new ThermiteGrenadeProperty(4, 20, 2f, 12f, 1.5f);
    }
}
