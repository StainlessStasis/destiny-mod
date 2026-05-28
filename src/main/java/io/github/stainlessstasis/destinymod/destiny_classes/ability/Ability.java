package io.github.stainlessstasis.destinymod.destiny_classes.ability;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.stainlessstasis.destinymod.destiny_classes.Subclass;
import io.github.stainlessstasis.destinymod.destiny_classes.Subclasses;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.Identifier;

public record Ability(AbilityType abilityType, int cooldownTicks, int maxCharges) {
    public static final Codec<Ability> CODEC = Identifier.CODEC.xmap(Abilities::getByID, Abilities::getID);
    public static final StreamCodec<ByteBuf, Ability> STREAM_CODEC = Identifier.STREAM_CODEC.map(Abilities::getByID, Abilities::getID);
}
