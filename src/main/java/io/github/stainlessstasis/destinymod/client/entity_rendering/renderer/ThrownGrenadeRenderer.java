package io.github.stainlessstasis.destinymod.client.entity_rendering.renderer;

import io.github.stainlessstasis.destinymod.DestinyMod;
import io.github.stainlessstasis.destinymod.entity.DestinyModEntities;
import com.geckolib.constant.DataTickets;
import com.geckolib.renderer.GeoEntityRenderer;
import com.geckolib.renderer.base.GeoRenderState;
import com.geckolib.renderer.base.RenderPassInfo;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import io.github.stainlessstasis.destinymod.entity.ThrownGrenadeEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.phys.Vec3;
import org.jspecify.annotations.NonNull;

public class ThrownGrenadeRenderer<S extends EntityRenderState & GeoRenderState> extends GeoEntityRenderer<ThrownGrenadeEntity, @NonNull S> {
    public static final Identifier TEXTURE_LOCATION = Identifier.fromNamespaceAndPath(DestinyMod.MODID, "textures/entity/hammer_of_sol.png");

    public ThrownGrenadeRenderer(EntityRendererProvider.Context context, EntityType<ThrownGrenadeEntity> entityType) {
        super(context, entityType);
    }

    public ThrownGrenadeRenderer(EntityRendererProvider.Context context) {
        this(context, DestinyModEntities.THROWN_GRENADE.get());
    }

    @Override
    public void scaleModelForRender(@NonNull RenderPassInfo<@NonNull S> renderPassInfo, float widthScale, float heightScale) {
        super.scaleModelForRender(renderPassInfo, widthScale*2, heightScale*2);
    }

    @Override
    public @NonNull Vec3 getRenderOffset(@NonNull S state) {
        return super.getRenderOffset(state).add(new Vec3(0, 0.1, 0));
    }

    @Override
    protected void applyRotations(RenderPassInfo<@NonNull S> renderPassInfo, PoseStack poseStack, float nativeScale) {
        var state = renderPassInfo.renderState();
        float xRot = state.getOrDefaultGeckolibData(DataTickets.ENTITY_PITCH, 0f);
        float yRot = state.getOrDefaultGeckolibData(DataTickets.ENTITY_YAW, 0f);

        poseStack.mulPose(Axis.YP.rotationDegrees(yRot + 90f));
        poseStack.mulPose(Axis.ZP.rotationDegrees(-xRot));
    }

    @Override
    public @NonNull Identifier getTextureLocation(@NonNull S state) {
        return TEXTURE_LOCATION;
    }
}
