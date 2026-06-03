package io.github.stainlessstasis.destinymod.client.ui;

import com.mojang.datafixers.util.Either;
import io.github.stainlessstasis.destinymod.DMColor;
import io.github.stainlessstasis.destinymod.destiny_classes.DestinyElement;
import io.github.stainlessstasis.destinymod.destiny_classes.Subclass;
import io.github.stainlessstasis.destinymod.destiny_combat.ability.AbilityType;
import io.github.stainlessstasis.destinymod.tooltip.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.locale.Language;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.resources.Identifier;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.ItemStack;
import org.jspecify.annotations.NonNull;

import java.util.ArrayList;
import java.util.List;

import static io.github.stainlessstasis.destinymod.client.ui.UIElements.*;
import static io.github.stainlessstasis.destinymod.client.ui.UIElements.ABILITY_ICON_SIZE;

public class SubclassScreen extends Screen {
    public static final float TITLE_SCALE = 4f;
    public static final int TITLE_X_OFFSET = 16;
    public static final int TITLE_Y_OFFSET = 12;
    public static final float SUBTITLE_SCALE = 2f;
    public static final int SUBTITLE_X_OFFSET = 16;
    public static final int SUBTITLE_Y_OFFSET = 50;
    public static final int ASPECT_GRID_ROWS = 2;
    public static final int ASPECT_GRID_COLS = 3;
    /*** The amount of padding to add to the bounds in which the mouse is checked for whether it's hovering over the aspect grid*/
    public static final int ASPECT_GRID_HOVER_PADDING = 12;
    private final Subclass subclass;

    public SubclassScreen(Subclass subclass) {
        super(subclass.title());
        this.subclass = subclass;
    }

    @Override
    public void extractRenderState(@NonNull GuiGraphicsExtractor graphics, int mouseX, int mouseY, float a) {
        super.extractRenderState(graphics, mouseX, mouseY, a);
        Minecraft mc = Minecraft.getInstance();
        float guiScalar = GuiScaleUtil.getConsistencyScalar();
        var pose = graphics.pose();
        final DMColor subclassColor = subclass.destinyElement().getColor();
        final DMColor subclassColorLight = subclass.destinyElement().getColorLight();
        final DMColor subclassColorDark = subclass.destinyElement().getColorDark();

        // Title
        pose.pushMatrix();
        pose.translate(TITLE_X_OFFSET*guiScalar, TITLE_Y_OFFSET*guiScalar);
        pose.scale(TITLE_SCALE*guiScalar);
        graphics.text(font, subclass.title(), 0, 0, subclassColor.get(), true);
        pose.popMatrix();

        // Subtitle
        pose.pushMatrix();
        pose.translate(SUBTITLE_X_OFFSET*guiScalar, SUBTITLE_Y_OFFSET*guiScalar);
        pose.scale(SUBTITLE_SCALE*guiScalar);
        Component subtitle = Component.translatable("subclass.destinymod."+subclass.destinyClass().name().toLowerCase());
        graphics.text(font, subtitle, 0, 0, DMColor.LIGHT_GRAY.get(), true);
        pose.popMatrix();

        // Ability icon rows
        final int scaledScreenWidth = mc.getWindow().getGuiScaledWidth();
        final int scaledScreenHeight = mc.getWindow().getGuiScaledHeight();
        final int iconSize = (int) (48 * guiScalar); // the icons are 32x32, but they are rendered at 48x48 in this menu
        final int borderSize = iconSize + 2;
        int renderX = (int) (scaledScreenWidth - (iconSize*5f));
        int renderY = (int) (TITLE_Y_OFFSET * guiScalar);

        // each of these components is already scaled, so no scalar, or it will break
        final int aspectGridVerticalSpacing = (int) ((scaledScreenHeight - (renderY*2) - (iconSize*2)) / 3f);
        for (int i = 0; i < 4; i++) {
            AbilityType abilityType = switch(i) {
                case 0 -> AbilityType.SUPER;
                case 1 -> AbilityType.MELEE;
                case 2 -> AbilityType.GRENADE;
                case 3 -> AbilityType.CLASS_ABILITY;
                default -> throw new IllegalStateException("Unexpected value: " + i);
            };

            // The main 4 icons (super, melee, grenade, class)
            List<Identifier> abilityIcons = getAbilityIcons(abilityType, 0);
            for (Identifier texture : abilityIcons) {
                graphics.blit(
                        RenderPipelines.GUI_TEXTURED, texture,
                        renderX, renderY, 0f, 0f,
                        iconSize, iconSize,
                        iconSize, iconSize,
                        iconSize, iconSize
                );
            }

            // Ability tooltips
            if (!abilityIcons.isEmpty() && isHovering(mouseX, mouseY, renderX, renderY, iconSize)) {
                List<String> abilityNames = getAbilityIconNames(abilityType);
                if (!abilityNames.isEmpty()) {
                    createTooltip(graphics, abilityNames.getFirst(), mouseX, mouseY, subclassColorDark);
                }
            }

            // Ability border
            graphics.blit(
                    RenderPipelines.GUI_TEXTURED, ABILITY_BORDER,
                    renderX-1, renderY-1, 0f, 0f,
                    borderSize, borderSize,
                    borderSize, borderSize,
                    borderSize, borderSize,
                    subclassColor.get()
            );

            // Aspect icons grid
            final int gridRowOffsetFromAbility = iconSize + (iconSize/4);
            final int gridStartingX = renderX + gridRowOffsetFromAbility;
            final int gridSpacingX = iconSize + (iconSize/8) + 1;
            final int gridSpacingY = iconSize + (iconSize/8) + 1;
            int gridTotalWidth = (ASPECT_GRID_COLS * iconSize) + ((ASPECT_GRID_COLS - 1) * ((iconSize / 8) + 1));
            int gridTotalHeight = (ASPECT_GRID_ROWS * iconSize) + ((ASPECT_GRID_ROWS - 1) * ((iconSize / 8) + 1));

            int gridBoundsPadding = (int) (ASPECT_GRID_HOVER_PADDING*guiScalar);
            if (isHoveringWithinBounds(
                    mouseX, mouseY,
                    renderX-gridBoundsPadding, renderY-gridBoundsPadding,
                    gridRowOffsetFromAbility+gridTotalWidth+(gridBoundsPadding*2), gridTotalHeight+(gridBoundsPadding*2))
            ) {

                for (int row = 0; row < ASPECT_GRID_ROWS; row++) {
                    for (int col = 0; col < ASPECT_GRID_COLS; col++) {
                        int cellIndex = (row * ASPECT_GRID_COLS) + col;
                        List<Identifier> aspectIcons = getAbilityIcons(abilityType, cellIndex + 1);
                        int gridX = gridStartingX + (col * gridSpacingX);
                        int gridY = renderY + (row * gridSpacingY);

                        graphics.blit(
                                RenderPipelines.GUI_TEXTURED, ABILITY_BORDER,
                                gridX - 1, gridY - 1, 0f, 0f,
                                borderSize, borderSize,
                                borderSize, borderSize,
                                borderSize, borderSize
                        );

                        if (aspectIcons.isEmpty()) {
                            graphics.fill(gridX, gridY, gridX+iconSize, gridY+iconSize, DMColor.BLACK.withOpacity(0.25f));
                            continue;
                        }

                        for (Identifier texture : aspectIcons) {
                            graphics.blit(
                                    RenderPipelines.GUI_TEXTURED, texture,
                                    gridX, gridY, 0f, 0f,
                                    iconSize, iconSize,
                                    iconSize, iconSize,
                                    iconSize, iconSize
                            );
                        }

                        if (isHovering(mouseX, mouseY, gridX, gridY, iconSize)) {
                            List<String> aspectNames = getAbilityIconNames(abilityType);
                            if (cellIndex + 1 < aspectNames.size()) {
                                createTooltip(graphics, aspectNames.get(cellIndex + 1), mouseX, mouseY, subclassColor);
                            }
                        }
                    }
                }
            }

            renderY += aspectGridVerticalSpacing;
        }

        // Passive ability (bottom left corner)
        final int passiveBorderSize = (int) (64 * guiScalar);
        renderX = (int) (TITLE_X_OFFSET*guiScalar);
        renderY = (int) (scaledScreenHeight-(70*guiScalar));
        graphics.blit(
                RenderPipelines.GUI_TEXTURED, ABILITY_BORDER,
                renderX, renderY, 0f, 0f,
                passiveBorderSize, passiveBorderSize,
                passiveBorderSize, passiveBorderSize,
                passiveBorderSize, passiveBorderSize,
                subclassColor.get()
        );
    }

