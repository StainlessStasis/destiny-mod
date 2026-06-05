package io.github.stainlessstasis.destinymod.client.tooltip;

import io.github.stainlessstasis.destinymod.tooltip.TooltipWidthContext;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;

public class ActionHintTooltipComponent implements ClientTooltipComponent {
    private static final int BAR_HEIGHT = 18;
    private static final int HALF_BAR_HEIGHT = BAR_HEIGHT/2;
    private static final int TEXT_RIGHT_PADDING = 2;
    private static final int BACKGROUND_COLOR = 0xCC0b0d12;

    private final String hint;
    private final TooltipWidthContext widthContext;

    public ActionHintTooltipComponent(String hint, TooltipWidthContext widthContext) {
        this.hint = hint;
        this.widthContext = widthContext;
    }

    @Override
    public int getHeight(Font font) {
        return BAR_HEIGHT;
    }

    @Override
    public int getWidth(Font font) {
        return this.widthContext.getWidth();
    }

    @Override
    public void extractImage(Font font, int x, int y, int w, int h, GuiGraphicsExtractor graphics) {
        int totalWidth = getWidth(font);
        int textWidth = font.width(this.hint);
        int renderX = x - 2;
        int renderWidth = totalWidth + 4;
        graphics.fill(renderX, y, renderX + renderWidth, y + BAR_HEIGHT, BACKGROUND_COLOR);

        int alignedX = (x + totalWidth) - textWidth - TEXT_RIGHT_PADDING;
        int centerY = y + (HALF_BAR_HEIGHT / 2);
        graphics.text(font, this.hint, alignedX, centerY, 0xFFFFFFFF, true);
    }
}
