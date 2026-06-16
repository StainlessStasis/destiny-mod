package io.github.stainlessstasis.destinymod.api.block_display_fx.channel;

import io.github.stainlessstasis.destinymod.api.block_display_fx.easing.Easing;
import org.joml.Quaternionf;

public record RotationChannel(Quaternionf start, Quaternionf end, Easing easing) implements Channel<Quaternionf> {
    @Override
    public void evaluate(float rawT, Quaternionf destination) {
        float easedT = easing.apply(rawT);
        start.slerp(end, easedT, destination);
    }
}
