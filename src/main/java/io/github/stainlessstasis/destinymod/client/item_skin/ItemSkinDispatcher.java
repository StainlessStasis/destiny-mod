package io.github.stainlessstasis.destinymod.client.item_skin;

import com.geckolib.model.GeoModel;
import com.geckolib.renderer.GeoItemRenderer;
import com.geckolib.renderer.base.GeoRenderState;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.world.entity.ItemOwner;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;

public class ItemSkinDispatcher {

    public static boolean shouldOverride(ItemStack stack) {
//        SkinComponent skin = stack.get(ModDataComponents.WEAPON_SKIN.get());
//        return skin != null && WeaponSkinRegistry.hasSkin(skin.skinId());
        return stack.getItem() == Items.NETHERITE_SWORD;
    }

    public static void render(
            ItemStack stack,
            ItemStackRenderState vanillaRenderState,
            ItemDisplayContext ctx,
            @Nullable ItemOwner owner,
            PoseStack poseStack,
            SubmitNodeCollector submitNodeCollector,
            int lightCoords
    ) {
//        SkinComponent skin = stack.get(ModDataComponents.WEAPON_SKIN.get());
//        if (skin == null) return;

//        var entry = WeaponSkinRegistry.get(skin.skinId());
        var entry = ItemSkinRegistry.get(HammerSkinItem.SKIN_ID);
        if (entry == null) return;

        renderSkin(entry, stack, vanillaRenderState, ctx, owner, poseStack, submitNodeCollector, lightCoords);
    }

    private static <T extends ItemSkin> void renderSkin(
            ItemSkinRegistry.SkinEntry<T> entry,
            ItemStack stack,
            ItemStackRenderState vanillaRenderState,
            ItemDisplayContext context,
            @Nullable ItemOwner owner,
            PoseStack poseStack,
            SubmitNodeCollector submitNodeCollector,
            int lightCoords
    ) {
        Minecraft mc = Minecraft.getInstance();
        GeoItemRenderer<T> renderer = entry.renderer();
        T skinItem = entry.item();
        GeoModel<T> model = entry.model();

        GeoItemRenderer.RenderData renderData = new GeoItemRenderer.RenderData(stack, vanillaRenderState, context, mc.level, owner);
        float partialTick = mc.getDeltaTracker().getGameTimeDeltaPartialTick(false);

        poseStack.pushPose();

        if (model instanceof ItemSkinTransform transform) {
            Vec3 translation = transform.translation(context);
            Vec3 rotation = transform.rotation(context);
            Vec3 scale = transform.scale(context);

            poseStack.translate(translation.x, translation.y, translation.z);
            poseStack.mulPose(Axis.XP.rotationDegrees((float) rotation.x));
            poseStack.mulPose(Axis.YP.rotationDegrees((float) rotation.y));
            poseStack.mulPose(Axis.ZP.rotationDegrees((float) rotation.z));
            poseStack.scale((float) scale.x, (float) scale.y, (float) scale.z);
        }

        GeoRenderState renderState = renderer.createRenderState(skinItem, renderData);
        renderer.captureDefaultRenderState(skinItem, renderData, renderState, partialTick);
        renderer.fireCompileRenderStateEvent(skinItem, renderData, renderState, partialTick);

        renderer.submit(renderState, poseStack, submitNodeCollector, 0);

        poseStack.popPose();
    }
}
