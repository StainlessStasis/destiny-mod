package io.github.stainlessstasis.destinymod.client.item_skin.models;

import com.geckolib.renderer.base.GeoRenderState;
import io.github.stainlessstasis.destinymod.DestinyMod;
import io.github.stainlessstasis.destinymod.client.item_skin.ItemSkinModel;
import io.github.stainlessstasis.destinymod.item_skin.ItemSkin;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.phys.Vec3;

public class HeartshadowModel extends ItemSkinModel<ItemSkin> {
    @Override
    public Identifier getModelResource(GeoRenderState renderState) {
        return DestinyMod.id("item/heartshadow");
    }

    @Override
    public Identifier getTextureResource(GeoRenderState renderState) {
        return DestinyMod.id("textures/item/heartshadow.png");
    }

    @Override
    public Identifier getAnimationResource(ItemSkin animatable) {
        return null;
    }

    @Override
    public Vec3 translation(ItemDisplayContext displayMode, AdditionalItemDisplayContext context) {
        return super.translation(displayMode, context);
    }
}
