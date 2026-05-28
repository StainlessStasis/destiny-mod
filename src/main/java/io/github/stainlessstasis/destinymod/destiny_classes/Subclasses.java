package io.github.stainlessstasis.destinymod.destiny_classes;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import io.github.stainlessstasis.destinymod.DestinyMod;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;

public class Subclasses {
    private static final BiMap<Identifier, Subclass> SUBCLASSES = HashBiMap.create();
    public static final Subclass SUNBREAKER = register(DestinyMod.id("sunbreaker"), new Subclass(Component.translatable("subclass.destinymod.sunbreaker"), DestinyClass.TITAN, DestinyElement.SOLAR));

    public static Subclass getByID(Identifier id) {
        return SUBCLASSES.get(id);
    }

    public static Identifier getID(Subclass subclass) {
        return SUBCLASSES.inverse().get(subclass);
    }

    public static Subclass register(Identifier id, Subclass subclass) {
        SUBCLASSES.put(id, subclass);
        return subclass;
    }
}
