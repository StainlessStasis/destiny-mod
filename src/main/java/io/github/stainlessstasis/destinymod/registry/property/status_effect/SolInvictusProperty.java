package io.github.stainlessstasis.destinymod.registry.property.status_effect;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.stainlessstasis.destinymod.registry.property.AbilityProperty;

public record SolInvictusProperty(int durationTicks, float additionalAbilityRegenPercent) implements AbilityProperty {
    public static final MapCodec<SolInvictusProperty> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            Codec.INT.fieldOf("durationTicks").forGetter(SolInvictusProperty::durationTicks),
            Codec.FLOAT.fieldOf("additionalAbilityRegenPercent").forGetter(SolInvictusProperty::additionalAbilityRegenPercent)
    ).apply(instance, SolInvictusProperty::new));
}

