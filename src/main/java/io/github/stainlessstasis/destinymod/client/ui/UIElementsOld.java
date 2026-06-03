//package io.github.stainlessstasis.destinymod.client.ui;
//
//import io.github.stainlessstasis.destinymod.DestinyMod;
//import io.github.stainlessstasis.destinymod.destiny_classes.Subclass;
//import io.github.stainlessstasis.destinymod.destiny_classes.Subclasses;
//import io.github.stainlessstasis.destinymod.destiny_combat.ability.AbilityType;
//import net.minecraft.client.Minecraft;
//import net.minecraft.resources.Identifier;
//
//import java.util.*;
//
//public class UIElementsOld {
//    public static final Identifier ABILITY_BORDER = Identifier.fromNamespaceAndPath(DestinyMod.MODID, "textures/gui/sprites/destiny_hud/ability_border.png");
//    public static final int ABILITY_ICON_SIZE = 32;
//    public static final int ABILITY_BORDER_SIZE = ABILITY_ICON_SIZE+2;
//    public static final Identifier THROWING_HAMMER = Identifier.fromNamespaceAndPath(DestinyMod.MODID, "textures/gui/sprites/destiny_hud/throwing_hammer.png");
//    public static final Identifier THROWING_HAMMER_CHARGED = Identifier.fromNamespaceAndPath(DestinyMod.MODID, "textures/gui/sprites/destiny_hud/throwing_hammer_charged.png");
//
//    public static final Map<Subclass, SubclassIcons> SUBCLASS_ICONS = new HashMap<>();
//    static {
//        SUBCLASS_ICONS.put(Subclasses.SUNBREAKER, new SubclassIcons(
//                List.of(),
//                List.of("throwing_hammer", "melting_point"),
//                List.of(),
//                List.of(),
//                "sol_invictus"
//                )
//        );
//    }
//
//    /**
//     * Should contain only the name of the png located in textures/gui/sprites/destiny_hud.
//     * Automatically appends _charged for charged variants of sprites.
//     */
//    public record SubclassIcons(List<String> superIconNames, List<String> meleeIconNames, List<String> grenadeIconNames, List<String> classAbilityIconNames, String passiveIconName) {
//        public List<Identifier> getIcons(AbilityType type, int index) {
//            String name = getIconName(type, index);
//            if (name.isEmpty()) return List.of();
//
//            List<Identifier> textures = new ArrayList<>();
//
//            Identifier icon = getIcon(name);
//            if (Minecraft.getInstance().getResourceManager().getResource(icon).isPresent()) {
//                textures.add(icon);
//            }
//            Identifier chargedIcon = getChargedIcon(name);
//            if (Minecraft.getInstance().getResourceManager().getResource(chargedIcon).isPresent()) {
//                textures.add(chargedIcon);
//            }
//            return textures;
//        }
//
//        public Identifier getIcon(String iconName) {
//            return Identifier.fromNamespaceAndPath(DestinyMod.MODID, "textures/gui/sprites/destiny_hud/" + iconName + ".png");
//        }
//
//        public Identifier getChargedIcon(String iconName) {
//            return Identifier.fromNamespaceAndPath(DestinyMod.MODID, "textures/gui/sprites/destiny_hud/" + iconName + "_charged.png");
//        }
//
//        public Identifier getPassiveIcon() {
//            return getIcon(passiveIconName);
//        }
//
//        String getIconName(AbilityType type, int index) {
//            List<String> iconList = switch (type) {
//                case SUPER -> superIconNames;
//                case MELEE -> meleeIconNames;
//                case GRENADE -> grenadeIconNames;
//                case CLASS_ABILITY -> classAbilityIconNames;
//                default -> List.of();
//            };
//            if (index >= iconList.size()) return "";
//
//            return iconList.get(index);
//        }
//    }
//}
