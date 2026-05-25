package com.example.examplemod.client.tooltip;

import com.example.examplemod.tooltip.TooltipWidthContext;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;

public class HeaderTooltipComponent implements ClientTooltipComponent {
    private final String title;
    private final String subtitle;
    private final TooltipWidthContext widthContext;
    private final int color;

    public HeaderTooltipComponent(String title, String subtitle, TooltipWidthContext widthContext, int color) {
        this.title = title;
        this.subtitle = subtitle;
        this.widthContext = widthContext;
        this.color = color;
    }

    @Override
    public int getHeight(Font font) {
        return 32;
    }

    @Override
    public int getWidth(Font font) {
        System.out.println(this.widthContext.getWidth());
        return this.widthContext.getWidth();
    }

    @Override
    public void extractImage(Font font, int x, int y, int w, int h, GuiGraphicsExtractor graphics) {
        int renderX = x - 4;

        graphics.fill(renderX, y, renderX + getWidth(font), y + getHeight(font), this.color);
        graphics.text(font, this.title, x, y + 6, 0xFFFFFFFF, true);
        graphics.text(font, this.subtitle, x, y + 18, 0xFFAAAAAA, false);
    }
}
