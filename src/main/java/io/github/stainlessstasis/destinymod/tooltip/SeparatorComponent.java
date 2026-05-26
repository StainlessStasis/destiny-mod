package io.github.stainlessstasis.destinymod.tooltip;

import net.minecraft.world.inventory.tooltip.TooltipComponent;

public record SeparatorComponent(TooltipWidthContext widthContext, int height, int color) implements TooltipComponent {}
