package io.github.stainlessstasis.destinymod.ability;

import com.mojang.serialization.Codec;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.StringRepresentable;

public enum AbilityType implements StringRepresentable {
    MELEE("melee");

    public static final Codec<AbilityType> CODEC = StringRepresentable.fromEnum(AbilityType::values);
    public static final StreamCodec<ByteBuf, AbilityType> STREAM_CODEC = ByteBufCodecs.BYTE.map(
            b -> AbilityType.values()[b],
            e -> (byte) e.ordinal()
    );

    private final String name;
    AbilityType(String name) {
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
