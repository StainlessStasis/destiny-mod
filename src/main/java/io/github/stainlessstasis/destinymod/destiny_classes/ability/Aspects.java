package io.github.stainlessstasis.destinymod.destiny_classes.ability;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import io.github.stainlessstasis.destinymod.DestinyMod;
import net.minecraft.resources.Identifier;

public class Aspects {
    private static final BiMap<Identifier, Aspect> ASPECTS = HashBiMap.create();
    public static final Aspect NONE = register(DestinyMod.id("none"), new Aspect(0));
    public static final Aspect MELTING_POINT = register(DestinyMod.id("melting_point"), new Aspect(0));

    public static Aspect getByID(Identifier id) {
        return ASPECTS.get(id);
    }

    public static Identifier getID(Aspect aspect) {
        return ASPECTS.inverse().get(aspect);
    }

    public static Aspect register(Identifier id, Aspect aspect) {
        ASPECTS.put(id, aspect);
        return aspect;
    }
}
