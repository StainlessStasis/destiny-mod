package io.github.stainlessstasis.destinymod.client.item_skin;

import com.geckolib.renderer.base.GeoRenderState;
import io.github.stainlessstasis.destinymod.DestinyMod;
import io.github.stainlessstasis.destinymod.client.entity_rendering.renderer.BonkHammerRenderer;
import io.github.stainlessstasis.destinymod.item_skin.HammerSkinItem;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.phys.Vec3;

public class HammerSkinModel extends ItemSkinModel<HammerSkinItem> {
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
    public Identifier getAnimationResource(HammerSkinItem animatable) {
        return null;
    }

    @Override
    public Vec3 rotation(ItemDisplayContext context) {
        if (isInHand(context)) {
            return new Vec3(0, -90, 0);
        }
        return super.rotation(context);
    }

    @Override
    public Vec3 scale(ItemDisplayContext context) {
        if (context == ItemDisplayContext.GROUND || context == ItemDisplayContext.GUI) {
            return new Vec3(0.5, 0.5, 0.5);
        }
        return new Vec3(1, 1, 1).scale(2f / 3f);
    }

    @Override
    public Vec3 translation(ItemDisplayContext context) {
        if (isInHand(context)) {
            return new Vec3(TRANSFORM*3, TRANSFORM, TRANSFORM);
        }
        if (context == ItemDisplayContext.ON_SHELF) {
            return new Vec3(TRANSFORM, TRANSFORM, TRANSFORM);
        }
        if (context == ItemDisplayContext.FIXED) {
            return new Vec3(TRANSFORM/2, TRANSFORM/2, TRANSFORM/2);
        }
        if (context == ItemDisplayContext.GROUND) {
            return new Vec3(TRANSFORM, TRANSFORM*2, TRANSFORM);
        }
        if (context == ItemDisplayContext.GUI) {
            return new Vec3(TRANSFORM, TRANSFORM, TRANSFORM);
        }
        return super.translation(context);
    }

    private boolean isInHand(ItemDisplayContext context) {
        return context.firstPerson() || context == ItemDisplayContext.THIRD_PERSON_LEFT_HAND ||  context == ItemDisplayContext.THIRD_PERSON_RIGHT_HAND;
    }
}
