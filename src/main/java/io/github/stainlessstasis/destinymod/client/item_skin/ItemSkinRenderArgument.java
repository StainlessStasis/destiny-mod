package io.github.stainlessstasis.destinymod.client.item_skin;

import io.github.stainlessstasis.destinymod.item_skin.ItemSkinComponent;
import net.minecraft.world.item.ItemDisplayContext;

public record ItemSkinRenderArgument(ItemSkinComponent skin, ItemDisplayContext displayMode) {}
