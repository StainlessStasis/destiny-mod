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
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;

public class WeaponSkinDispatcher {

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
        var entry = WeaponSkinRegistry.get(HammerSkinItem.SKIN_ID);
        if (entry == null) return;

        renderSkin(entry, stack, vanillaRenderState, ctx, owner, poseStack, submitNodeCollector, lightCoords);
    }

    private static <T extends WeaponSkinItem> void renderSkin(
            WeaponSkinRegistry.SkinEntry<T> entry,
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

        if (model instanceof WeaponSkinTransform transform) {
            Vector3f translation = transform.translation();
            Vector3f rotation = transform.rotation();
            Vector3f scale = transform.scale();

            poseStack.translate(translation.x, translation.y, translation.z);
            poseStack.mulPose(Axis.XP.rotationDegrees(rotation.x));
            poseStack.mulPose(Axis.YP.rotationDegrees(rotation.y));
            poseStack.mulPose(Axis.ZP.rotationDegrees(rotation.z));
            poseStack.scale(scale.x, scale.y, scale.z);
        }

        GeoRenderState renderState = renderer.createRenderState(skinItem, renderData);
        renderer.captureDefaultRenderState(skinItem, renderData, renderState, partialTick);
        renderer.fireCompileRenderStateEvent(skinItem, renderData, renderState, partialTick);

        renderer.submit(renderState, poseStack, submitNodeCollector, 0);

        poseStack.popPose();
    }
}
