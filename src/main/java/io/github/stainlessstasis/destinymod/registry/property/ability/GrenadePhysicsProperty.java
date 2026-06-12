package io.github.stainlessstasis.destinymod.registry.property.ability;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.stainlessstasis.destinymod.registry.property.AbilityProperty;

public record GrenadePhysicsProperty(
        float bounciness, float friction, float gravity, float settleSpeedThreshold,
        boolean detonateOnBlock, boolean detonateOnEntity, boolean detonateOnSettle, int ticksBeforeForceDetonate
)
implements AbilityProperty {
    public static final MapCodec<GrenadePhysicsProperty> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            Codec.FLOAT.fieldOf("bounciness").forGetter(GrenadePhysicsProperty::bounciness),
            Codec.FLOAT.fieldOf("friction").forGetter(GrenadePhysicsProperty::friction),
            Codec.FLOAT.fieldOf("gravity").forGetter(GrenadePhysicsProperty::gravity),
            Codec.FLOAT.fieldOf("settleSpeedThreshold").forGetter(GrenadePhysicsProperty::settleSpeedThreshold),
            Codec.BOOL.fieldOf("detonateOnBlock").forGetter(GrenadePhysicsProperty::detonateOnBlock),
            Codec.BOOL.fieldOf("detonateOnEntity").forGetter(GrenadePhysicsProperty::detonateOnEntity),
            Codec.BOOL.fieldOf("detonateOnSettle").forGetter(GrenadePhysicsProperty::detonateOnSettle),
            Codec.INT.fieldOf("ticksBeforeForceDetonate").forGetter(GrenadePhysicsProperty::ticksBeforeForceDetonate)
    ).apply(instance, GrenadePhysicsProperty::new));

    public static GrenadePhysicsProperty getDefault() {
        return new GrenadePhysicsProperty(0.5f, 0.5f, 0.03f, 0.25f, true, true, true, -1);
    }
}


