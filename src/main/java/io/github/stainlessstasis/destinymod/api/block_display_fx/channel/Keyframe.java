package io.github.stainlessstasis.destinymod.api.block_display_fx.channel;

import io.github.stainlessstasis.destinymod.api.block_display_fx.easing.Easing;

public record Keyframe<T>(float time, T value, Easing easing) {}
