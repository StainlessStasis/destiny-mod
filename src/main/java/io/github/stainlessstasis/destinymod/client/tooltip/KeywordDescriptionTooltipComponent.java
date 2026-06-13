package io.github.stainlessstasis.destinymod.client.tooltip;

import io.github.stainlessstasis.destinymod.tooltip.TooltipWidthContext;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.network.chat.Component;
import net.minecraft.util.FormattedCharSequence;

import java.util.List;

public class KeywordDescriptionTooltipComponent implements ClientTooltipComponent {
    public static final int MAX_WIDTH = 180;
    public static final int WIDTH_PADDING = 12;
    public static final int HEIGHT_PADDING = 10;
    public static final float TITLE_SCALE = 1.1f;
    public static final float DESC_SCALE = 1f;
    public static final int TITLE_LINE_SPACING = 12;
    public static final int DESC_LINE_SPACING = 10;

    private final FormattedCharSequence titleLine;
    private final List<FormattedCharSequence> wrappedDescLines;
    private final int backgroundColor;
    private final int titleTextColor;
    private final int calculatedWidth;
    private final int calculatedHeight;

    public KeywordDescriptionTooltipComponent(Component title, Component description, TooltipWidthContext widthContext, int backgroundColor, int titleTextColor) {
        Font font = Minecraft.getInstance().font;
        this.backgroundColor = backgroundColor;
        this.titleTextColor = titleTextColor;

        this.titleLine = title.getVisualOrderText();
        int maxTitleWidth = (int) (font.width(this.titleLine) * TITLE_SCALE);

        int maxDescPixelWidth = (int) (MAX_WIDTH / DESC_SCALE);
        this.wrappedDescLines = font.split(description, maxDescPixelWidth);

        int maxDescLineWidth = 0;
        for (FormattedCharSequence line : this.wrappedDescLines) {
            maxDescLineWidth = Math.max(maxDescLineWidth, (int) (font.width(line) * DESC_SCALE));
        }

        int maxContentWidth = Math.max(maxTitleWidth, maxDescLineWidth);
        this.calculatedWidth = Math.min(MAX_WIDTH, maxContentWidth) + WIDTH_PADDING;
        widthContext.setWidth(this.calculatedWidth);

        this.calculatedHeight = TITLE_LINE_SPACING + 4 + (this.wrappedDescLines.size() * DESC_LINE_SPACING) + HEIGHT_PADDING;
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
        int renderX = x - 2;
        graphics.fill(renderX, y - 2, renderX + getWidth(font), y + getHeight(font) - 2, this.backgroundColor);

        var pose = graphics.pose();
        int currentY = y + 2;

        pose.pushMatrix();
        pose.translate(x + 2, currentY);
        pose.scale(TITLE_SCALE);
        graphics.text(font, this.titleLine, 0, 0, this.titleTextColor, true);
        pose.popMatrix();

        currentY += TITLE_LINE_SPACING + 4;
        for (FormattedCharSequence line : this.wrappedDescLines) {
            pose.pushMatrix();
            pose.translate(x + 2, currentY);
            pose.scale(DESC_SCALE);
            graphics.text(font, line, 0, 0, 0xFFE0E0E0, true);
            pose.popMatrix();

            currentY += DESC_LINE_SPACING;
        }
    }
}
