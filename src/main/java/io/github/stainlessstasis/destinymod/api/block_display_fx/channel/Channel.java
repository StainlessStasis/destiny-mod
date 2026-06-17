package io.github.stainlessstasis.destinymod.api.block_display_fx.channel;

public interface Channel<T> {
    void evaluate(float t, T destination);
}
