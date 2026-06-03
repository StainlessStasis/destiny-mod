package io.github.stainlessstasis.destinymod.client.ui;

import io.github.stainlessstasis.destinymod.DestinyMod;
import io.github.stainlessstasis.destinymod.destiny_classes.Subclass;
import io.github.stainlessstasis.destinymod.destiny_classes.Subclasses;
import io.github.stainlessstasis.destinymod.destiny_combat.ability.AbilityType;
import net.minecraft.resources.Identifier;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SubclassIcons {
    public static final Map<Subclass, SubclassIconSet> SUBCLASS_ICONS = new HashMap<>();

    static {
        SUBCLASS_ICONS.put(Subclasses.SUNBREAKER, new SubclassIconSet(
                new AbilityTrack("", List.of()),
                new AbilityTrack("throwing_hammer", List.of("melting_point", "heatseeker", "anvil_drop")),
                new AbilityTrack("", List.of()),
                new AbilityTrack("", List.of()),
                "sol_invictus"
        ));
    }

    public static Identifier getHudTexture(String name) {
        if (name == null || name.isEmpty()) return null;
        return DestinyMod.id("textures/gui/sprites/destiny_hud/" + name + ".png");
    }

    public record AbilityTrack(String mainAbilityName, List<String> aspectNames) {
        public Identifier getMainIcon(boolean charged) {
            if (mainAbilityName == null || mainAbilityName.isEmpty()) return null;
            String suffix = charged ? "_charged" : "";
            return getHudTexture(mainAbilityName + suffix);
        }

        public Identifier getAspectIcon(int aspectIndex) {
            if (aspectIndex < 0 || aspectIndex >= aspectNames.size()) return null;
            return getHudTexture(aspectNames.get(aspectIndex));
        }

        public int totalAspects() {
            return aspectNames.size();
        }
    }

    public record SubclassIconSet(
            AbilityTrack superTrack,
            AbilityTrack meleeTrack,
            AbilityTrack grenadeTrack,
            AbilityTrack classTrack,
            String passiveName
    ) {
        public AbilityTrack getTrack(AbilityType type) {
            return switch (type) {
                case SUPER -> superTrack;
                case MELEE -> meleeTrack;
                case GRENADE -> grenadeTrack;
                case CLASS_ABILITY -> classTrack;
                default -> new AbilityTrack("", List.of());
            };
        }

        public Identifier getPassiveIcon() {
            return getHudTexture(passiveName);
        }
    }
}
