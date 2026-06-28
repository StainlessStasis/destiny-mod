package io.github.stainlessstasis.destinymod.client.item_skin;

import com.geckolib.model.GeoModel;
import com.geckolib.renderer.GeoItemRenderer;
import io.github.stainlessstasis.destinymod.client.item_skin.models.CrownSplitterBloodyModel;
import io.github.stainlessstasis.destinymod.client.item_skin.models.CrownSplitterModel;
import io.github.stainlessstasis.destinymod.client.item_skin.models.HammerSkinModel;
import io.github.stainlessstasis.destinymod.client.item_skin.models.HeartshadowModel;
import io.github.stainlessstasis.destinymod.item_skin.ItemSkin;
import io.github.stainlessstasis.destinymod.registry.item.DestinyModItems;
import net.minecraft.resources.Identifier;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class ClientItemSkins {
    private static final Map<Identifier, SkinEntry<?>> SKINS = new HashMap<>();

    public static void init() {
        register(ItemSkin.HAMMER_OF_SOL, DestinyModItems.HAMMER_SKIN.get(), new HammerSkinModel());
        register(ItemSkin.CROWN_SPLITTER, DestinyModItems.CROWN_SPLITTER_SKIN.get(), new CrownSplitterModel());
        register(ItemSkin.CROWN_SPLITTER_BLOODY, DestinyModItems.CROWN_SPLITTER_BLOODY_SKIN.get(), new CrownSplitterBloodyModel());
        register(ItemSkin.HEARTSHADOW, DestinyModItems.HEARTSHADOW_SKIN.get(), new HeartshadowModel());
    }

    private static <T extends ItemSkin> void register(
            Identifier skinID, T item, GeoModel<T> model
    ) {
        SKINS.put(skinID, new SkinEntry<>(item, new GeoItemRenderer<>(model), model));
    }

    public static @Nullable <T extends ItemSkin> SkinEntry<T> get(Identifier skinID) {
        return (SkinEntry<T>) SKINS.get(skinID);
    }

    public static boolean hasSkin(Identifier skinID) {
        return SKINS.containsKey(skinID);
    }

    public static Set<Identifier> getSkinIDs() {
        return Collections.unmodifiableSet(SKINS.keySet());
    }

    public record SkinEntry<T extends ItemSkin>(
            T item,
            GeoItemRenderer<T> renderer,
            GeoModel<T> model
    ) {}
}
