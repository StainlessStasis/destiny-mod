package io.github.stainlessstasis.destinymod.registry.property.ability;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.stainlessstasis.destinymod.registry.property.AbilityProperty;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

public record ThermiteGrenadeProperty(int pulses, int pulseIntervalTicks, float distancePerTick, float maxDistance, float maxStepHeight, float width, float height) implements AbilityProperty {
    public static final MapCodec<ThermiteGrenadeProperty> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            Codec.INT.fieldOf("pulses").forGetter(ThermiteGrenadeProperty::pulses),
            Codec.INT.fieldOf("pulseIntervalTicks").forGetter(ThermiteGrenadeProperty::pulseIntervalTicks),
            Codec.FLOAT.fieldOf("distancePerTick").forGetter(ThermiteGrenadeProperty::distancePerTick),
            Codec.FLOAT.fieldOf("maxDistance").forGetter(ThermiteGrenadeProperty::maxDistance),
            Codec.FLOAT.fieldOf("maxStepHeight").forGetter(ThermiteGrenadeProperty::maxStepHeight),
            Codec.FLOAT.fieldOf("width").forGetter(ThermiteGrenadeProperty::width),
            Codec.FLOAT.fieldOf("height").forGetter(ThermiteGrenadeProperty::height)
    ).apply(instance, ThermiteGrenadeProperty::new));

    public static ThermiteGrenadeProperty getDefault() {
        return new ThermiteGrenadeProperty(4, 30, 2.5f, 15f, 1.5f, 3.5f, 4.75f);
    }
}
