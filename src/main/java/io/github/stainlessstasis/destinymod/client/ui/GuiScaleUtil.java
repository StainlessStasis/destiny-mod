package io.github.stainlessstasis.destinymod.client.ui;

import net.minecraft.client.Minecraft;

public class GuiScaleUtil {
    public static float getConsistencyScalar() {
        int guiScale = Minecraft.getInstance().options.guiScale().get();
        return switch (guiScale) {
            case 1 -> 2f;
            case 2 -> 1f;
            case 3 -> 2/3f;
            case 0, 4 -> 0.5f;
            default -> 1f;
        };
    }

    public static float getConsistentScale(float value) {
        return value * getConsistencyScalar();
    }

    public static int getConsistentScale(int value) {
        return (int) (value * getConsistencyScalar());
    }
}
