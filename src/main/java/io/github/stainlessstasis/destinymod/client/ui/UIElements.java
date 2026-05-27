package io.github.stainlessstasis.destinymod.client.ui;

import io.github.stainlessstasis.destinymod.DestinyMod;
import io.github.stainlessstasis.destinymod.destiny_classes.Subclass;
import io.github.stainlessstasis.destinymod.destiny_classes.Subclasses;
import io.github.stainlessstasis.destinymod.destiny_classes.ability.AbilityType;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.resources.ResourceManager;

import java.util.*;

public class UIElements {
    public static final Identifier ABILITY_BORDER = Identifier.fromNamespaceAndPath(DestinyMod.MODID, "textures/gui/sprites/destiny_hud/ability_border.png");
    public static final int ABILITY_BORDER_SIZE = 34;
    public static final int ABILITY_ICON_SIZE = 32;
    public static final Identifier THROWING_HAMMER = Identifier.fromNamespaceAndPath(DestinyMod.MODID, "textures/gui/sprites/destiny_hud/throwing_hammer.png");
    public static final Identifier THROWING_HAMMER_CHARGED = Identifier.fromNamespaceAndPath(DestinyMod.MODID, "textures/gui/sprites/destiny_hud/throwing_hammer_charged.png");

    public static final Map<Subclass, SubclassIcons> SUBCLASS_ICONS = new HashMap<>();
    static {
        SUBCLASS_ICONS.put(Subclasses.SUNBREAKER, new SubclassIcons(
                List.of(),
                List.of("throwing_hammer", "melting_point"),
                List.of(),
                List.of())
        );
    }

    /**
     * Should contain only the name of the png located in textures/gui/sprites/destiny_hud.
     * Automatically appends _charged for charged variants of sprites.
     */
    public record SubclassIcons(List<String> superIcons, List<String> meleeIcons, List<String> grenadeIcons, List<String> classAbilityIcons) {
        public List<Identifier> getIcons(AbilityType type, int index) {
            List<String> iconList = switch (type) {
                case SUPER -> superIcons;
                case MELEE -> meleeIcons;
                case GRENADE -> grenadeIcons;
                case CLASS_ABILITY -> classAbilityIcons;
            };
            if (index >= iconList.size()) return List.of();

            String name = iconList.get(index);
            List<Identifier> textures = new ArrayList<>();
            ResourceManager resourceManager = Minecraft.getInstance().getResourceManager();

            Identifier baseTexture = Identifier.fromNamespaceAndPath(DestinyMod.MODID, "textures/gui/sprites/destiny_hud/" + name + ".png");
            textures.add(baseTexture);
            Identifier chargedTexture = Identifier.fromNamespaceAndPath(DestinyMod.MODID, "textures/gui/sprites/destiny_hud/" + name + "_charged.png");
            if (resourceManager.getResource(chargedTexture).isPresent()) {
                textures.add(chargedTexture);
            }
            return textures;
        }
    }
}
