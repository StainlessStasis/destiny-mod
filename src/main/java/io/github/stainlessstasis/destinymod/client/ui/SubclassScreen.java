package io.github.stainlessstasis.destinymod.client.ui;

import io.github.stainlessstasis.DMColor;
import io.github.stainlessstasis.destinymod.destiny_classes.Subclass;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;

import static io.github.stainlessstasis.destinymod.client.ui.UIElements.*;
import static io.github.stainlessstasis.destinymod.client.ui.UIElements.THROWING_HAMMER;
import static io.github.stainlessstasis.destinymod.client.ui.UIElements.ABILITY_ICON_SIZE;

public class SubclassScreen extends Screen {
    public static final float TITLE_SCALE = 4f;
    public static final int TITLE_X_OFFSET = 16;
    public static final int TITLE_Y_OFFSET = 12;
    public static final float SUBTITLE_SCALE = 2f;
    public static final int SUBTITLE_X_OFFSET = 16;
    public static final int SUBTITLE_Y_OFFSET = 50;
    private final Subclass subclass;

    public SubclassScreen(Subclass subclass) {
        super(subclass.title());
        this.subclass = subclass;
    }

    @Override
    public void extractRenderState(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float a) {
        super.extractRenderState(graphics, mouseX, mouseY, a);
        Minecraft mc = Minecraft.getInstance();
        float scalar = GuiScaleUtil.getConsistencyScalar();
        var pose = graphics.pose();

        // Title
        pose.pushMatrix();
        pose.translate(TITLE_X_OFFSET*scalar, TITLE_Y_OFFSET*scalar);
        pose.scale(TITLE_SCALE*scalar);
        graphics.text(font, subclass.title(), 0, 0, subclass.destinyElement().getColor(), true);
        pose.popMatrix();

        // Subtitle
        pose.pushMatrix();
        pose.translate(SUBTITLE_X_OFFSET*scalar, SUBTITLE_Y_OFFSET*scalar);
        pose.scale(SUBTITLE_SCALE*scalar);
        Component subtitle = Component.translatable("subclass.destinymod."+subclass.destinyClass().name().toLowerCase());
        graphics.text(font, subtitle, 0, 0, DMColor.GRAY.get(), true);
        pose.popMatrix();

        // Ability icon rows
        int renderX = (int) (ABILITY_ICON_SIZE/2f * scalar);
        int renderY = (int) (TITLE_Y_OFFSET * scalar);
        int iconSize = (int) (ABILITY_ICON_SIZE * scalar);
        int borderSize = iconSize + 2;

        graphics.blit(
                RenderPipelines.GUI_TEXTURED, ABILITY_BORDER,
                renderX-1, renderY-1, 0f, 0f,
                borderSize, borderSize,
                borderSize, borderSize,
                borderSize, borderSize
        );

        graphics.blit(
                RenderPipelines.GUI_TEXTURED, THROWING_HAMMER,
                renderX, renderY, 0f, 0f,
                iconSize, iconSize,
                iconSize, iconSize,
                iconSize, iconSize
        );

        graphics.blit(
                RenderPipelines.GUI_TEXTURED, THROWING_HAMMER_CHARGED,
                renderX, renderY, 0f, 0f,
                iconSize, iconSize,
                iconSize, iconSize,
                iconSize, iconSize
        );
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}
