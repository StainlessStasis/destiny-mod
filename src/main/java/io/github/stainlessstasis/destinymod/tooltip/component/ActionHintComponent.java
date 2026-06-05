package io.github.stainlessstasis.destinymod.tooltip.component;

import io.github.stainlessstasis.destinymod.tooltip.TooltipWidthContext;
import net.minecraft.world.inventory.tooltip.TooltipComponent;

public record ActionHintComponent(String hint, TooltipWidthContext widthContext) implements TooltipComponent { }

