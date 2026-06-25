package io.github.stainlessstasis.destinymod.client.item_skin;

import com.geckolib.model.GeoModel;
import com.geckolib.renderer.GeoItemRenderer;
import io.github.stainlessstasis.destinymod.registry.item.DestinyModItems;
import net.minecraft.resources.Identifier;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

public class WeaponSkinRegistry {
    private static final Map<Identifier, SkinEntry<?>> SKINS = new HashMap<>();

    public static void init() {
        register(HammerSkinItem.SKIN_ID, DestinyModItems.HAMMER_SKIN.get(), new HammerSkinModel());
    }

    private static <T extends WeaponSkinItem> void register(
            Identifier skinId, T item, GeoModel<T> model
    ) {
        SKINS.put(skinId, new SkinEntry<>(item, new GeoItemRenderer<>(model), model));
    }

    public static @Nullable <T extends WeaponSkinItem> SkinEntry<T> get(Identifier skinId) {
        return (SkinEntry<T>) SKINS.get(skinId);
    }

    public static boolean hasSkin(Identifier skinId) {
        return SKINS.containsKey(skinId);
    }

    public record SkinEntry<T extends WeaponSkinItem>(
            T item,
            GeoItemRenderer<T> renderer,
            GeoModel<T> model
    ) {}
}
