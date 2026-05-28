package io.github.stainlessstasis.destinymod.destiny_classes.ability;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import io.github.stainlessstasis.destinymod.DestinyMod;
import net.minecraft.resources.Identifier;

public class Abilities {
    private static final BiMap<Identifier, Ability> ABILITIES = HashBiMap.create();
    public static final Ability NONE = register(DestinyMod.id("none"), new Ability(AbilityType.MELEE, 0, 0));
    public static final Ability THROWING_HAMMER = register(DestinyMod.id("throwing_hammer"), new Ability(AbilityType.MELEE, 200, 3));

    public static Ability getByID(Identifier id) {
        return ABILITIES.get(id);
    }

    public static Identifier getID(Ability ability) {
        return ABILITIES.inverse().get(ability);
    }

    public static Ability register(Identifier id, Ability ability) {
        ABILITIES.put(id, ability);
        return ability;
    }
}
