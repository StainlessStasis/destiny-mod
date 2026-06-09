package io.github.stainlessstasis.destinymod.client.entity_rendering.layer;

import com.mojang.blaze3d.vertex.PoseStack;
import io.github.stainlessstasis.destinymod.DMColor;
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
import net.minecraft.util.LightCoordsUtil;
import net.minecraft.util.Mth;
import org.jspecify.annotations.NonNull;

public class MeltingPointRenderLayer<S extends LivingEntityRenderState, M extends EntityModel<? super S>> extends RenderLayer<S, M> {
    private static final Identifier TEXTURE = DestinyMod.id("textures/entity/hammer_of_sol.png");

    public MeltingPointRenderLayer(LivingEntityRenderer<?, S, M> renderer) {
        super(renderer);
    }

    @Override
    public void submit(@NonNull PoseStack poseStack, @NonNull SubmitNodeCollector collector, int light, S state, float yRot, float xRot) {
        EntityRendererEvents.DebuffData debuffData = state.getRenderData(EntityRendererEvents.DEBUFF_CONTEXT_KEY);
        if (debuffData == null || !debuffData.isMelting()) return;

        float pulse = Mth.sin(state.ageInTicks * 0.15f) * 0.03f + 1.04f;
        poseStack.scale(pulse, pulse, pulse);

        RenderType renderType = RenderTypes.energySwirl(TEXTURE, 0.0f, 0.0f);
        M model = this.getParentModel();

        int tint = DMColor.SOLAR_DARK.withOpacity(0.1f);

        collector.submitModel(
                model,
                state,
                poseStack,
                renderType,
                LightCoordsUtil.FULL_BRIGHT,
                LivingEntityRenderer.getOverlayCoords(state, 0f),
                tint,
                null,
                -1,
                null
        );
    }
}
