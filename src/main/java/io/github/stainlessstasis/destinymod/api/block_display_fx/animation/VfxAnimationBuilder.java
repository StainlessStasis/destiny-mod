package io.github.stainlessstasis.destinymod.api.block_display_fx.animation;

import io.github.stainlessstasis.destinymod.api.block_display_fx.channel.Interpolators;
import io.github.stainlessstasis.destinymod.api.block_display_fx.channel.Keyframe;
import io.github.stainlessstasis.destinymod.api.block_display_fx.channel.KeyframedChannel;
import io.github.stainlessstasis.destinymod.api.block_display_fx.easing.Easing;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.List;

public class VfxAnimationBuilder {
    private KeyframedChannel<Vector3f, Vector3f> translationChannel;
    private KeyframedChannel<Vector3f, Vector3f> scaleChannel;
    private KeyframedChannel<Vector3f, Quaternionf> rotationChannel;
    KeyframedChannel<Vector3f, Vector3f> overlayColorChannel;
    KeyframedChannel<Float, float[]> overlayIntensityChannel;

    public TranslationBuilder translation(Vector3f start) {
        return new TranslationBuilder(start);
    }

    public ScaleBuilder scale(Vector3f start) {
        return new ScaleBuilder(start);
    }

    public RotationBuilder rotation(Vector3f startDegrees) {
        return new RotationBuilder(startDegrees);
    }

    public VfxAnimation build(int durationTicks) {
        if (translationChannel == null)
            translationChannel = new KeyframedChannel<>(
                    List.of(new Keyframe<>(0f, new Vector3f(), Easing.LINEAR),
                            new Keyframe<>(1f, new Vector3f(), Easing.LINEAR)),
                    Interpolators::lerpVector3f
            );
        if (scaleChannel == null)
            scaleChannel = new KeyframedChannel<>(
                    List.of(new Keyframe<>(0f, new Vector3f(1f), Easing.LINEAR),
                            new Keyframe<>(1f, new Vector3f(1f), Easing.LINEAR)),
                    Interpolators::lerpVector3f
            );
        if (rotationChannel == null)
            rotationChannel = new KeyframedChannel<>(
                    List.of(new Keyframe<>(0f, new Vector3f(), Easing.LINEAR),
                            new Keyframe<>(1f, new Vector3f(), Easing.LINEAR)),
                    Interpolators::lerpDegrees
            );
        if (overlayColorChannel == null)
            overlayColorChannel = new KeyframedChannel<>(
                    List.of(new Keyframe<>(0f, new Vector3f(1f), Easing.LINEAR),
                            new Keyframe<>(1f, new Vector3f(1f), Easing.LINEAR)),
                    Interpolators::lerpVector3f
            );
        if (overlayIntensityChannel == null)
            overlayIntensityChannel = new KeyframedChannel<>(
                    List.of(new Keyframe<>(0f, 0f, Easing.LINEAR),
                            new Keyframe<>(1f, 0f, Easing.LINEAR)),
                    Interpolators::lerpFloat
            );
        return new VfxAnimation(translationChannel, scaleChannel, rotationChannel, overlayColorChannel, overlayIntensityChannel, durationTicks);
    }

    public class TranslationBuilder {
        private final List<Keyframe<Vector3f>> keyframes = new ArrayList<>();

        private TranslationBuilder(Vector3f start) {
            keyframes.add(new Keyframe<>(0f, start, Easing.LINEAR));
        }

        public TranslationBuilder addKeyframe(float time, Vector3f value, Easing easing) {
            keyframes.add(new Keyframe<>(time, value, easing));
            return this;
        }

        public VfxAnimationBuilder end(Vector3f value, Easing easing) {
            keyframes.add(new Keyframe<>(1f, value, easing));
            translationChannel = new KeyframedChannel<>(keyframes, Interpolators::lerpVector3f);
            return VfxAnimationBuilder.this;
        }
    }

    public class ScaleBuilder {
        private final List<Keyframe<Vector3f>> keyframes = new ArrayList<>();

        private ScaleBuilder(Vector3f start) {
            keyframes.add(new Keyframe<>(0f, start, Easing.LINEAR));
        }

        public ScaleBuilder addKeyframe(float time, Vector3f value, Easing easing) {
            keyframes.add(new Keyframe<>(time, value, easing));
            return this;
        }

        public VfxAnimationBuilder end(Vector3f value, Easing easing) {
            keyframes.add(new Keyframe<>(1f, value, easing));
            scaleChannel = new KeyframedChannel<>(keyframes, Interpolators::lerpVector3f);
            return VfxAnimationBuilder.this;
        }
    }

    public class RotationBuilder {
        private final List<Keyframe<Vector3f>> keyframes = new ArrayList<>();

        private RotationBuilder(Vector3f startDegrees) {
            keyframes.add(new Keyframe<>(0f, startDegrees, Easing.LINEAR));
        }

        public RotationBuilder addKeyframe(float time, Vector3f degrees, Easing easing) {
            keyframes.add(new Keyframe<>(time, degrees, easing));
            return this;
        }

        public VfxAnimationBuilder end(Vector3f degrees, Easing easing) {
            keyframes.add(new Keyframe<>(1f, degrees, easing));
            rotationChannel = new KeyframedChannel<>(keyframes, Interpolators::lerpDegrees);
            return VfxAnimationBuilder.this;
        }
    }

    public OverlayBuilder overlay(Vector3f startColor, float startIntensity) {
        return new OverlayBuilder(startColor, startIntensity);
    }

    public class OverlayBuilder {
        private final List<Keyframe<Vector3f>> colorKeyframes = new ArrayList<>();
        private final List<Keyframe<Float>> intensityKeyframes = new ArrayList<>();

        private OverlayBuilder(Vector3f startColor, float startIntensity) {
            colorKeyframes.add(new Keyframe<>(0f, startColor, Easing.LINEAR));
            intensityKeyframes.add(new Keyframe<>(0f, startIntensity, Easing.LINEAR));
        }

        public OverlayBuilder addColorKeyframe(float time, Vector3f color, Easing easing) {
            colorKeyframes.add(new Keyframe<>(time, color, easing));
            return this;
        }

        public OverlayBuilder addIntensityKeyframe(float time, float intensity, Easing easing) {
            intensityKeyframes.add(new Keyframe<>(time, intensity, easing));
            return this;
        }

        public VfxAnimationBuilder end(Vector3f endColor, Easing colorEasing, float endIntensity, Easing intensityEasing) {
            colorKeyframes.add(new Keyframe<>(1f, endColor, colorEasing));
            intensityKeyframes.add(new Keyframe<>(1f, endIntensity, intensityEasing));
            overlayColorChannel = new KeyframedChannel<>(colorKeyframes, Interpolators::lerpVector3f);
            overlayIntensityChannel = new KeyframedChannel<>(intensityKeyframes, Interpolators::lerpFloat);
            return VfxAnimationBuilder.this;
        }
    }
}
