package io.github.stainlessstasis.destinymod.registry.item;

import io.github.stainlessstasis.destinymod.DestinyMod;
import io.github.stainlessstasis.destinymod.client.item_skin.HammerSkinItem;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

public class DestinyModItems {
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(DestinyMod.MODID);
    public static final DeferredItem<HammerSkinItem> HAMMER_SKIN =
            ITEMS.registerItem("hammer_skin", HammerSkinItem::new, p -> p);

    public static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);
    }
}
