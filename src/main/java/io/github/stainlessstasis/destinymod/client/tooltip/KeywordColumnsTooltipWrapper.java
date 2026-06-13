package io.github.stainlessstasis.destinymod.client.tooltip;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;

import java.util.List;

public class KeywordColumnsTooltipWrapper implements ClientTooltipComponent {
    private final List<ClientTooltipComponent> mainComponents;
    private final List<List<ClientTooltipComponent>> keywordColumns;
    private final int HORIZONTAL_PADDING = 8;

    public KeywordColumnsTooltipWrapper(List<ClientTooltipComponent> mainComponents, List<List<ClientTooltipComponent>> keywordColumns) {
        this.mainComponents = mainComponents;
        this.keywordColumns = keywordColumns;
    }

    @Override
    public int getHeight(Font font) {
        int maxHeight = mainComponents.stream().mapToInt(c -> c.getHeight(font)).sum();
        for (List<ClientTooltipComponent> column : keywordColumns) {
            int colHeight = column.stream().mapToInt(c -> c.getHeight(font)).sum();
            maxHeight = Math.max(maxHeight, colHeight);
        }
        return maxHeight;
    }

    @Override
    public int getWidth(Font font) {
        int totalWidth = mainComponents.stream().mapToInt(c -> c.getWidth(font)).max().orElse(0);
        for (List<ClientTooltipComponent> column : keywordColumns) {
            int colWidth = column.stream().mapToInt(c -> c.getWidth(font)).max().orElse(0);
            totalWidth += HORIZONTAL_PADDING + colWidth;
        }
        return totalWidth;
    }

    @Override
    public void extractImage(Font font, int x, int y, int w, int h, GuiGraphicsExtractor graphics) {
        int totalKeywordsWidth = 0;
        for (List<ClientTooltipComponent> column : keywordColumns) {
            int colWidth = column.stream().mapToInt(c -> c.getWidth(font)).max().orElse(0);
            totalKeywordsWidth += colWidth + HORIZONTAL_PADDING;
        }

        int mainX = x + totalKeywordsWidth;
        int currentY = y;
        int mainWidth = mainComponents.stream().mapToInt(c -> c.getWidth(font)).max().orElse(0);
        for (ClientTooltipComponent component : mainComponents) {
            int compHeight = component.getHeight(font);
            component.extractImage(font, mainX, currentY, mainWidth, compHeight, graphics);
            currentY += compHeight;
        }

        int xOffset = x;
        for (List<ClientTooltipComponent> column : keywordColumns) {
            currentY = y;
            int colWidth = column.stream().mapToInt(c -> c.getWidth(font)).max().orElse(0);

            for (ClientTooltipComponent component : column) {
                int height = component.getHeight(font);
                component.extractImage(font, xOffset, currentY, colWidth, height, graphics);
                currentY += height;
            }

            xOffset += colWidth + HORIZONTAL_PADDING;
        }
    }

    @Override
    public void extractText(GuiGraphicsExtractor graphics, Font font, int x, int y) {
        int totalKeywordsWidth = 0;
        for (List<ClientTooltipComponent> column : keywordColumns) {
            int colWidth = column.stream().mapToInt(c -> c.getWidth(font)).max().orElse(0);
            totalKeywordsWidth += colWidth + HORIZONTAL_PADDING;
        }

        int mainX = x + totalKeywordsWidth;
        int currentY = y;
        for (ClientTooltipComponent component : mainComponents) {
            component.extractText(graphics, font, mainX, currentY);
            currentY += component.getHeight(font);
        }

        int xOffset = x;
        for (List<ClientTooltipComponent> column : keywordColumns) {
            currentY = y;
            int colWidth = column.stream().mapToInt(c -> c.getWidth(font)).max().orElse(0);

            for (ClientTooltipComponent component : column) {
                component.extractText(graphics, font, xOffset, currentY);
                currentY += component.getHeight(font);
            }
            xOffset += colWidth + HORIZONTAL_PADDING;
        }
    }
}