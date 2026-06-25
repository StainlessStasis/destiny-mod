package io.github.stainlessstasis.destinymod.mixin.client;

import com.mojang.blaze3d.vertex.PoseStack;
import io.github.stainlessstasis.destinymod.client.item_skin.WeaponSkinDispatcher;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.layers.ItemInHandLayer;
import net.minecraft.client.renderer.entity.state.ArmedEntityRenderState;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ItemInHandLayer.class)
public abstract class ItemInHandLayerMixin {

    @Inject(
            method = "submitArmWithItem",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/renderer/item/ItemStackRenderState;submit(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/SubmitNodeCollector;III)V"
            ),
            cancellable = true
    )
    private <S extends ArmedEntityRenderState> void submitArmWithItem(
            S state, ItemStackRenderState item, ItemStack itemStack, HumanoidArm arm, PoseStack poseStack, SubmitNodeCollector submitNodeCollector, int lightCoords, CallbackInfo ci
    ) {
        if (!WeaponSkinDispatcher.shouldOverride(itemStack)) return;

        ci.cancel();
        poseStack.popPose();

        WeaponSkinDispatcher.render(
                itemStack,
                item,
                arm == HumanoidArm.RIGHT
                        ? ItemDisplayContext.THIRD_PERSON_RIGHT_HAND
                        : ItemDisplayContext.THIRD_PERSON_LEFT_HAND,
                null,
                poseStack,
                submitNodeCollector,
                lightCoords
        );
    }
}
