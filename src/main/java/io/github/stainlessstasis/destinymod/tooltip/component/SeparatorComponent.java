package io.github.stainlessstasis.destinymod.tooltip.component;

import io.github.stainlessstasis.destinymod.tooltip.TooltipWidthContext;
import net.minecraft.world.inventory.tooltip.TooltipComponent;

public record SeparatorComponent(TooltipWidthContext widthContext, int height, int color) implements TooltipComponent {}
