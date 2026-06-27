package io.github.stainlessstasis.destinymod.client.item_skin.models;

import com.geckolib.renderer.base.GeoRenderState;
import io.github.stainlessstasis.destinymod.DestinyMod;
import net.minecraft.resources.Identifier;

public class CrownSplitterBloodyModel extends CrownSplitterModel {
    @Override
    public Identifier getTextureResource(GeoRenderState renderState) {
        return DestinyMod.id("textures/item/crown_splitter_bloody.png");
    }
}
