package io.github.stainlessstasis.destinymod.registry.item;

import io.github.stainlessstasis.destinymod.DestinyMod;
import io.github.stainlessstasis.destinymod.item_skin.ItemSkin;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

public class DestinyModItems {
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(DestinyMod.MODID);
    public static final DeferredItem<ItemSkin> HAMMER_SKIN = ITEMS.registerItem("hammer_of_sol_skin", ItemSkin::new, p -> p);
    public static final DeferredItem<ItemSkin> CROWN_SPLITTER_SKIN = ITEMS.registerItem("crown_splitter_skin", ItemSkin::new, p -> p);
    public static final DeferredItem<ItemSkin> CROWN_SPLITTER_BLOODY_SKIN = ITEMS.registerItem("crown_splitter_bloody_skin", ItemSkin::new, p -> p);

    public static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);
    }
}
