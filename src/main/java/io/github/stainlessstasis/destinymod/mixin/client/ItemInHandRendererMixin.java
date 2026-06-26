package io.github.stainlessstasis.destinymod.mixin.client;

import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.blaze3d.vertex.PoseStack;
import io.github.stainlessstasis.destinymod.client.item_skin.ItemSkinDispatcher;
import net.minecraft.client.renderer.ItemInHandRenderer;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ItemInHandRenderer.class)
public abstract class ItemInHandRendererMixin {

    @Inject(
            method = "renderItem",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/renderer/item/ItemStackRenderState;submit(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/SubmitNodeCollector;III)V"
            ),
            cancellable = true
    )
    private void renderItem(
            LivingEntity mob,
            ItemStack itemStack,
            ItemDisplayContext type,
            PoseStack poseStack,
            SubmitNodeCollector submitNodeCollector,
            int lightCoords,
            CallbackInfo ci,
            @Local(name = "renderState") ItemStackRenderState renderState
    ) {
        if (!ItemSkinDispatcher.shouldOverride(itemStack)) return;

        ci.cancel();
        ItemSkinDispatcher.render(itemStack, renderState, type, mob, poseStack, submitNodeCollector, lightCoords);
    }


}
