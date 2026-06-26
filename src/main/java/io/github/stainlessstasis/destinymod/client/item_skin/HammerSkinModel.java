package io.github.stainlessstasis.destinymod.client.item_skin;

import com.geckolib.renderer.base.GeoRenderState;
import io.github.stainlessstasis.destinymod.DestinyMod;
import io.github.stainlessstasis.destinymod.client.entity_rendering.renderer.BonkHammerRenderer;
import io.github.stainlessstasis.destinymod.item_skin.HammerSkinItem;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.phys.Vec3;

public class HammerSkinModel extends ItemSkinModel<HammerSkinItem> {
    @Override
    public Identifier getModelResource(GeoRenderState renderState) {
        return DestinyMod.id("entity/hammer_of_sol");
    }

    @Override
    public Identifier getTextureResource(GeoRenderState renderState) {
        return BonkHammerRenderer.TEXTURE_LOCATION;
    }

    @Override
    public Identifier getAnimationResource(HammerSkinItem animatable) {
        return null;
    }

    @Override
    public Vec3 rotation(ItemDisplayContext context) {
        return new Vec3(0, -90, 0);
    }

    @Override
    public Vec3 scale(ItemDisplayContext context) {
        if (context.firstPerson()) {
            return new Vec3(0.5, 0.5, 0.5);
        }
        return new Vec3(1, 1, 1).scale(2f/3f);
    }

    @Override
    public Vec3 translation(ItemDisplayContext context) {
//        if (!context.firstPerson()) {
//            return new Vec3(0.333, -0.1, -0.2);
//        }
//        if (context == ItemDisplayContext.FIRST_PERSON_LEFT_HAND) {
//            return new Vec3(0.5, 0, 0);
//        }
        return super.translation(context);
    }
}
