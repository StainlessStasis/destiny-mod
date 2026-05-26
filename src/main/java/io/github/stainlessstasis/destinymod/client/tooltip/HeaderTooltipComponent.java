package io.github.stainlessstasis.destinymod.client.tooltip;

import io.github.stainlessstasis.destinymod.tooltip.TooltipWidthContext;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.network.chat.Component;

public class HeaderTooltipComponent implements ClientTooltipComponent {
    public static final float TITLE_SCALE = 1.2f;
    public static final float SUBTITLE_SCALE = 1f;
    public static final int WIDTH_PADDING = 16;
    private final String title;
    private final String subtitle;
    private final Component titleComponent;
    private final TooltipWidthContext widthContext;
    private final int color;

    public HeaderTooltipComponent(String title, String subtitle, TooltipWidthContext widthContext, int color) {
        this.title = title;
        this.subtitle = subtitle;
        this.widthContext = widthContext;
        this.color = color;
        this.titleComponent = Component.literal(title).withStyle(ChatFormatting.BOLD);
    }

    @Override
    public int getHeight(Font font) {
        return 32;
    }

    @Override
    public int getWidth(Font font) {
        int titleWidth = (int) (font.width(Component.literal(this.title).withStyle(s -> s.withBold(true))) * TITLE_SCALE) + WIDTH_PADDING;
        int subtitleWidth = (int) (font.width(this.subtitle) * SUBTITLE_SCALE) + WIDTH_PADDING;
        widthContext.setWidth(Math.max(titleWidth, subtitleWidth));

        return this.widthContext.getWidth();
    }

    @Override
    public void extractImage(Font font, int x, int y, int w, int h, GuiGraphicsExtractor graphics) {
        int renderX = x - 4;

        graphics.fill(renderX, y-2, renderX + getWidth(font), y + getHeight(font), this.color);

        var pose = graphics.pose();
        pose.pushMatrix();
        pose.translate(x, y+6);
        pose.scale(TITLE_SCALE);
        graphics.text(font, this.titleComponent, 0, 0, 0xFFFFFFFF, true);
        pose.popMatrix();

        pose.pushMatrix();
        pose.translate(x, y+18);
        pose.scale(SUBTITLE_SCALE);
        graphics.text(font, this.subtitle, 0, 0, 0xFFAAAAAA, false);
        pose.popMatrix();
    }
}
