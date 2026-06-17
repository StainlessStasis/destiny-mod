package io.github.stainlessstasis.destinymod.api.block_display_fx.channel;

import net.minecraft.util.Mth;

import java.util.List;

public class KeyframedChannel<S, T> implements Channel<T> {
    private final List<Keyframe<S>> keyframes;
    private final LerpFunction<S, T> lerpFunc;

    public KeyframedChannel(List<Keyframe<S>> keyframes, LerpFunction<S, T> lerpFunc) {
        this.keyframes = List.copyOf(keyframes);
        this.lerpFunc = lerpFunc;
    }

    @Override
    public void evaluate(float t, T destination) {
        Keyframe<S> prev = keyframes.getFirst();
        Keyframe<S> next = keyframes.getLast();

        for (int i = 0; i < keyframes.size() - 1; i++) {
            if (t <= keyframes.get(i + 1).time()) {
                prev = keyframes.get(i);
                next = keyframes.get(i + 1);
                break;
            }
        }

        float segmentT = Mth.inverseLerp(t, prev.time(), next.time());
        float easedT = next.easing().apply(segmentT);
        lerpFunc.lerp(prev.value(), next.value(), easedT, destination);
    }

    @FunctionalInterface
    public interface LerpFunction<S, T> {
        void lerp(S start, S end, float t, T destination);
    }
}
