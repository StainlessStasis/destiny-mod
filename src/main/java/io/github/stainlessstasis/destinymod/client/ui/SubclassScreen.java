package io.github.stainlessstasis.destinymod.client.ui;

import com.lowdragmc.lowdraglib2.gui.holder.ModularUIScreen;
import com.lowdragmc.lowdraglib2.gui.ui.ModularUI;
import com.lowdragmc.lowdraglib2.gui.ui.UI;
import com.lowdragmc.lowdraglib2.gui.ui.UIElement;
import com.lowdragmc.lowdraglib2.gui.ui.data.Horizontal;
import com.lowdragmc.lowdraglib2.gui.ui.elements.Label;
import dev.vfyjxf.taffy.style.FlexDirection;
import dev.vfyjxf.taffy.style.TaffyPosition;
import io.github.stainlessstasis.destinymod.DMColor;
import io.github.stainlessstasis.destinymod.destiny_classes.Subclass;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.network.chat.Component;
import org.jspecify.annotations.NonNull;

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
                .widthPercent(100)
                .heightPercent(100)
                .flexDirection(FlexDirection.COLUMN)
        );

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
