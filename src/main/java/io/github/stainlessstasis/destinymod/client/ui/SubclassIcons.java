package io.github.stainlessstasis.destinymod.client.ui;

import io.github.stainlessstasis.destinymod.DestinyMod;
import io.github.stainlessstasis.destinymod.destiny_classes.Subclass;
import io.github.stainlessstasis.destinymod.destiny_classes.Subclasses;
import io.github.stainlessstasis.destinymod.destiny_combat.ability.*;
import io.github.stainlessstasis.destinymod.destiny_combat.ability.registry.Abilities;
import io.github.stainlessstasis.destinymod.destiny_combat.ability.registry.Aspects;
import io.github.stainlessstasis.destinymod.destiny_combat.ability.registry.RegisteredAbility;
import io.github.stainlessstasis.destinymod.destiny_combat.ability.registry.RegisteredAspect;
import net.minecraft.resources.Identifier;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SubclassIcons {
    public static final Map<Subclass, SubclassIconSet> SUBCLASS_ICONS = new HashMap<>();

    static {
        SUBCLASS_ICONS.put(Subclasses.SUNBREAKER, new SubclassIconSet(
                new AbilityTrack("", List.of()),
                new AbilityTrack(getAbilityName(Abilities.THROWING_HAMMER), List.of(getAspectName(Aspects.MELTING_POINT), getAspectName(Aspects.HEATSEEKER), getAspectName(Aspects.ANVIL_DROP))),
                new AbilityTrack("", List.of()),
                new AbilityTrack("", List.of()),
                "sol_invictus"
        ));
    }

    private static String getAbilityName(RegisteredAbility ability) {
        return ability.resourceKey().identifier().getPath();
    }

    private static String getAspectName(RegisteredAspect aspect) {
        return aspect.resourceKey().identifier().getPath();
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
