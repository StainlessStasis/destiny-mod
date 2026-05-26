package io.github.stainlessstasis.destinymod.tooltip;

public class TooltipWidthContext {
    private int width = 0;

    public int getWidth() {
        return this.width;
    }

    public void setWidth(int width) {
        if (width > this.width) {
            this.width = width;
        }
    }
}
