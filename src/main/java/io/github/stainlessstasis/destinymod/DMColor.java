package io.github.stainlessstasis.destinymod;

public class DMColor {
    public static final DMColor WHITE = new DMColor(0xFFFFFFFF);
    public static final DMColor BLACK = new DMColor(0xFF000000);
    public static final DMColor LIGHT_GRAY = new DMColor(0xFFAAAAAA);
    public static final DMColor SOLAR_LIGHT = new DMColor(0xFFFC9A53);
    public static final DMColor SOLAR = new DMColor(0xFFF36F26);
    public static final DMColor SOLAR_DARK = new DMColor(0xFF9D310F);

    private final int hex;

    private DMColor(int hex) {
        this.hex = hex;
    }

    public int withOpacity(float opacity) {
        float clamped = Math.clamp(opacity, 0f, 1f);
        int alpha = Math.round(clamped * 255);
        return (this.hex & 0x00FFFFFF) | (alpha << 24);
    }

    public int get() {
        return this.hex;
    }

    public float getRed() {
        return ((this.hex >> 16) & 0xFF) / 255f;
    }

    public float getGreen() {
        return ((this.hex >> 8) & 0xFF) / 255f;
    }

    public float getBlue() {
        return (this.hex & 0xFF) / 255f;
    }

    public float getAlpha() {
        return ((this.hex >>> 24) & 0xFF) / 255f;
    }
}
