package io.github.stainlessstasis.destinymod.client.item_skin.models;

import com.geckolib.renderer.base.GeoRenderState;
import io.github.stainlessstasis.destinymod.DestinyMod;
import io.github.stainlessstasis.destinymod.client.entity_rendering.renderer.BonkHammerRenderer;
import io.github.stainlessstasis.destinymod.client.item_skin.ItemSkinModel;
import io.github.stainlessstasis.destinymod.item_skin.ItemSkin;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Brightness;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.phys.Vec3;

public class HammerSkinModel extends ItemSkinModel<ItemSkin> {
    public static float TRANSFORM = 0.275f;

    @Override
    public Identifier getModelResource(GeoRenderState renderState) {
        return DestinyMod.id("entity/hammer_of_sol");
    }

    @Override
    public Identifier getTextureResource(GeoRenderState renderState) {
        return BonkHammerRenderer.TEXTURE_LOCATION;
    }

    @Override
    public Identifier getAnimationResource(ItemSkin animatable) {
        return null;
    }

    @Override
    public int getBrightnessOverride() {
        return Brightness.FULL_BRIGHT.pack();
    }

    @Override
    public Vec3 rotation(ItemDisplayContext displayMode, AdditionalItemDisplayContext context) {
        if (context.isInHand()) {
            return new Vec3(0, -90, 0);
        }
        if (displayMode == ItemDisplayContext.FIXED) {
            return new Vec3(0, 180, 0);
        }
        if (displayMode == ItemDisplayContext.GUI) {
            return new Vec3(0, 0, -45);
        }
        return super.getDefaultRotation();
    }

    @Override
    public Vec3 scale(ItemDisplayContext displayMode, AdditionalItemDisplayContext context) {
        if (displayMode == ItemDisplayContext.GROUND) {
            return new Vec3(0.5, 0.5, 0.5);
        }
        if (displayMode == ItemDisplayContext.FIXED) {
            return super.getDefaultScale();
        }
        return super.getDefaultScale().scale(2f / 3f);
    }

    @Override
    public Vec3 translation(ItemDisplayContext displayMode, AdditionalItemDisplayContext context) {
        if (context.isInHand()) {
            return new Vec3(TRANSFORM*3, TRANSFORM, TRANSFORM);
        }
        if (displayMode == ItemDisplayContext.ON_SHELF) {
            return new Vec3(TRANSFORM, TRANSFORM, TRANSFORM);
        }
        if (displayMode == ItemDisplayContext.FIXED) {
            return new Vec3(TRANSFORM*3.5, 0, TRANSFORM*3.25);
        }
        if (displayMode == ItemDisplayContext.GROUND) {
            return new Vec3(TRANSFORM, TRANSFORM*2, TRANSFORM);
        }
        if (displayMode == ItemDisplayContext.GUI) {
            return new Vec3(0, TRANSFORM*1.5, 0);
        }
        return super.getDefaultTranslation();
    }
}
