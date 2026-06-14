package io.github.stainlessstasis.destinymod.client.keyword;

import io.github.stainlessstasis.destinymod.DMColor;
import io.github.stainlessstasis.destinymod.tooltip.component.DescriptionComponentParser;
import net.minecraft.locale.Language;
import net.minecraft.network.chat.Component;

public enum DestinyKeyword {
    SCORCH("scorch", DMColor.SOLAR),
    IGNITION("ignition", DMColor.SOLAR);

    private final String id;
    private final DMColor elementColor;

    DestinyKeyword(String id, DMColor elementColor) {
        this.id = id;
        this.elementColor = elementColor;
    }

    public String getID() { return this.id; }
    public int getTitleColor() { return this.elementColor.get(); }

    public Component getTitle() {
        return Component.literal(Language.getInstance().getOrDefault("tooltip.destinymod.keyword." + id + ".title"));
    }

    public Component getDescription() {
        return DescriptionComponentParser.parseTranslatable("tooltip.destinymod.keyword." + id + ".desc");
    }
}
