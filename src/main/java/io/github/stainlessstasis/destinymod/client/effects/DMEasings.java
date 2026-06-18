package io.github.stainlessstasis.destinymod.client.effects;

import io.github.stainlessstasis.bdanimator.easing.Easing;
import io.github.stainlessstasis.bdanimator.easing.Easings;
import io.github.stainlessstasis.destinymod.DestinyMod;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class DMEasings {
    public static final DeferredRegister<Easing> EASINGS = DeferredRegister.create(Easings.EASING_REGISTRY_KEY, DestinyMod.MODID);

    public static final Supplier<Easing> TEST = EASINGS.register("test", () -> new Easing(t -> {
        if (t == 0 || t == 1) return t;
        float frequency = 12f;
        float spikes = (float) Math.abs(Math.sin(t * Math.PI * frequency) - 0.5f);
        float amplitude = 0.25f;
        return t + (spikes * amplitude);
    }));
}
