package io.github.stainlessstasis.destinymod.destiny_classes.ability;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.stainlessstasis.destinymod.destiny_classes.Subclass;
import io.github.stainlessstasis.destinymod.destiny_classes.Subclasses;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.Identifier;

public record Aspect(int cooldownTicks) {
    public static final Codec<Aspect> CODEC = Identifier.CODEC.xmap(Aspects::getByID, Aspects::getID);
    public static final StreamCodec<ByteBuf, Aspect> STREAM_CODEC = Identifier.STREAM_CODEC.map(Aspects::getByID, Aspects::getID);
}

