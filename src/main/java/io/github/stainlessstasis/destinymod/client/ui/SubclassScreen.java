package io.github.stainlessstasis.destinymod.client.ui;

import com.lowdragmc.lowdraglib2.gui.holder.ModularUIScreen;
import com.lowdragmc.lowdraglib2.gui.texture.*;
import com.lowdragmc.lowdraglib2.gui.ui.ModularUI;
import com.lowdragmc.lowdraglib2.gui.ui.UI;
import com.lowdragmc.lowdraglib2.gui.ui.UIElement;
import com.lowdragmc.lowdraglib2.gui.ui.data.Horizontal;
import com.lowdragmc.lowdraglib2.gui.ui.elements.Label;
import com.lowdragmc.lowdraglib2.gui.ui.event.UIEvents;
import dev.vfyjxf.taffy.style.AlignContent;
import dev.vfyjxf.taffy.style.AlignItems;
import dev.vfyjxf.taffy.style.FlexDirection;
import dev.vfyjxf.taffy.style.TaffyPosition;
import io.github.stainlessstasis.destinymod.DMColor;
import io.github.stainlessstasis.destinymod.destiny_classes.Subclass;
import io.github.stainlessstasis.destinymod.destiny_combat.ability.AbilityType;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import org.jspecify.annotations.NonNull;

import java.awt.*;

public class SubclassScreen extends ModularUIScreen {
    public static final float TITLE_SIZE = 36f;
    public static final float SUBTITLE_SIZE = 24f;

    public static final float ICON_SIZE = 48f;
    public static final float ABILITY_PANEL_RIGHT_PERCENT = 20f;
    public static final float ABILITY_PANEL_TOP_PERCENT = 2.5f;
    public static final float ABILITY_PANEL_HEIGHT_PERCENT = 80f;
    public static final float ABILITY_PANEL_ROW_PADDING = 2f;
    public static final float ASPECT_GRID_OFFSET_LEFT = 24f;
    public static final float ASPECT_GRID_OFFSET_TOP = 2f;
    public static final float ASPECT_GRID_GAP_ROWS = 4f;
    public static final float ASPECT_GRID_GAP_COLS = 4f;
    public static final int ASPECT_GRID_ROWS = 2;
    public static final int ASPECT_GRID_COLS = 3;
    public static final int HOVER_PADDING = 8;

    private final Subclass subclass;

    public SubclassScreen(Subclass subclass) {
        super(createModularUI(subclass), subclass.title());
        this.subclass = subclass;
    }

