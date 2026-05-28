package io.github.stainlessstasis.destinymod.destiny_classes;

import com.mojang.serialization.Codec;
import io.github.stainlessstasis.DMColor;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.StringRepresentable;

public enum DestinyElement implements StringRepresentable {
    SOLAR("solar", DMColor.SOLAR.get()),
    ARC("arc", DMColor.LIGHT_GRAY.get()),
    VOID("void", DMColor.LIGHT_GRAY.get()),
    STASIS("stasis", DMColor.LIGHT_GRAY.get()),
    STRAND("strand", DMColor.LIGHT_GRAY.get());

    public static final Codec<DestinyElement> CODEC = StringRepresentable.fromEnum(DestinyElement::values);

    public static final StreamCodec<ByteBuf, DestinyElement> STREAM_CODEC = ByteBufCodecs.BYTE.map(
            b -> DestinyElement.values()[b],
            e -> (byte) e.ordinal()
    );

    private final String name;
    private final int color;

    DestinyElement(String name, int color) {
        this.name = name;
        this.color = color;
    }

    public int getColor() {
        return this.color;
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
