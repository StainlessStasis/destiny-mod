package io.github.stainlessstasis.destinymod.client.item_skin.models;

import com.geckolib.renderer.base.GeoRenderState;
import io.github.stainlessstasis.destinymod.DestinyMod;
import io.github.stainlessstasis.destinymod.client.item_skin.ItemSkinModel;
import io.github.stainlessstasis.destinymod.item_skin.items.CrownSplitterSkinItem;
import io.github.stainlessstasis.destinymod.item_skin.items.HammerSkinItem;
import net.minecraft.resources.Identifier;

public class CrownSplitterModel extends ItemSkinModel<CrownSplitterSkinItem> {
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
}
