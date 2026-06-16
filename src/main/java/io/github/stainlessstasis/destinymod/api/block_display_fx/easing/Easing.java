package io.github.stainlessstasis.destinymod.api.block_display_fx.easing;

// Easing formulas from https://easings.net/
public enum Easing {
    LINEAR(t -> t),
    EASE_IN_QUAD(t -> t * t),
    EASE_OUT_QUAD(t -> 1 - (1 - t) * (1 - t));

    private final EasingFunction formula;

    Easing(EasingFunction formula) {
        this.formula = formula;
    }

    public float apply(float t) {
        return formula.apply(Math.clamp(t, 0f, 1f));
    }
}