    private void createTooltip(GuiGraphicsExtractor graphics, String abilityName, int x, int y, DMColor headerColor) {
        List<Either<FormattedText, TooltipComponent>> elements = new ArrayList<>();
        TooltipWidthContext widthContext = new TooltipWidthContext();

        String title = Language.getInstance().getOrDefault("tooltip.destinymod." + abilityName + ".title");
        String subtitle = Language.getInstance().getOrDefault("tooltip.destinymod." + abilityName + ".subtitle");

        var header = new HeaderComponent(title, subtitle, widthContext, headerColor.withOpacity(0.95f));
        elements.add(Either.right(header));

        var bar = new SeparatorComponent(widthContext, 1, 0xFFF27149);
        elements.add(Either.right(bar));

        Component desc = DescriptionComponentParser.parseTranslatable("tooltip.destinymod." + abilityName + ".desc");
        var description = new DescriptionComponent(desc, widthContext, 0xEE222222);
        elements.add(Either.right(description));

        graphics.setComponentTooltipFromElementsForNextFrame(this.font, elements, x, y, ItemStack.EMPTY);
    }

    private List<String> getAbilityIconNames(AbilityType abilityType) {
        var subclassIcons = SUBCLASS_ICONS.get(subclass);
        return switch (abilityType) {
            case SUPER -> subclassIcons.superIcons();
            case MELEE -> subclassIcons.meleeIcons();
            case GRENADE -> subclassIcons.grenadeIcons();
            case CLASS_ABILITY -> subclassIcons.classAbilityIcons();
            default -> List.of();
        };
    }

    private List<Identifier> getAbilityIcons(AbilityType abilityType, int index) {
        return SUBCLASS_ICONS.get(subclass).getIcons(abilityType, index);
    }

    private boolean isHovering(int mouseX, int mouseY, int x, int y, int scale) {
        return mouseX >= x && mouseX < x + scale && mouseY >= y && mouseY < y + scale;
    }

    private boolean isHoveringWithinBounds(int mouseX, int mouseY, int minX, int minY, int width, int height) {
        return mouseX >= minX && mouseX < minX + width && mouseY >= minY && mouseY < minY + height;
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}
