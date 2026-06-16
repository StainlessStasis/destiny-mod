package io.github.stainlessstasis.destinymod.api.block_display_fx.easing;

@FunctionalInterface
public interface EasingFunction {
    float apply(float t);
}
