package io.github.stainlessstasis.destinymod.destiny_combat.ability;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.resources.Identifier;

public record Aspect(Identifier subclassID, AbilityType abilityType, int cooldownTicks) {
    public static final Codec<Aspect> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                    Identifier.CODEC.fieldOf("subclassID").forGetter(Aspect::subclassID),
                    AbilityType.CODEC.fieldOf("abilityType").forGetter(Aspect::abilityType),
                    Codec.INT.optionalFieldOf("cooldownTicks", -1).forGetter(Aspect::cooldownTicks)
            ).apply(instance, Aspect::new)
    );
}

