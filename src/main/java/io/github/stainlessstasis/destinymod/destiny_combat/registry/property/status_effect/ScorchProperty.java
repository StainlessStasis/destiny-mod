package io.github.stainlessstasis.destinymod.destiny_combat.registry.property.status_effect;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.stainlessstasis.destinymod.destiny_combat.registry.property.AbilityProperty;

public record ScorchProperty(float damage, int decayDelayTicks, int ignitionThreshold) implements AbilityProperty {
    public static final MapCodec<ScorchProperty> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            Codec.FLOAT.fieldOf("damage").forGetter(ScorchProperty::damage),
            Codec.INT.fieldOf("decayDelayTicks").forGetter(ScorchProperty::decayDelayTicks),
            Codec.INT.fieldOf("ignitionThreshold").forGetter(ScorchProperty::ignitionThreshold)
    ).apply(instance, ScorchProperty::new));
}

