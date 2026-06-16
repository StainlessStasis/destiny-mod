package io.github.stainlessstasis.destinymod.api.block_display_fx.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Transformation;
import io.github.stainlessstasis.destinymod.api.block_display_fx.VfxEntity;
import io.github.stainlessstasis.destinymod.api.block_display_fx.channel.VfxAnimation;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.block.BlockModelResolver;
import net.minecraft.client.renderer.entity.DisplayRenderer;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import org.jspecify.annotations.NonNull;

public class VfxEntityRenderer extends EntityRenderer<VfxEntity, VfxEntityRenderState> {
    protected final BlockModelResolver blockModelResolver;

    public VfxEntityRenderer(EntityRendererProvider.Context context) {
        super(context);
        this.blockModelResolver = context.getBlockModelResolver();
    }

    @Override
    public @NonNull VfxEntityRenderState createRenderState() {
        return new VfxEntityRenderState();
    }

    @Override
    public void extractRenderState(@NonNull VfxEntity entity, @NonNull VfxEntityRenderState state, float partialTicks) {
        super.extractRenderState(entity, state, partialTicks);

        VfxAnimation anim = entity.getCurrentAnimation();
        if (anim == null) return;

        float t = entity.getAnimationProgress(partialTicks);
        anim.translationChannel().evaluate(t, state.translation);
        anim.scaleChannel().evaluate(t, state.scale);
        anim.rotationChannel().evaluate(t, state.rotation);
        state.brightnessOverride = entity.getBrightnessOverride();

        blockModelResolver.update(
                state.blockModel,
                entity.getBlockState(),
                DisplayRenderer.BLOCK_DISPLAY_CONTEXT
        );
    }

    @Override
    public void submit(@NonNull VfxEntityRenderState state, @NonNull PoseStack poseStack, @NonNull SubmitNodeCollector collector, @NonNull CameraRenderState camera) {
        super.submit(state, poseStack, collector, camera);
        poseStack.pushPose();

        Transformation transformation = new Transformation(
                state.translation,
                state.rotation,
                state.scale,
                null
        );
        poseStack.mulPose(transformation);

        int light = state.brightnessOverride != -1 ? state.brightnessOverride : state.lightCoords;
        state.blockModel.submit(poseStack, collector, light, OverlayTexture.NO_OVERLAY, state.outlineColor);

        poseStack.popPose();
    }
}