package io.github.stainlessstasis.destinymod.tooltip.component;

import io.github.stainlessstasis.destinymod.tooltip.TooltipWidthContext;
import net.minecraft.network.chat.Component;
import net.minecraft.world.inventory.tooltip.TooltipComponent;

public record DescriptionComponent(Component description, TooltipWidthContext widthContext, int color) implements TooltipComponent {}
