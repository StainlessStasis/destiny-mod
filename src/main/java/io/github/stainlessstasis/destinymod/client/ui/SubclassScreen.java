package io.github.stainlessstasis.destinymod.client.ui;

import com.lowdragmc.lowdraglib2.gui.holder.ModularUIScreen;
import com.lowdragmc.lowdraglib2.gui.texture.*;
import com.lowdragmc.lowdraglib2.gui.ui.ModularUI;
import com.lowdragmc.lowdraglib2.gui.ui.UI;
import com.lowdragmc.lowdraglib2.gui.ui.UIElement;
import com.lowdragmc.lowdraglib2.gui.ui.data.Horizontal;
import com.lowdragmc.lowdraglib2.gui.ui.elements.Label;
import com.lowdragmc.lowdraglib2.gui.ui.event.HoverTooltips;
import com.lowdragmc.lowdraglib2.gui.ui.event.UIEvents;
import dev.vfyjxf.taffy.style.*;
import io.github.stainlessstasis.destinymod.DMColor;
import io.github.stainlessstasis.destinymod.DestinyMod;
import io.github.stainlessstasis.destinymod.client.effects.ClientAudioAndVFX;
import io.github.stainlessstasis.destinymod.client.keyword.DestinyKeyword;
import io.github.stainlessstasis.destinymod.client.keyword.DestinyKeywords;
import io.github.stainlessstasis.destinymod.client.tooltip.*;
import io.github.stainlessstasis.destinymod.destiny_classes.DestinyElement;
import io.github.stainlessstasis.destinymod.destiny_classes.Subclass;
import io.github.stainlessstasis.destinymod.destiny_classes.player_equipped.PlayerSubclassData;
import io.github.stainlessstasis.destinymod.destiny_combat.ability.AbilityType;
import io.github.stainlessstasis.destinymod.registry.datapack.RegisteredAspect;
import io.github.stainlessstasis.destinymod.network.serverbound.EquipAspectsPacket;
import io.github.stainlessstasis.destinymod.tooltip.*;
import io.github.stainlessstasis.destinymod.tooltip.component.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.locale.Language;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.client.network.ClientPacketDistributor;
import org.jspecify.annotations.NonNull;

