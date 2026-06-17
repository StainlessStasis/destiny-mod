package io.github.stainlessstasis.destinymod.api.block_display_fx.channel;

import io.github.stainlessstasis.destinymod.api.block_display_fx.easing.Easing;
import net.minecraft.util.Mth;
import org.joml.Quaternionf;
import org.joml.Vector3f;

public record RotationDegreesChannel(Vector3f start, Vector3f end, Easing easing) implements Channel<Quaternionf> {
    @Override
    public void evaluate(float rawT, Quaternionf destination) {
        float easedT = easing.apply(rawT);
        float x = Mth.lerp(easedT, start.x, end.x);
        float y = Mth.lerp(easedT, start.y, end.y);
        float z = Mth.lerp(easedT, start.z, end.z);
        destination.rotationYXZ(
                (float) Math.toRadians(y),
                (float) Math.toRadians(x),
                (float) Math.toRadians(z)
        );
    }
}
