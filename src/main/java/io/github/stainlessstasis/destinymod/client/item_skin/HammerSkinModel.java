package io.github.stainlessstasis.destinymod.client.item_skin;

import com.geckolib.model.GeoModel;
import com.geckolib.renderer.base.GeoRenderState;
import io.github.stainlessstasis.destinymod.DestinyMod;
import io.github.stainlessstasis.destinymod.client.entity_rendering.renderer.BonkHammerRenderer;
import net.minecraft.resources.Identifier;
import org.joml.Vector3f;

public class HammerSkinModel extends WeaponSkinModel<HammerSkinItem> {
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
    public Vector3f rotation() {
        return new Vector3f(0, -90, 0);
    }

    @Override
    public Vector3f scale() {
        return new Vector3f(0.5f);
    }
}
