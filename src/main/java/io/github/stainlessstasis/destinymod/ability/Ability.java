package io.github.stainlessstasis.destinymod.ability;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

public record Ability(AbilityType abilityType, int cooldownTicks, int maxCharges) {
    public static final StreamCodec<ByteBuf, Ability> STREAM_CODEC = StreamCodec.composite(
            AbilityType.STREAM_CODEC, Ability::abilityType,
            ByteBufCodecs.VAR_INT, Ability::cooldownTicks,
            ByteBufCodecs.VAR_INT, Ability::maxCharges,
            Ability::new
    );
}
