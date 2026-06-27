package io.github.stainlessstasis.destinymod.item_skin;

import io.github.stainlessstasis.destinymod.item_skin.items.CrownSplitterSkinItem;
import io.github.stainlessstasis.destinymod.item_skin.items.HammerSkinItem;
import net.minecraft.resources.Identifier;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class ItemSkinIDs {
    private static final Set<Identifier> REGISTERED_IDS = new HashSet<>();

    public static void register(Identifier id) {
        REGISTERED_IDS.add(id);
    }

    public static boolean has(Identifier id) {
        return REGISTERED_IDS.contains(id);
    }

    public static Set<Identifier> getAll() {
        return Collections.unmodifiableSet(REGISTERED_IDS);
    }

    public static void init() {
        register(HammerSkinItem.SKIN_ID);
        register(CrownSplitterSkinItem.SKIN_ID);
        register(CrownSplitterSkinItem.BLOODY_SKIN_ID);
    }
}
