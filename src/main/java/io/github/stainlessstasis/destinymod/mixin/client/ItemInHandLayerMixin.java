package io.github.stainlessstasis.destinymod.mixin.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import io.github.stainlessstasis.destinymod.client.item_skin.ItemSkinDispatcher;
import net.minecraft.client.model.ArmedModel;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.effects.SpearAnimations;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.ItemInHandLayer;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.entity.state.ArmedEntityRenderState;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SwingAnimationType;
import org.jspecify.annotations.NonNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ItemInHandLayer.class)
public abstract class ItemInHandLayerMixin<S extends ArmedEntityRenderState, M extends EntityModel<S> & ArmedModel<S>> extends RenderLayer<S, M> {
    @Shadow
    protected abstract boolean useBabyOffset(S state);

    private ItemInHandLayerMixin(RenderLayerParent<S, @NonNull M> renderer) {
        super(renderer);
    }

    @Inject(
            method = "submitArmWithItem",
            at = @At("HEAD"),
            cancellable = true
    )
    private void submitArmWithItem(
            S state,
            ItemStackRenderState item,
            ItemStack itemStack,
            HumanoidArm arm,
            PoseStack poseStack,
            SubmitNodeCollector submitNodeCollector,
            int lightCoords,
            CallbackInfo ci
    ) {
        if (item.isEmpty()) return;
        if (!ItemSkinDispatcher.shouldOverride(itemStack)) return;

        ci.cancel();
        poseStack.pushPose();

        this.getParentModel().translateToHand(state, arm, poseStack);
        poseStack.mulPose(Axis.XP.rotationDegrees(-90.0F));
        poseStack.mulPose(Axis.YP.rotationDegrees(180.0F));

        boolean isLeftHand = arm == HumanoidArm.LEFT;
        float offsetX = this.useBabyOffset(state) ? 0.0F : 1.0F;
        float offsetY = this.useBabyOffset(state) ? 1.0F : 2.0F;
        float offsetZ = this.useBabyOffset(state) ? -4.5F : -10.0F;
        poseStack.translate((isLeftHand ? -1 : 1) * offsetX / 16.0F, offsetY / 16.0F, offsetZ / 16.0F);

        if (state.attackTime > 0.0F && state.attackArm == arm && state.swingAnimationType == SwingAnimationType.STAB) {
            SpearAnimations.thirdPersonAttackItem(state, poseStack);
        }

        float ticksUsingItem = state.ticksUsingItem(arm);
        if (ticksUsingItem != 0.0F) {
            (arm == HumanoidArm.RIGHT ? state.rightArmPose : state.leftArmPose)
                    .animateUseItem(state, poseStack, ticksUsingItem, arm, itemStack);
        }

        ItemSkinDispatcher.render(
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

        poseStack.popPose();
    }
}