import javax.annotation.Nullable;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

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
    public static final float EQUIPPED_PANEL_BOTTOM_PERCENT = 5f;
    public static final float EQUIPPED_PANEL_GAP = 6f;

    private final Subclass subclass;
    private final AspectsHolder equippedAspects;

    public SubclassScreen(Subclass subclass) {
        this(subclass, new AspectsHolder());
    }

    private SubclassScreen(Subclass subclass, AspectsHolder aspects) {
        super(createModularUI(subclass, aspects), subclass.title());
        this.subclass = subclass;
        this.equippedAspects = aspects;
    }

    private static class AspectsHolder {
        List<RegisteredAspect> list = new ArrayList<>();
        UIElement bottomBarContainer;
    }

    private static ModularUI createModularUI(Subclass subclass, AspectsHolder equippedAspects) {
        final float guiScalar = GuiScaleUtil.getConsistencyScalar();
        final float aspectRatio = (float) Minecraft.getInstance().getWindow().getScreenWidth() / Minecraft.getInstance().getWindow().getScreenHeight();
        equippedAspects.list.addAll(PlayerSubclassData.getAllEquippedRegisteredAspects(Minecraft.getInstance().player));

        var root = new UIElement();
        root.layout(layout -> layout
                .widthPercent(100f)
                .heightPercent(100f)
                .flexDirection(FlexDirection.COLUMN)
        );

        // HEADER
        var headerContainer = new UIElement();
        headerContainer.layout(layout -> layout
                .positionType(TaffyPosition.ABSOLUTE)
                .flexDirection(FlexDirection.COLUMN)
                .leftPercent(1.5f)
                .topPercent(1.5f * aspectRatio)
                .widthAuto()
                .heightAuto()
        );

        // header - subclassID name
        var titleLabel = new Label()
                .setText(subclass.title())
                .textStyle(style -> style
                        .textAlignHorizontal(Horizontal.LEFT)
                        .textColor(subclass.destinyElement().getColor().get())
                        .textShadow(true)
                        .fontSize(TITLE_SIZE * guiScalar)
                );
        titleLabel.layout(layout -> layout.height(TITLE_SIZE * guiScalar));

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
        subtitleLabel.layout(layout -> layout.height(SUBTITLE_SIZE * guiScalar));

        headerContainer.addChildren(titleLabel, subtitleLabel);
        root.addChildren(headerContainer);

        // EQUIPPED ASPECTS TRACK
        var bottomContainer = new UIElement();
        bottomContainer.layout(layout -> layout
                .positionType(TaffyPosition.ABSOLUTE)
                .leftPercent(0f)
                .rightPercent(0f)
                .bottomPercent(EQUIPPED_PANEL_BOTTOM_PERCENT * aspectRatio)
                .flexDirection(FlexDirection.COLUMN)
                .alignItems(AlignItems.CENTER)
                .justifyContent(AlignContent.CENTER)
                .widthPercent(100f)
                .heightAuto()
        );

        var equippedRow = new UIElement();
        equippedRow.layout(layout -> layout
                .flexDirection(FlexDirection.ROW)
                .justifyContent(AlignContent.CENTER)
                .gapColumn(EQUIPPED_PANEL_GAP * guiScalar)
                .widthAuto()
                .heightAuto()
        );
        bottomContainer.addChild(equippedRow);
        equippedAspects.bottomBarContainer = equippedRow;

        rebuildBottomBar(subclass, equippedAspects, guiScalar);
        root.addChild(bottomContainer);

        // ABILITY COLUMNS & ASPECT GRIDS
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

            // main ability icon (super, melee, grenade, class)
            var mainIcon = new UIElement();
            mainIcon.layout(layout -> layout
                    .width(iconSize)
                    .height(iconSize)
            );

            // main ability tooltip
            if (iconTrack.mainAbility() != null) {
                String name = iconTrack.mainAbility().getName();
                if (!name.isEmpty()) {
                    mainIcon.addEventListener(UIEvents.HOVER_TOOLTIPS, event -> {
                        event.hoverTooltips = buildHoverTooltips(name, subclass.destinyElement());
                    });
                }
            }

            // main ability border
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

            // aspect grid stuff (shows up when hovering over main ability icon)
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

            int cellIndex = -1;
            for (int row = 0; row < ASPECT_GRID_ROWS; row++) {
                var gridRow = new UIElement();
                gridRow.layout(layout -> layout.flexDirection(FlexDirection.ROW));

                for (int col = 0; col < ASPECT_GRID_COLS; col++) {
                    cellIndex++;
                    if (cellIndex >= iconTrack.totalAspects()) {
                        break;
                    }

                    var gridCell = new UIElement();
                    gridCell.layout(layout -> layout
                            .width(iconSize)
                            .height(iconSize)
                            .marginRight(ASPECT_GRID_GAP_COLS * guiScalar)
                    );

                    // aspect tooltip & click event
                    if (iconTrack.totalAspects() > cellIndex) {
                        RegisteredAspect aspect = iconTrack.aspects().get(cellIndex);
                        String aspectName = aspect.getName();
                        gridCell.addEventListener(UIEvents.HOVER_TOOLTIPS, event -> {
                            event.hoverTooltips = buildHoverTooltips(aspectName, subclass.destinyElement(), equippedAspects, aspect);
                        });
                        gridCell.addEventListener(UIEvents.CLICK, event -> {
                            onClickAspect(subclass, equippedAspects, aspect, guiScalar);
                        });
                    }

                    // aspect border
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

                    // aspect icon
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
            // the initial hover is only when the main ability icon is hovered over, but then this is added to expand the hoverable area
            // to cover the entirety of the grid, with a bit of padding
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

            // makes hover control visibility of grid
            abilityRowTrack.addEventListener(UIEvents.MOUSE_ENTER, event -> {
                gridWrapper.setDisplay(true);
                abilityRowTrack.markTaffyStyleDirty();
            }, true);

            abilityRowTrack.addEventListener(UIEvents.MOUSE_LEAVE, event -> {
                gridWrapper.setDisplay(false);
                abilityRowTrack.markTaffyStyleDirty();
            }, true);

            abilityRowTrack.addChildren(gridWrapper, mainIcon);
            rightPanel.addChildren(abilityRowTrack);
        }

        root.addChildren(rightPanel);

        UI ui = UI.of(root);
        return ModularUI.of(ui);
    }

    private static UIElement buildAspectCell(Subclass subclass, AspectsHolder aspects, @Nullable RegisteredAspect aspect, float iconSize, float guiScalar) {
        var cell = new UIElement();
        cell.layout(layout -> layout.width(iconSize).height(iconSize));

        final IGuiTexture cellBorder = new ColorBorderTexture(-1, Color.WHITE);
        var borderLayer = new UIElement().style(style -> style.background(cellBorder));
        borderLayer.layout(layout -> layout
                .positionType(TaffyPosition.ABSOLUTE)
                .left(-1f).top(-1f)
                .width(iconSize + 2f).height(iconSize + 2f)
        );
        cell.addChild(borderLayer);

        if (aspect != null) {
            Identifier iconPath = SubclassIcons.getHudTexture(aspect.getName());
            if (iconPath != null) {
                var iconLayer = new UIElement().style(style -> style.background(SpriteTexture.of(iconPath)));
                iconLayer.layout(layout -> layout
                        .positionType(TaffyPosition.ABSOLUTE)
                        .widthPercent(100f).heightPercent(100f)
                );
                cell.addChild(iconLayer);
            }

            cell.addEventListener(UIEvents.HOVER_TOOLTIPS, event -> {
                event.hoverTooltips = buildHoverTooltips(aspect.getName(), subclass.destinyElement(), aspects, aspect);
            });
            cell.addEventListener(UIEvents.CLICK, event -> {
                onClickAspect(subclass, aspects, aspect, guiScalar);
            });
        } else {
            cell.style(style -> style.background(new ColorBorderTexture(-1, DMColor.WHITE.withOpacity(0.2f))));
        }

        return cell;
    }

    private static void rebuildBottomBar(Subclass subclass, AspectsHolder aspects, float guiScalar) {
        if (aspects.bottomBarContainer == null) return;

        aspects.bottomBarContainer.clearAllChildren();

        int maxSlots = PlayerSubclassData.getMaxAspectsEquippable(Minecraft.getInstance().player);
        List<RegisteredAspect> currentlyEquipped = new ArrayList<>(aspects.list);
        float iconSize = ICON_SIZE * guiScalar;

        for (int i = 0; i < maxSlots; i++) {
            RegisteredAspect aspectAtSlot = (i < currentlyEquipped.size()) ? currentlyEquipped.get(i) : null;
            UIElement card = buildAspectCell(subclass, aspects, aspectAtSlot, iconSize, guiScalar);
            aspects.bottomBarContainer.addChild(card);
        }

        aspects.bottomBarContainer.markTaffyStyleDirty();
    }

    private static void onClickAspect(Subclass subclass, AspectsHolder aspects, RegisteredAspect aspect, float guiScalar) {
        Player player = Minecraft.getInstance().player;
        if (player == null) {
            DestinyMod.LOGGER.error("Somehow the player was null when attempting to click an aspect in the subclass screen. This shouldn't be possible so your guess is as good as mine.");
            return;
        }
        double x = player.getX();
        double y = player.getY();
        double z = player.getZ();

        if (aspects.list.contains(aspect)) {
            aspects.list.remove(aspect);
            player.level().playSeededSound(player, x, y, z, SoundEvents.AMETHYST_BLOCK_RESONATE, SoundSource.UI, 1f, 1f, ClientAudioAndVFX.AMETHYST_RESONATE_2);
        } else {
            boolean canEquip = aspects.list.size() < PlayerSubclassData.getMaxAspectsEquippable(player) && PlayerSubclassData.hasUnlockedAspect(player, aspect);
            if (canEquip) {
                aspects.list.add(aspect);
                player.level().playSeededSound(player, x, y, z, SoundEvents.AMETHYST_BLOCK_RESONATE, SoundSource.UI, 1f, 1f, ClientAudioAndVFX.AMETHYST_RESONATE_4);
            } else {
                player.level().playSeededSound(player, x, y, z, SoundEvents.AMETHYST_BLOCK_RESONATE, SoundSource.UI, 0.7f, 1f, ClientAudioAndVFX.AMETHYST_RESONATE_3);
            }
        }

        rebuildBottomBar(subclass, aspects, guiScalar);
    }

    private static HoverTooltips buildHoverTooltips(String abilityName, DestinyElement element) {
        return buildHoverTooltips(abilityName, element, null, null);
    }

    private static HoverTooltips buildHoverTooltips(String abilityName, DestinyElement element, @Nullable AspectsHolder aspects, @Nullable RegisteredAspect aspect) {
        // MAIN TOOLTIP
        TooltipWidthContext widthContext = new TooltipWidthContext();

        String title = Language.getInstance().getOrDefault("tooltip.destinymod." + abilityName + ".title");
        String subtitle = Language.getInstance().getOrDefault("tooltip.destinymod." + abilityName + ".subtitle");

        var header = new HeaderComponent(title, subtitle, widthContext, element.getColor().withOpacity(0.95f));
        var bar = new SeparatorComponent(widthContext, 1, element.getColorLight().get());

        Component desc = DescriptionComponentParser.parseTranslatable("tooltip.destinymod." + abilityName + ".desc");
        var description = new DescriptionComponent(desc, widthContext, 0xEE222222);

        String hintText = "";
        if (aspects != null && aspect != null) {
            String equipped = aspects.list.contains(aspect) ? "unequip" : "equip";
            hintText = Language.getInstance().getOrDefault("tooltip.destinymod.action.click_to_" + equipped);
        }
        var actionHint = new ActionHintComponent(hintText, widthContext);

        List<ClientTooltipComponent> mainTooltip = List.of(
                ClientTooltipComponent.create(header),
                ClientTooltipComponent.create(bar),
                ClientTooltipComponent.create(description),
                ClientTooltipComponent.create(actionHint)
        );

        // KEYWORDS
        List<List<ClientTooltipComponent>> sideColumns = new ArrayList<>();
        List<DestinyKeyword> keywords = DestinyKeywords.getKeywordsFor(abilityName);
        if (!keywords.isEmpty()) {
            int keywordBgColor = 0xEE111111;
            List<ClientTooltipComponent> column = new ArrayList<>();

            for (int i = 0; i < keywords.size(); i++) {
                DestinyKeyword keyword = keywords.get(i);
                TooltipWidthContext kwWidth = new TooltipWidthContext();

                column.add(new KeywordDescriptionTooltipComponent(
                        keyword.getTitle(),
                        keyword.getDescription(),
                        kwWidth,
                        keywordBgColor,
                        keyword.getTitleColor()
                ));

                if (i < keywords.size() - 1) {
                    column.add(new SeparatorTooltipComponent(kwWidth, 1, DMColor.LIGHT_GRAY.get()));
                }
            }
            sideColumns.add(column);
        }

        // FINALIZE
        var wrapper = new KeywordColumnsTooltipWrapper(mainTooltip, sideColumns);
        return HoverTooltips.create(wrapper);
    }

    @Override
    public void onClose() {
        super.onClose();
        ClientPacketDistributor.sendToServer(new EquipAspectsPacket(this.equippedAspects.list));
    }

    @Override
    public void extractBackground(@NonNull GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTick) {
        // modified stuff from Screen because ModularUIScreen overrides this method with a no-op, and i want my blur back
        this.extractBlurredBackground(graphics);
        this.extractMenuBackground(graphics);
        this.minecraft.gui.extractDeferredSubtitles();
    }
}