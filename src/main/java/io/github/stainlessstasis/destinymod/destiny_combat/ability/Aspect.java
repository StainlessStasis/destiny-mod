package io.github.stainlessstasis.destinymod.destiny_combat.ability;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

public record Aspect(int cooldownTicks) {
    public static final Codec<Aspect> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                    Codec.INT.optionalFieldOf("cooldownTicks", -1).forGetter(Aspect::cooldownTicks)
            ).apply(instance, Aspect::new)
    );
}

