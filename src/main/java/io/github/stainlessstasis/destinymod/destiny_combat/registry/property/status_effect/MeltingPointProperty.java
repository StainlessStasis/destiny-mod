package io.github.stainlessstasis.destinymod.destiny_combat.registry.property.status_effect;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.stainlessstasis.destinymod.destiny_combat.registry.property.AbilityProperty;

public record MeltingPointProperty(int durationTicks, float additionalDamagePercent) implements AbilityProperty {
    public static final MapCodec<MeltingPointProperty> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            Codec.INT.fieldOf("durationTicks").forGetter(MeltingPointProperty::durationTicks),
            Codec.FLOAT.fieldOf("additionalDamagePercent").forGetter(MeltingPointProperty::additionalDamagePercent)
    ).apply(instance, MeltingPointProperty::new));
}
