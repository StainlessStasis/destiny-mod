package io.github.stainlessstasis.destinymod.client.entity_rendering.layer;

import com.mojang.blaze3d.vertex.PoseStack;
import io.github.stainlessstasis.destinymod.DestinyMod;
import io.github.stainlessstasis.destinymod.client.entity_rendering.EntityRendererEvents;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.resources.Identifier;
import net.minecraft.util.ARGB;

public class ScorchRenderLayer<S extends LivingEntityRenderState, M extends EntityModel<? super S>> extends RenderLayer<S, M> {
    private static final Identifier TEXTURE = DestinyMod.id("textures/entity/hammer_of_sol.png");

    public ScorchRenderLayer(LivingEntityRenderer<?, S, M> renderer) {
        super(renderer);
    }

    @Override
    public void submit(PoseStack poseStack, SubmitNodeCollector collector, int light, S state, float yRot, float xRot) {
        EntityRendererEvents.DebuffData debuffData = state.getRenderData(EntityRendererEvents.DEBUFF_CONTEXT_KEY);
        if (debuffData == null || !debuffData.isScorched()) return;

        float ticks = state.ageInTicks;
        float uOffset = (ticks * 0.01f) % 1f;
        float vOffset = (ticks * 0.015f) % 1f;
        RenderType renderType = RenderTypes.energySwirl(TEXTURE, uOffset, vOffset);
        M model = this.getParentModel();
        int alpha = (int) (0.25f * 255);
        int tint = ARGB.color(alpha, alpha, alpha, alpha);

        collector.submitModel(
                model,
                state,
                poseStack,
                renderType,
                light,
                LivingEntityRenderer.getOverlayCoords(state, 0f),
                tint,
                null,
                -1,
                null
        );
    }
}
