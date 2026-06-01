package io.github.stainlessstasis.destinymod.compat.LDL;

import dev.lambdaurora.lambdynlights.api.entity.luminance.EntityLuminance;
import dev.lambdaurora.lambdynlights.api.item.ItemLightSourceManager;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.Range;
import org.jspecify.annotations.NonNull;

public final class ConstantEntityLuminance implements EntityLuminance {
    public static final ConstantEntityLuminance INSTANCE = new ConstantEntityLuminance();

    private ConstantEntityLuminance() {}

    @Override
    public @NonNull Type type() {
        return LDLCompat.CONSTANT;
    }

    @Override
    public @Range(from = 0, to = 15) int getLuminance(
            @NonNull ItemLightSourceManager itemLightSourceManager,
            @NonNull Entity entity
    ) {
        return 5;
    }
}
