package io.github.stainlessstasis.destinymod.compat;

import dev.lambdaurora.lambdynlights.api.entity.luminance.EntityLuminance;
import dev.lambdaurora.lambdynlights.api.item.ItemLightSourceManager;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.Range;

public final class ConstantEntityLuminance implements EntityLuminance {
    public static final ConstantEntityLuminance INSTANCE = new ConstantEntityLuminance();

    private ConstantEntityLuminance() {}

    @Override
    public Type type() {
        return LDLInitializer.CONSTANT;
    }

    @Override
    public @Range(from = 0, to = 15) int getLuminance(
            ItemLightSourceManager itemLightSourceManager,
            Entity entity
    ) {
        return 5;
    }
}
