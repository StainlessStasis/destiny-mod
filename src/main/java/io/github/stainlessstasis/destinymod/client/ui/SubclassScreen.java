package io.github.stainlessstasis.destinymod.client.ui;

import io.github.stainlessstasis.destinymod.destiny_classes.Subclass;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

public class SubclassScreen extends Screen {
    private final Subclass subclass;

    public SubclassScreen(Subclass subclass) {
        super(subclass.title());
        this.subclass = subclass;
    }
}
