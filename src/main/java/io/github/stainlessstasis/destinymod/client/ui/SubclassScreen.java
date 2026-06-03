package io.github.stainlessstasis.destinymod.client.ui;

import com.lowdragmc.lowdraglib2.gui.holder.ModularUIScreen;
import com.lowdragmc.lowdraglib2.gui.texture.SpriteTexture;
import com.lowdragmc.lowdraglib2.gui.ui.ModularUI;
import com.lowdragmc.lowdraglib2.gui.ui.UI;
import com.lowdragmc.lowdraglib2.gui.ui.UIElement;
import com.lowdragmc.lowdraglib2.gui.ui.data.Horizontal;
import com.lowdragmc.lowdraglib2.gui.ui.elements.Button;
import com.lowdragmc.lowdraglib2.gui.ui.elements.Label;
import com.lowdragmc.lowdraglib2.gui.ui.styletemplate.Sprites;
import io.github.stainlessstasis.destinymod.destiny_classes.Subclass;

public class SubclassScreen extends ModularUIScreen {
    private final Subclass subclass;

    public SubclassScreen(Subclass subclass) {
        super(createModularUI(), subclass.title());
        this.subclass = subclass;
    }

    private static ModularUI createModularUI() {
        var root = new UIElement();
        root.addChildren(
                new Label().setText("My First UI")
                        .textStyle(textStyle -> textStyle.textAlignHorizontal(Horizontal.CENTER)),
                new Button().setText("Click Me!"),
                new UIElement().layout(layout -> layout.width(80).height(80))
                        .style(style -> style.background(
                                SpriteTexture.of("ldlib2:textures/gui/icon.png"))
                        )
        ).style(style -> style.background(Sprites.BORDER));
        root.layout(layout -> layout.paddingAll(7).gapAll(5));
        var ui = UI.of(root);
        return ModularUI.of(ui);
    }
}
