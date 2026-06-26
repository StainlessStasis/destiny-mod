package io.github.stainlessstasis.destinymod.data;

import io.github.stainlessstasis.destinymod.DestinyMod;
import io.github.stainlessstasis.destinymod.item_skin.ItemSkinComponent;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class DestinyModDataComponents {
    public static final DeferredRegister.DataComponents REGISTRAR = DeferredRegister.createDataComponents(Registries.DATA_COMPONENT_TYPE, DestinyMod.MODID);

    public static final Supplier<DataComponentType<ItemSkinComponent>> ITEM_SKIN = REGISTRAR.registerComponentType(
            "item_skin",
            builder -> builder
                    .persistent(ItemSkinComponent.CODEC)
                    .networkSynchronized(ItemSkinComponent.STREAM_CODEC)
    );

    public static void register(IEventBus eventBus) {
        REGISTRAR.register(eventBus);
    }
}
