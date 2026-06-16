package io.github.stainlessstasis.destinymod.registry.property.aspect;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.stainlessstasis.destinymod.registry.property.AbilityProperty;

public record ThermalVentProperty(int bonusWaves, float angleBetweenWaves, float waveDamageMultiplier, float waveScorchMultiplier) implements AbilityProperty {
    public static final MapCodec<ThermalVentProperty> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            Codec.INT.fieldOf("bonusWaves").forGetter(ThermalVentProperty::bonusWaves),
            Codec.FLOAT.fieldOf("angleBetweenWaves").forGetter(ThermalVentProperty::angleBetweenWaves),
            Codec.FLOAT.fieldOf("waveDamageMultiplier").forGetter(ThermalVentProperty::waveDamageMultiplier),
            Codec.FLOAT.fieldOf("waveScorchMultiplier").forGetter(ThermalVentProperty::waveScorchMultiplier)
    ).apply(instance, ThermalVentProperty::new));
}


