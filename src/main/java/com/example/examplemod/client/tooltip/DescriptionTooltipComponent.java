package com.example.examplemod.client.tooltip;

import com.example.examplemod.tooltip.TooltipWidthContext;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.network.chat.Component;
import net.minecraft.util.FormattedCharSequence;

import java.util.List;

public class DescriptionTooltipComponent implements ClientTooltipComponent {
    public static final int MAX_WIDTH = 200;
    public static final int WIDTH_PADDING = 8;
    public static final float TEXT_SCALE = 1f;
    public static final int LINE_SPACING = 10;

    private final List<FormattedCharSequence> wrappedLines;
    private final TooltipWidthContext widthContext;
    private final int color;

    private final int calculatedWidth;
    private final int calculatedHeight;

    public DescriptionTooltipComponent(Component description, TooltipWidthContext widthContext, int color) {
        Font font = Minecraft.getInstance().font;
        this.widthContext = widthContext;
        this.color = color;

        int maxPixelWidth = (int) (MAX_WIDTH / TEXT_SCALE);
        this.wrappedLines = font.split(description, maxPixelWidth);

        int maxLineWidth = 0;
        for (FormattedCharSequence line : this.wrappedLines) {
            maxLineWidth = Math.max(maxLineWidth, (int) (font.width(line) * TEXT_SCALE));
        }

        this.calculatedWidth = Math.min(MAX_WIDTH, maxLineWidth + 4);
        this.widthContext.setWidth(this.calculatedWidth+WIDTH_PADDING);

        this.calculatedHeight = (this.wrappedLines.size() * LINE_SPACING) + 4;
    }

    @Override
    public int getHeight(Font font) {
        return this.calculatedHeight;
    }

    @Override
    public int getWidth(Font font) {
        return this.calculatedWidth;
    }

    @Override
    public void extractImage(Font font, int x, int y, int w, int h, GuiGraphicsExtractor graphics) {
        int renderX = x - 4;
        graphics.fill(renderX, y-2, renderX + getWidth(font), y + getHeight(font), this.color);

        var pose = graphics.pose();
        int currentY = y + 2;

        for (FormattedCharSequence line : this.wrappedLines) {
            pose.pushMatrix();
            pose.translate(x, currentY);
            pose.scale(TEXT_SCALE);

            graphics.text(font, line, 0, 0, 0xFFFFFFFF, true);
            pose.popMatrix();

            currentY += LINE_SPACING;
        }
    }
}