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
    public static final Codec<Ability> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                    AbilityType.CODEC.fieldOf("abilityType").forGetter(Ability::abilityType),
                    Codec.INT.fieldOf("cooldownTicks").forGetter(Ability::cooldownTicks),
                    Codec.INT.optionalFieldOf("maxCharges", 1).forGetter(Ability::maxCharges)
            ).apply(instance, Ability::new)
    );

    public static final StreamCodec<ByteBuf, Ability> STREAM_CODEC = StreamCodec.composite(
            AbilityType.STREAM_CODEC, Ability::abilityType,
            ByteBufCodecs.VAR_INT, Ability::cooldownTicks,
            ByteBufCodecs.VAR_INT, Ability::maxCharges,
            Ability::new
    );
}
