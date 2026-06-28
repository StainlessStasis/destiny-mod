package io.github.stainlessstasis.destinymod.client.item_skin;

import com.geckolib.model.GeoModel;
import com.geckolib.renderer.GeoItemRenderer;
import com.geckolib.renderer.base.GeoRenderState;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import io.github.stainlessstasis.destinymod.client.item_skin.models.AdditionalItemDisplayContext;
import io.github.stainlessstasis.destinymod.item_skin.ItemSkin;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;

public class ItemSkinDispatcher {
    public static <T extends ItemSkin> void renderFromSpecial(
            ItemSkinRenderArgument arg,
            ClientItemSkins.SkinEntry<T> entry,
            PoseStack poseStack,
            SubmitNodeCollector submitNodeCollector,
            int lightCoords
    ) {
        Minecraft mc = Minecraft.getInstance();
        GeoItemRenderer<T> renderer = entry.renderer();
        T skinItem = entry.item();
        ItemDisplayContext displayMode = arg.displayMode();
        AdditionalItemDisplayContext context = AdditionalItemDisplayContext.create(displayMode);

        GeoItemRenderer.RenderData renderData = new GeoItemRenderer.RenderData(
                ItemStack.EMPTY, new ItemStackRenderState(), displayMode, mc.level, null
        );

        poseStack.pushPose();

        GeoModel<T> model = entry.model();
        if (model instanceof ItemSkinTransform transform) {
            Vec3 t = transform.translation(displayMode, context);
            Vec3 r = transform.rotation(displayMode, context);
            Vec3 s = transform.scale(displayMode, context);
            poseStack.translate(t.x, t.y, t.z);
            poseStack.mulPose(Axis.XP.rotationDegrees((float) r.x));
            poseStack.mulPose(Axis.YP.rotationDegrees((float) r.y));
            poseStack.mulPose(Axis.ZP.rotationDegrees((float) r.z));
            poseStack.scale((float) s.x, (float) s.y, (float) s.z);
        }

        float partialTick = mc.getDeltaTracker().getGameTimeDeltaPartialTick(false);
        GeoRenderState renderState = renderer.createRenderState(skinItem, renderData);
        renderer.captureDefaultRenderState(skinItem, renderData, renderState, partialTick);
        renderer.fireCompileRenderStateEvent(skinItem, renderData, renderState, partialTick);
        renderer.submit(renderState, poseStack, submitNodeCollector, 0);

        poseStack.popPose();
    }
}
