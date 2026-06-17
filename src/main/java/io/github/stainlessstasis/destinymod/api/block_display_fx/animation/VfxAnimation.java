package io.github.stainlessstasis.destinymod.api.block_display_fx.animation;

import io.github.stainlessstasis.destinymod.api.block_display_fx.channel.KeyframedChannel;
import org.joml.Quaternionf;
import org.joml.Vector3f;

public record VfxAnimation(
        KeyframedChannel<Vector3f, Vector3f> translationChannel,
        KeyframedChannel<Vector3f, Vector3f> scaleChannel,
        KeyframedChannel<Vector3f, Quaternionf> rotationChannel,
        KeyframedChannel<Vector3f, Vector3f> overlayColorChannel,
        KeyframedChannel<Float, float[]> overlayIntensityChannel,
        int durationTicks
) {}
