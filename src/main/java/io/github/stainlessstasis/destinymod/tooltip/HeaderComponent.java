package io.github.stainlessstasis.destinymod.tooltip;

import net.minecraft.world.inventory.tooltip.TooltipComponent;

public record HeaderComponent(String title, String subtitle, TooltipWidthContext widthContext, int color) implements TooltipComponent { }
