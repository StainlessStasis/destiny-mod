package io.github.stainlessstasis.destinymod.api.block_display_fx.channel;

import io.github.stainlessstasis.destinymod.api.block_display_fx.easing.Easing;
import org.joml.Quaternionf;
import org.joml.Vector3f;

public record RotationDegreesChannel(Vector3f start, Vector3f end, Easing easing) implements Channel<Quaternionf> {
    @Override
    public void evaluate(float rawT, Quaternionf destination) {
        float easedT = easing.apply(rawT);
        Vector3f easedRotation = start.lerp(end, easedT);
        destination.rotationYXZ(
                (float) Math.toRadians(easedRotation.x),
                (float) Math.toRadians(easedRotation.y),
                (float) Math.toRadians(easedRotation.z)
        );
    }
}
