package io.github.stainlessstasis.destinymod.client.ui;

import com.lowdragmc.lowdraglib2.gui.holder.ModularUIScreen;
import com.lowdragmc.lowdraglib2.gui.texture.ColorBorderTexture;
import com.lowdragmc.lowdraglib2.gui.texture.IGuiTexture;
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
import org.jspecify.annotations.NonNull;

import java.awt.*;

public class SubclassScreen extends ModularUIScreen {
    public static final float TITLE_SIZE = 36f;
    public static final float SUBTITLE_SIZE = 24f;

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

        // ABILITIES COLUMN (right side)
        final float iconSize = 48f * guiScalar;
        final IGuiTexture cellBorder = new ColorBorderTexture(-1, Color.WHITE);

        var rightPanel = new UIElement();
        rightPanel.layout(layout -> layout
                .positionType(TaffyPosition.ABSOLUTE)
                .rightPercent(20f)
                .topPercent(2.5f * aspectRatio)
                .heightPercent(80f)
                .flexDirection(FlexDirection.COLUMN)
                .justifyContent(AlignContent.SPACE_BETWEEN)
        );

        AbilityType[] types = {AbilityType.SUPER, AbilityType.MELEE, AbilityType.GRENADE, AbilityType.CLASS_ABILITY};
        for (AbilityType type : types) {

            var abilityRowTrack = new UIElement();
            abilityRowTrack.layout(layout -> layout
                    .flexDirection(FlexDirection.ROW)
                    .alignItems(AlignItems.START)
                    .paddingAll(2f * guiScalar)
            );

            var mainIcon = new UIElement()
                    .style(style -> style.background(cellBorder));
            mainIcon.layout(layout -> layout
                    .width(iconSize)
                    .height(iconSize)
            );
            // TODO: icons

            var aspectGrid = new UIElement();
            aspectGrid.layout(layout -> layout
                    .positionType(TaffyPosition.ABSOLUTE)
                    .left(iconSize + (24f * guiScalar))
                    .top(2f * guiScalar)
                    .flexDirection(FlexDirection.COLUMN)
                    .gapRow(4f * guiScalar)
                    .gapColumn(4f * guiScalar)
            );
            aspectGrid.setDisplay(false);

            for (int row = 0; row < 2; row++) {
                var gridRow = new UIElement();
                gridRow.layout(layout -> layout.flexDirection(FlexDirection.ROW));

                for (int col = 0; col < 3; col++) {
                    int cellIndex = (row * 3) + col + 1;

                    var gridCell = new UIElement()
                            .style(style -> style.background(cellBorder));
                    gridCell.layout(layout -> layout
                            .width(iconSize)
                            .height(iconSize)
                            .marginRight(4f * guiScalar)
                    );
                    // TODO: icons

                    gridRow.addChildren(gridCell);
                }
                aspectGrid.addChildren(gridRow);
            }

            abilityRowTrack.addEventListener(UIEvents.MOUSE_ENTER, event -> {
                aspectGrid.setDisplay(true);
                abilityRowTrack.markTaffyStyleDirty();
            }, true);

            abilityRowTrack.addEventListener(UIEvents.MOUSE_LEAVE, event -> {
                aspectGrid.setDisplay(false);
                abilityRowTrack.markTaffyStyleDirty();
            }, true);

            abilityRowTrack.addChildren(mainIcon, aspectGrid);
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