    private static ModularUI createModularUI(Subclass subclass) {
        final float guiScalar = GuiScaleUtil.getConsistencyScalar();
        final float aspectRatio = (float) Minecraft.getInstance().getWindow().getScreenWidth() / Minecraft.getInstance().getWindow().getScreenHeight();

        var root = new UIElement();
        root.layout(layout -> layout
                .widthPercent(100f)
                .heightPercent(100f)
                .flexDirection(FlexDirection.COLUMN)
        );

        // HEADER (top left)
        var headerContainer = new UIElement();
        headerContainer.layout(layout -> layout
                .positionType(TaffyPosition.ABSOLUTE)
                .flexDirection(FlexDirection.COLUMN)
                .leftPercent(1.5f)
                .topPercent(1.5f * aspectRatio)
                .widthAuto()
                .heightAuto()
        );

        // header - subclass name
        var titleLabel = new Label()
                .setText(subclass.title())
                .textStyle(style -> style
                        .textAlignHorizontal(Horizontal.LEFT)
                        .textColor(subclass.destinyElement().getColor().get())
                        .textShadow(true)
                        .fontSize(TITLE_SIZE * guiScalar)
                );
        titleLabel.layout(layout -> layout
                .height(TITLE_SIZE * guiScalar)
        );

        // header - class subtitle
        Component subtitleText = Component.translatable("subclass.destinymod." + subclass.destinyClass().name().toLowerCase());
        var subtitleLabel = new Label()
                .setText(subtitleText)
                .textStyle(style -> style
                        .textAlignHorizontal(Horizontal.LEFT)
                        .textColor(DMColor.LIGHT_GRAY.get())
                        .textShadow(true)
                        .fontSize(SUBTITLE_SIZE * guiScalar)
                );
        subtitleLabel.layout(layout -> layout
                .height(SUBTITLE_SIZE * guiScalar)
        );

        headerContainer.addChildren(titleLabel, subtitleLabel);
        root.addChildren(headerContainer);

        // ABILITY COLUMNS & HOVER GRIDS (right side)
        final float iconSize = ICON_SIZE * guiScalar;
        final var iconSet = SubclassIcons.SUBCLASS_ICONS.get(subclass);
        final IGuiTexture cellBorder = new ColorBorderTexture(-1, Color.WHITE);

        var rightPanel = new UIElement();
        rightPanel.layout(layout -> layout
                .positionType(TaffyPosition.ABSOLUTE)
                .rightPercent(ABILITY_PANEL_RIGHT_PERCENT)
                .topPercent(ABILITY_PANEL_TOP_PERCENT * aspectRatio)
                .heightPercent(ABILITY_PANEL_HEIGHT_PERCENT)
                .flexDirection(FlexDirection.COLUMN)
                .justifyContent(AlignContent.SPACE_BETWEEN)
        );

        AbilityType[] types = {AbilityType.SUPER, AbilityType.MELEE, AbilityType.GRENADE, AbilityType.CLASS_ABILITY};
        for (AbilityType type : types) {
            var iconTrack = iconSet.getTrack(type);
            var abilityRowTrack = new UIElement();
            abilityRowTrack.layout(layout -> layout
                    .flexDirection(FlexDirection.ROW)
                    .alignItems(AlignItems.START)
                    .paddingAll(ABILITY_PANEL_ROW_PADDING * guiScalar)
            );

            var mainIcon = new UIElement();
            mainIcon.layout(layout -> layout
                    .width(iconSize)
                    .height(iconSize)
            );

            var mainBorderLayer = new UIElement()
                    .style(style -> style.background(cellBorder));
            mainBorderLayer.layout(layout -> layout
                    .positionType(TaffyPosition.ABSOLUTE)
                    .left(-1f)
                    .top(-1f)
                    .width(iconSize + 2f)
                    .height(iconSize + 2f)
            );
            mainIcon.addChildren(mainBorderLayer);

            for (int i = 0; i < 2; i++) {
                boolean isBaseIcon = i == 1; // the base icon and the background are separate for abilities, since they can be charged or uncharged.
                Identifier iconPath = iconTrack.getMainIcon(isBaseIcon);
                if (iconPath != null) {
                    var chargedLayer = new UIElement()
                            .style(style -> style.background(SpriteTexture.of(iconPath)));
                    chargedLayer.layout(layout -> layout
                            .positionType(TaffyPosition.ABSOLUTE)
                            .widthPercent(100f)
                            .heightPercent(100f)
                            .left(0f)
                            .top(0f)
                    );
                    mainIcon.addChildren(chargedLayer);
                }
            }

            var aspectGrid = new UIElement();
            aspectGrid.layout(layout -> layout
                    .flexDirection(FlexDirection.COLUMN)
                    .gapRow(ASPECT_GRID_GAP_ROWS * guiScalar)
                    .gapColumn(ASPECT_GRID_GAP_COLS * guiScalar)
            );

            var gridWrapper = new UIElement();
            gridWrapper.layout(layout -> layout
                    .positionType(TaffyPosition.ABSOLUTE)
                    .left(iconSize + (ASPECT_GRID_OFFSET_LEFT * guiScalar))
                    .top(ASPECT_GRID_OFFSET_TOP * guiScalar)
                    .flexDirection(FlexDirection.COLUMN)
            );
            gridWrapper.setDisplay(false);

            for (int row = 0; row < ASPECT_GRID_ROWS; row++) {
                var gridRow = new UIElement();
                gridRow.layout(layout -> layout.flexDirection(FlexDirection.ROW));

                for (int col = 0; col < ASPECT_GRID_COLS; col++) {
                    int cellIndex = (row * ASPECT_GRID_COLS) + col;

                    var gridCell = new UIElement();
                    gridCell.layout(layout -> layout
                            .width(iconSize)
                            .height(iconSize)
                            .marginRight(ASPECT_GRID_GAP_COLS * guiScalar)
                    );

                    var gridBorderLayer = new UIElement()
                            .style(style -> style.background(cellBorder));
                    gridBorderLayer.layout(layout -> layout
                            .positionType(TaffyPosition.ABSOLUTE)
                            .left(-1f)
                            .top(-1f)
                            .width(iconSize + 2f)
                            .height(iconSize + 2f)
                    );
                    gridCell.addChildren(gridBorderLayer);

                    if (cellIndex < iconTrack.totalAspects()) {
                        Identifier iconPath = iconTrack.getAspectIcon(cellIndex);
                        if (iconPath != null) {
                            var aspectIconLayer = new UIElement()
                                    .style(style -> style.background(SpriteTexture.of(iconPath)));
                            aspectIconLayer.layout(layout -> layout
                                    .positionType(TaffyPosition.ABSOLUTE)
                                    .widthPercent(100f)
                                    .heightPercent(100f)
                                    .left(0f)
                                    .top(0f)
                            );
                            gridCell.addChildren(aspectIconLayer);
                        }
                    }

                    gridRow.addChildren(gridCell);
                }
                aspectGrid.addChildren(gridRow);
            }

            final float totalGridHeight = (iconSize * ASPECT_GRID_ROWS) + (ASPECT_GRID_GAP_ROWS * guiScalar * (ASPECT_GRID_ROWS - 1));
            final float totalGridWidth = (iconSize * ASPECT_GRID_COLS) + (ASPECT_GRID_GAP_COLS * guiScalar * (ASPECT_GRID_COLS - 1));
            final float gapToGrid = iconSize + (ASPECT_GRID_OFFSET_LEFT * guiScalar);
            final float hoverPadding = HOVER_PADDING * guiScalar;
            final float totalBridgeWidth = gapToGrid + totalGridWidth;
            var hoverBridge = new UIElement()
                    .style(style -> style.background(new ColorRectTexture(subclass.destinyElement().getColorDark().withOpacity(0.1f))));
            hoverBridge.layout(layout -> layout
                    .positionType(TaffyPosition.ABSOLUTE)
                    .left(-gapToGrid - hoverPadding)
                    .top(-hoverPadding)
                    .width(totalBridgeWidth + (hoverPadding * 2f))
                    .height(totalGridHeight + (hoverPadding * 2f))
            );
            hoverBridge.addChildren(aspectGrid);

            aspectGrid.layout(layout -> layout
                    .positionType(TaffyPosition.ABSOLUTE)
                    .left(gapToGrid + hoverPadding)
                    .top(hoverPadding)
                    .flexDirection(FlexDirection.COLUMN)
                    .gapRow(ASPECT_GRID_GAP_ROWS * guiScalar)
                    .gapColumn(ASPECT_GRID_GAP_COLS * guiScalar)
            );
            gridWrapper.addChildren(hoverBridge);

            abilityRowTrack.addEventListener(UIEvents.MOUSE_ENTER, event -> {
                gridWrapper.setDisplay(true);
                abilityRowTrack.markTaffyStyleDirty();
            }, true);

            abilityRowTrack.addEventListener(UIEvents.MOUSE_LEAVE, event -> {
                gridWrapper.setDisplay(false);
                abilityRowTrack.markTaffyStyleDirty();
            }, true);

            abilityRowTrack.addChildren(mainIcon, gridWrapper);
            rightPanel.addChildren(abilityRowTrack);
        }

        root.addChildren(rightPanel);

        UI ui = UI.of(root);
        return ModularUI.of(ui);
    }

    @Override
    public void extractBackground(@NonNull GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTick) {
        // modified stuff from Screen because ModularUIScreen overrides this method with a no-op, and i want my blur back
        this.extractBlurredBackground(graphics);
        this.extractMenuBackground(graphics);
        this.minecraft.gui.extractDeferredSubtitles();
    }
}
