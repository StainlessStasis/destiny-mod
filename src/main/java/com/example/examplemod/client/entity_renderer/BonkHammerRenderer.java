package com.example.examplemod.client.entity_renderer;

import com.example.examplemod.DestinyMod;
import com.example.examplemod.entity.BonkHammerEntity;
import com.example.examplemod.entity.DestinyModEntities;
import com.geckolib.constant.DataTickets;
import com.geckolib.renderer.GeoEntityRenderer;
import com.geckolib.renderer.base.GeoRenderState;
import com.geckolib.renderer.base.RenderPassInfo;
import com.geckolib.renderer.layer.builtin.AutoGlowingGeoLayer;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.EntityType;

public class BonkHammerRenderer<S extends EntityRenderState & GeoRenderState> extends GeoEntityRenderer<BonkHammerEntity, S> {
    public static final Identifier TEXTURE_LOCATION = Identifier.fromNamespaceAndPath(DestinyMod.MODID, "textures/entity/hammer_of_sol.png");

    public BonkHammerRenderer(EntityRendererProvider.Context context, EntityType<BonkHammerEntity> entityType) {
        super(context, entityType);
        withRenderLayer(new AutoGlowingGeoLayer<>(this));
    }

    public BonkHammerRenderer(EntityRendererProvider.Context context) {
        this(context, DestinyModEntities.HAMMER_OF_SOL.get());
    }

    @Override
    public void scaleModelForRender(RenderPassInfo<S> renderPassInfo, float widthScale, float heightScale) {
        super.scaleModelForRender(renderPassInfo, widthScale/2, heightScale/2);
    }

    @Override
    protected void applyRotations(RenderPassInfo<S> renderPassInfo, PoseStack poseStack, float nativeScale) {
        var state = renderPassInfo.renderState();
        float xRot = state.getOrDefaultGeckolibData(DataTickets.ENTITY_PITCH, 0f);
        float yRot = state.getOrDefaultGeckolibData(DataTickets.ENTITY_YAW, 0f);
        poseStack.mulPose(Axis.YP.rotationDegrees(yRot + 90f));
        poseStack.mulPose(Axis.ZP.rotationDegrees(-xRot));
        float spin = state.ageInTicks * 30f;
        poseStack.mulPose(Axis.ZP.rotationDegrees(spin));
    }

    @Override
    public Identifier getTextureLocation(S state) {
        return TEXTURE_LOCATION;
    }
}
