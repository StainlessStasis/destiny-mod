package io.github.stainlessstasis.destinymod.api.block_display_fx.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Transformation;
import io.github.stainlessstasis.destinymod.api.block_display_fx.VfxEntity;
import io.github.stainlessstasis.destinymod.api.block_display_fx.animation.VfxAnimation;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.block.BlockModelResolver;
import net.minecraft.client.renderer.entity.DisplayRenderer;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import org.joml.Matrix4f;
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
        anim.overlayColorChannel().evaluate(t, state.overlayColor);
        anim.overlayIntensityChannel().evaluate(t, state.overlayIntensity);
        state.brightnessOverride = entity.getBrightnessOverride();

        blockModelResolver.update(state.blockModel, entity.getBlockState(), DisplayRenderer.BLOCK_DISPLAY_CONTEXT);
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

        applyOverlayColor(state, poseStack, collector);

        poseStack.popPose();
    }

    private void applyOverlayColor(VfxEntityRenderState state, PoseStack poseStack, SubmitNodeCollector collector) {
        if (state.overlayIntensity[0] <= 0f) return;
        collector.submitCustomGeometry(poseStack, RenderTypes.debugFilledBox(), (pose, buffer) -> {
            int r = (int)(state.overlayColor.x * 255);
            int g = (int)(state.overlayColor.y * 255);
            int b = (int)(state.overlayColor.z * 255);
            int a = (int)(state.overlayIntensity[0] * 255);
            Matrix4f mat = pose.pose();

            // Bottom
            buffer.addVertex(mat, 0, 0, 0).setColor(r, g, b, a);
            buffer.addVertex(mat, 1, 0, 0).setColor(r, g, b, a);
            buffer.addVertex(mat, 1, 0, 1).setColor(r, g, b, a);
            buffer.addVertex(mat, 0, 0, 1).setColor(r, g, b, a);
            // Top
            buffer.addVertex(mat, 0, 1, 1).setColor(r, g, b, a);
            buffer.addVertex(mat, 1, 1, 1).setColor(r, g, b, a);
            buffer.addVertex(mat, 1, 1, 0).setColor(r, g, b, a);
            buffer.addVertex(mat, 0, 1, 0).setColor(r, g, b, a);
            // North
            buffer.addVertex(mat, 1, 0, 0).setColor(r, g, b, a);
            buffer.addVertex(mat, 0, 0, 0).setColor(r, g, b, a);
            buffer.addVertex(mat, 0, 1, 0).setColor(r, g, b, a);
            buffer.addVertex(mat, 1, 1, 0).setColor(r, g, b, a);
            // South
            buffer.addVertex(mat, 0, 0, 1).setColor(r, g, b, a);
            buffer.addVertex(mat, 1, 0, 1).setColor(r, g, b, a);
            buffer.addVertex(mat, 1, 1, 1).setColor(r, g, b, a);
            buffer.addVertex(mat, 0, 1, 1).setColor(r, g, b, a);
            // West
            buffer.addVertex(mat, 0, 0, 0).setColor(r, g, b, a);
            buffer.addVertex(mat, 0, 0, 1).setColor(r, g, b, a);
            buffer.addVertex(mat, 0, 1, 1).setColor(r, g, b, a);
            buffer.addVertex(mat, 0, 1, 0).setColor(r, g, b, a);
            // East
            buffer.addVertex(mat, 1, 0, 1).setColor(r, g, b, a);
            buffer.addVertex(mat, 1, 0, 0).setColor(r, g, b, a);
            buffer.addVertex(mat, 1, 1, 0).setColor(r, g, b, a);
            buffer.addVertex(mat, 1, 1, 1).setColor(r, g, b, a);
        });
    }

    @Override
    protected boolean shouldShowName(@NonNull VfxEntity entity, double distanceToCameraSq) {
        return false;
    }

    @Override
    protected float getShadowRadius(@NonNull VfxEntityRenderState state) {
        return 0f;
    }

    @Override
    protected float getShadowStrength(@NonNull VfxEntityRenderState state) {
        return 0f;
    }
}