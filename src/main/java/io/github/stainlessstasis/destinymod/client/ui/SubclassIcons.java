package io.github.stainlessstasis.destinymod.client.ui;

import io.github.stainlessstasis.destinymod.DestinyMod;
import io.github.stainlessstasis.destinymod.destiny_classes.Subclass;
import io.github.stainlessstasis.destinymod.destiny_classes.Subclasses;
import io.github.stainlessstasis.destinymod.destiny_combat.ability.*;
import io.github.stainlessstasis.destinymod.registry.Abilities;
import io.github.stainlessstasis.destinymod.registry.Aspects;
import io.github.stainlessstasis.destinymod.registry.RegisteredAbility;
import io.github.stainlessstasis.destinymod.registry.RegisteredAspect;
import net.minecraft.resources.Identifier;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SubclassIcons {
    public static final Map<Subclass, SubclassIconSet> SUBCLASS_ICONS = new HashMap<>();

    static {
        SUBCLASS_ICONS.put(Subclasses.SUNBREAKER, new SubclassIconSet(
                new AbilityTrack(Abilities.NONE, List.of()),
                new AbilityTrack(Abilities.THROWING_HAMMER, List.of(Aspects.MELTING_POINT, Aspects.HEATSEEKER, Aspects.ANVIL_DROP)),
                new AbilityTrack(Abilities.NONE, List.of()),
                new AbilityTrack(Abilities.NONE, List.of()),
                Abilities.NONE
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
        return DestinyMod.id("textures/gui/sprites/destiny_ui/" + name + ".png");
    }

    public record AbilityTrack(RegisteredAbility mainAbility, List<RegisteredAspect> aspects) {
        public Identifier getMainIcon(boolean charged) {
            if (mainAbility == null || mainAbility == Abilities.NONE) return null;
            String suffix = charged ? "_charged" : "";
            return getHudTexture(getAbilityName(mainAbility) + suffix);
        }

        public Identifier getAspectIcon(int aspectIndex) {
            if (aspectIndex < 0 || aspectIndex >= aspects.size()) return null;
            return getHudTexture(getAspectName(aspects.get(aspectIndex)));
        }

        public int totalAspects() {
            return aspects.size();
        }
    }

    public record SubclassIconSet(
            AbilityTrack superTrack,
            AbilityTrack meleeTrack,
            AbilityTrack grenadeTrack,
            AbilityTrack classTrack,
            RegisteredAbility passiveAbility
    ) {
        public AbilityTrack getTrack(AbilityType type) {
            return switch (type) {
                case SUPER -> superTrack;
                case MELEE -> meleeTrack;
                case GRENADE -> grenadeTrack;
                case CLASS_ABILITY -> classTrack;
                default -> new AbilityTrack(Abilities.NONE, List.of());
            };
        }

        public Identifier getPassiveIcon() {
            return getHudTexture(getAbilityName(passiveAbility));
        }
    }
}
