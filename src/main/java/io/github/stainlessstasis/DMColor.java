package io.github.stainlessstasis;

public class DMColor {
    public static final DMColor WHITE = new DMColor(0xFFFFFFFF);
    public static final DMColor BLACK = new DMColor(0xFF000000);
    public static final DMColor LIGHT_GRAY = new DMColor(0xFFAAAAAA);
    public static final DMColor SOLAR = new DMColor(0xFFF36F26);
    public static final DMColor SOLAR_DARK = new DMColor(0xFF9D310F);

    private final int hex;

    private DMColor(int hex) {
        this.hex = hex;
    }

    public int withOpacity(float opacity) {
        float clamped = Math.max(0f, Math.min(1f, opacity));
        int alpha = Math.round(clamped * 255);
        return (this.hex & 0x00FFFFFF) | (alpha << 24);
    }

    public int get() {
        return this.hex;
    }
}
