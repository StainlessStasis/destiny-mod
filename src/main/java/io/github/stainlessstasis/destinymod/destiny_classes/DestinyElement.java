package io.github.stainlessstasis.destinymod.destiny_classes;

import io.github.stainlessstasis.DMColor;

public enum DestinyElement {
    SOLAR(DMColor.SOLAR.get()),
    ARC(DMColor.GRAY.get()),
    VOID(DMColor.GRAY.get()),
    STASIS(DMColor.GRAY.get()),
    STRAND(DMColor.GRAY.get());

    private final int color;

    DestinyElement(int color) {
        this.color = color;
    }

    public int getColor() {
        return this.color;
    }
}
