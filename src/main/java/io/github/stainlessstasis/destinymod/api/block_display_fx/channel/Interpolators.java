package io.github.stainlessstasis.destinymod.api.block_display_fx.channel;

import net.minecraft.util.Mth;
import org.joml.Quaternionf;
import org.joml.Vector3f;

public final class Interpolators {
    private Interpolators() {}

    public static void lerpVector3f(Vector3f start, Vector3f end, float t, Vector3f destination) {
        start.lerp(end, t, destination);
    }

    public static void lerpQuaternionf(Quaternionf start, Quaternionf end, float t, Quaternionf destination) {
        start.slerp(end, t, destination);
    }

    public static void lerpDegrees(Vector3f start, Vector3f end, float t, Quaternionf destination) {
        float x = Mth.lerp(t, start.x, end.x);
        float y = Mth.lerp(t, start.y, end.y);
        float z = Mth.lerp(t, start.z, end.z);
        destination.rotationYXZ(
                (float) Math.toRadians(y),
                (float) Math.toRadians(x),
                (float) Math.toRadians(z)
        );
    }
}
