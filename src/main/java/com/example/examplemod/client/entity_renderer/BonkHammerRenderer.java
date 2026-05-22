package com.example.examplemod.client.entity_renderer;

import com.example.examplemod.DestinyMod;
import com.example.examplemod.entity.BonkHammerEntity;
import com.example.examplemod.entity.DestinyModEntities;
import com.geckolib.constant.DataTickets;
import com.geckolib.constant.dataticket.DataTicket;
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
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.ApiStatus;

public class BonkHammerRenderer<S extends EntityRenderState & GeoRenderState> extends GeoEntityRenderer<BonkHammerEntity, S> {
    public static final Identifier TEXTURE_LOCATION = Identifier.fromNamespaceAndPath(DestinyMod.MODID, "textures/entity/hammer_of_sol.png");
    public static final DataTicket<Boolean> IS_GROUNDED = DataTicket.create("is_grounded", Boolean.class);
    public static final DataTicket<Boolean> IS_IN_LIQUID = DataTicket.create("is_in_liquid", Boolean.class);
    public static final DataTicket<Float> SPIN_DEGREES = DataTicket.create("spin_degrees", Float.class);

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
    public Vec3 getRenderOffset(S state) {
        return super.getRenderOffset(state).add(new Vec3(0, 0.2, 0));
    }

    @Override
    protected void applyRotations(RenderPassInfo<S> renderPassInfo, PoseStack poseStack, float nativeScale) {
        var state = renderPassInfo.renderState();
        float xRot = state.getOrDefaultGeckolibData(DataTickets.ENTITY_PITCH, 0f);
        float yRot = state.getOrDefaultGeckolibData(DataTickets.ENTITY_YAW, 0f);
        boolean isGrounded = state.getOrDefaultGeckolibData(IS_GROUNDED, false);
        boolean isInLiquid = state.getOrDefaultGeckolibData(IS_IN_LIQUID, false);

        poseStack.mulPose(Axis.YP.rotationDegrees(yRot + 90f));
        poseStack.mulPose(Axis.ZP.rotationDegrees(-xRot));
        if (!isGrounded) {
            float spinSpeed = isInLiquid ? 10f : 30f;
            float rotation = state.ageInTicks * spinSpeed;
            poseStack.mulPose(Axis.ZP.rotationDegrees(rotation));
        }
    }

    @ApiStatus.OverrideOnly
    @Override
    public void addRenderData(BonkHammerEntity entity, Void relatedObject, S renderState, float partialTick) {
        renderState.addGeckolibData(IS_GROUNDED, entity.isGrounded());
        renderState.addGeckolibData(IS_IN_LIQUID, entity.isInLiquid());
    }

    @Override
    public Identifier getTextureLocation(S state) {
        return TEXTURE_LOCATION;
    }
}
