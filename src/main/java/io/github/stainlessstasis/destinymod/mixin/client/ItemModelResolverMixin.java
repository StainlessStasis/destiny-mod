package io.github.stainlessstasis.destinymod.mixin.client;

import io.github.stainlessstasis.destinymod.client.item_skin.ItemSkinRenderArgument;
import io.github.stainlessstasis.destinymod.client.item_skin.ItemSkinRenderer;
import io.github.stainlessstasis.destinymod.data.DestinyModDataComponents;
import io.github.stainlessstasis.destinymod.item_skin.ItemSkinComponent;
import io.github.stainlessstasis.destinymod.item_skin.ItemSkinIDs;
import net.minecraft.client.renderer.item.ItemModelResolver;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.world.entity.ItemOwner;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ItemModelResolver.class)
public abstract class ItemModelResolverMixin {
    @Unique
    private static final ItemSkinRenderer SKIN_RENDERER = new ItemSkinRenderer();

    @Inject(
            method = "updateForTopItem",
            at = @At("RETURN")
    )
    private void injectSkinLayer(
            ItemStackRenderState output, ItemStack item, ItemDisplayContext displayContext, Level level, ItemOwner owner, int seed, CallbackInfo ci
    ) {
        if (item.isEmpty()) return;
        ItemSkinComponent skin = item.get(DestinyModDataComponents.ITEM_SKIN.get());
        if (skin == null || !ItemSkinIDs.has(skin.skinID())) return;

        output.clear();
        output.displayContext = displayContext;
        output.setOversizedInGui(true);
        output.setAnimated(); // NEEDED FOR GUI TO WORK

        ItemStackRenderState.LayerRenderState layer = output.newLayer();
        layer.setupSpecialModel(SKIN_RENDERER, new ItemSkinRenderArgument(skin, displayContext));
    }
}
