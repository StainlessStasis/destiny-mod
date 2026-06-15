package io.github.stainlessstasis.destinymod.destiny_combat.ability;

import com.mojang.serialization.Codec;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.StringRepresentable;
import org.jspecify.annotations.NonNull;

import java.util.Set;

public enum AbilityType implements StringRepresentable {
    MELEE("melee"),
    GRENADE("grenade"),
    CLASS_ABILITY("class"),
    SUPER("super"),
    PASSIVE("passive"),
    DEBUFF("debuff"),
    NONE("none");

    public static final Codec<AbilityType> CODEC = StringRepresentable.fromEnum(AbilityType::values);
//    public static final StreamCodec<ByteBuf, AbilityType> STREAM_CODEC = ByteBufCodecs.idMapper(
//            index -> AbilityType.values()[index],
//            AbilityType::ordinal
//    );
    public static final StreamCodec<ByteBuf, AbilityType> STREAM_CODEC = ByteBufCodecs.fromCodec(CODEC);

    public static Set<AbilityType> getCombatTypes() {
        return Set.of(MELEE, GRENADE, CLASS_ABILITY, SUPER, PASSIVE);
    }

    private final String name;
    AbilityType(String name) {
        this.name = name;
    }

    @Override
    public @NonNull String getSerializedName() {
        return this.name;
    }

    @Override
    public String toString() {
        return this.name;
    }
}
