package io.github.stainlessstasis.destinymod.api.block_display_fx.channel;

import io.github.stainlessstasis.destinymod.api.block_display_fx.easing.Easing;
import org.joml.Vector3f;

public record Vector3fChannel(Vector3f start, Vector3f end, Easing easing) implements Channel<Vector3f> {
    @Override
    public void evaluate(float t, Vector3f destination) {
        float easedT = easing.apply(t);
        start.lerp(end, easedT, destination);
    }
}
