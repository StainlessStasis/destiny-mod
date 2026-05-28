package io.github.stainlessstasis.destinymod.destiny_classes.ability;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

public record Aspect(int cooldownTicks) {
    public static final StreamCodec<ByteBuf, Aspect> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.VAR_INT, Aspect::cooldownTicks,
            Aspect::new
    );

    public static final Codec<Aspect> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.INT.fieldOf("cooldownTicks").forGetter(Aspect::cooldownTicks)
    ).apply(instance, Aspect::new));
}

