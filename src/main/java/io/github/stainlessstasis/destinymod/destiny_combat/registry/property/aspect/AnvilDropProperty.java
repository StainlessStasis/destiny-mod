package io.github.stainlessstasis.destinymod.destiny_combat.registry.property.aspect;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.stainlessstasis.destinymod.destiny_combat.registry.property.AbilityProperty;

public record AnvilDropProperty(float gravityMultiplier, float speedMultiplier, float damageMultiplier) implements AbilityProperty {
    public static final MapCodec<AnvilDropProperty> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            Codec.FLOAT.fieldOf("gravityMultiplier").forGetter(AnvilDropProperty::gravityMultiplier),
            Codec.FLOAT.fieldOf("speedMultiplier").forGetter(AnvilDropProperty::speedMultiplier),
            Codec.FLOAT.fieldOf("damageMultiplier").forGetter(AnvilDropProperty::damageMultiplier)
    ).apply(instance, AnvilDropProperty::new));
}

