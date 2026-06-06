package io.github.stainlessstasis.destinymod.destiny_classes;

import com.mojang.serialization.Codec;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.Identifier;

public record Subclass(Component title, DestinyClass destinyClass, DestinyElement destinyElement) {
    public static final Codec<Subclass> CODEC = Identifier.CODEC.xmap(Subclasses::getByID, Subclasses::getID);
    public static final StreamCodec<ByteBuf, Subclass> STREAM_CODEC = Identifier.STREAM_CODEC.map(Subclasses::getByID, Subclasses::getID);

    public Identifier getID() {
        return Subclasses.getID(this);
    }
}
