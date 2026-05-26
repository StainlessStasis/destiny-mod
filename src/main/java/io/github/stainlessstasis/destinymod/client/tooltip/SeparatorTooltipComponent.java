package io.github.stainlessstasis.destinymod.client.tooltip;

import io.github.stainlessstasis.destinymod.tooltip.TooltipWidthContext;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;

public class SeparatorTooltipComponent implements ClientTooltipComponent {
    private final TooltipWidthContext widthContext;
    private final int height;
    private final int color;

    public SeparatorTooltipComponent(TooltipWidthContext widthContext, int height, int color) {
        this.widthContext = widthContext;
        this.height = height;
        this.color = color;
    }

    @Override
    public int getHeight(Font font) {
        return this.height;
    }

    @Override
    public int getWidth(Font font) {
        return this.widthContext.getWidth();
    }

    @Override
    public void extractImage(Font font, int x, int y, int w, int h, GuiGraphicsExtractor graphics) {
        int renderX = x - 4;
        int renderY = y - 2;
        graphics.fill(renderX, renderY, renderX+getWidth(font), renderY+this.height, this.color);
    }
}
