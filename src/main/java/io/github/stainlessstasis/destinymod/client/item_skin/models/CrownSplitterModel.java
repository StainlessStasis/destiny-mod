package io.github.stainlessstasis.destinymod.client.item_skin.models;

import com.geckolib.renderer.base.GeoRenderState;
import io.github.stainlessstasis.destinymod.DestinyMod;
import io.github.stainlessstasis.destinymod.client.item_skin.ItemSkinModel;
import io.github.stainlessstasis.destinymod.item_skin.items.CrownSplitterSkinItem;
import io.github.stainlessstasis.destinymod.item_skin.items.HammerSkinItem;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.phys.Vec3;

public class CrownSplitterModel extends ItemSkinModel<CrownSplitterSkinItem> {
    public static final float TRANSFORM = 0.1875f;

    @Override
    public Identifier getModelResource(GeoRenderState renderState) {
        return DestinyMod.id("item/crown_splitter");
    }

    @Override
    public Identifier getTextureResource(GeoRenderState renderState) {
        return DestinyMod.id("textures/item/crown_splitter.png");
    }

    @Override
    public Identifier getAnimationResource(CrownSplitterSkinItem animatable) {
        return null;
    }

    @Override
    public Vec3 translation(ItemDisplayContext context) {
        if (context == ItemDisplayContext.THIRD_PERSON_LEFT_HAND || context == ItemDisplayContext.THIRD_PERSON_RIGHT_HAND) {
            return new Vec3(TRANSFORM*1.33, TRANSFORM, TRANSFORM*2);
        }
        if (context == ItemDisplayContext.FIRST_PERSON_RIGHT_HAND) {
            return new Vec3(TRANSFORM*3.33, 0, 0);
        }
        return super.translation(context);
    }

    @Override
    public Vec3 rotation(ItemDisplayContext context) {
        if (context == ItemDisplayContext.FIRST_PERSON_RIGHT_HAND) {
            return new Vec3(5, 5, 0);
        }
        if (context == ItemDisplayContext.FIRST_PERSON_LEFT_HAND) {
            return new Vec3(5, -5, 0);
        }
        return super.rotation(context);
    }

    @Override
    public Vec3 scale(ItemDisplayContext context) {
        return new Vec3(0.5, 0.5, 0.5);
    }
}
