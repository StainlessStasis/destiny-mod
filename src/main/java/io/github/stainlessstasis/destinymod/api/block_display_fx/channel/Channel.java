package io.github.stainlessstasis.destinymod.api.block_display_fx.channel;

public interface Channel<T> {
    T start();
    T end();
    void evaluate(float t, T destination);
}
