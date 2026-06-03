package io.github.stainlessstasis.destinymod.destiny_classes;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import io.github.stainlessstasis.destinymod.DMColor;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.StringRepresentable;

public enum DestinyElement implements StringRepresentable {
    SOLAR("solar", DMColor.SOLAR_LIGHT, DMColor.SOLAR, DMColor.SOLAR_DARK),
    ARC("arc", DMColor.WHITE, DMColor.LIGHT_GRAY, DMColor.BLACK),
    VOID("void", DMColor.WHITE, DMColor.LIGHT_GRAY, DMColor.BLACK),
    STASIS("stasis", DMColor.WHITE, DMColor.LIGHT_GRAY, DMColor.BLACK),
    STRAND("strand", DMColor.WHITE, DMColor.LIGHT_GRAY, DMColor.BLACK),
    NONE("none", DMColor.WHITE, DMColor.LIGHT_GRAY, DMColor.BLACK);

    public static final Codec<DestinyElement> CODEC = StringRepresentable.fromEnum(DestinyElement::values);
    public static final MapCodec<DestinyElement> MAP_CODEC = CODEC.fieldOf("element");
    public static final StreamCodec<ByteBuf, DestinyElement> STREAM_CODEC = ByteBufCodecs.fromCodec(CODEC);

    private final String name;
    private final DMColor lightColor;
    private final DMColor baseColor;
    private final DMColor darkColor;

    DestinyElement(String name, DMColor lightColor, DMColor baseColor, DMColor darkColor) {
        this.name = name;
        this.lightColor = lightColor;
        this.baseColor = baseColor;
        this.darkColor = darkColor;
    }

    public DMColor getColor() {
        return this.baseColor;
    }
    public DMColor getColorLight() {
        return this.lightColor;
    }
    public DMColor getColorDark() {
        return this.darkColor;
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
