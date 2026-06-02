package io.github.stainlessstasis.destinymod.destiny_classes.ability;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.stainlessstasis.destinymod.destiny_classes.DestinyElement;
import io.github.stainlessstasis.destinymod.destiny_classes.Subclass;
import io.github.stainlessstasis.destinymod.destiny_classes.Subclasses;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.Identifier;

public record Ability(
        AbilityType abilityType, DestinyElement element, int cooldownTicks, int maxCharges, float damage, int scorch
) {
    public static final Codec<Ability> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                    AbilityType.CODEC.fieldOf("abilityType").forGetter(Ability::abilityType),
                    DestinyElement.CODEC.fieldOf("element").forGetter(Ability::element),
                    Codec.INT.fieldOf("cooldownTicks").forGetter(Ability::cooldownTicks),
                    Codec.INT.fieldOf("maxCharges").forGetter(Ability::maxCharges),
                    Codec.FLOAT.fieldOf("damage").forGetter(Ability::damage),
                    Codec.INT.fieldOf("maxCharges").forGetter(Ability::scorch)
            ).apply(instance, Ability::new)
    );

    // wait do i even need this actually (no)
//    public static final StreamCodec<ByteBuf, Ability> STREAM_CODEC = StreamCodec.composite(
//            AbilityType.STREAM_CODEC, Ability::abilityType,
//            DestinyElement.STREAM_CODEC, Ability::element,
//            ByteBufCodecs.VAR_INT, Ability::cooldownTicks,
//            ByteBufCodecs.VAR_INT, Ability::maxCharges,
//            ByteBufCodecs.FLOAT, Ability::damage,
//            Ability::new
//    );
}
