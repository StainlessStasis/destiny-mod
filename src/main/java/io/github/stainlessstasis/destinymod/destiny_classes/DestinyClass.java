package io.github.stainlessstasis.destinymod.destiny_classes;

import com.mojang.serialization.Codec;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.StringRepresentable;

public enum DestinyClass implements StringRepresentable {
    HUNTER("hunter"),
    TITAN("titan"),
    WARLOCK("warlock");

    public static final Codec<DestinyClass> CODEC = StringRepresentable.fromEnum(DestinyClass::values);
    public static final StreamCodec<ByteBuf, DestinyClass> STREAM_CODEC = ByteBufCodecs.BYTE.map(
            b -> DestinyClass.values()[b],
            e -> (byte) e.ordinal()
    );

    private final String name;
    DestinyClass(String name) {
        this.name = name;
    }

    @Override
    public String getSerializedName() {
        return this.name;
    }

    @Override
    public String toString() {
        return this.name;
    }
}
