package io.github.stainlessstasis.destinymod.client.item_skin;

import com.geckolib.model.GeoModel;
import io.github.stainlessstasis.destinymod.client.item_skin.models.AdditionalItemDisplayContext;
import io.github.stainlessstasis.destinymod.item_skin.ItemSkin;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.phys.Vec3;

public abstract class ItemSkinModel<T extends ItemSkin> extends GeoModel<T> {
    public static final float TRANSFORM = 0.1875f;

    public Vec3 getDefaultTranslation() {
        return new Vec3(0, 0, 0);
    }
    public Vec3 translation(ItemDisplayContext displayMode, AdditionalItemDisplayContext context) {
        if (context.isThirdPerson()) {
            return new Vec3(TRANSFORM*1.33, TRANSFORM, TRANSFORM*2);
        }
        if (displayMode == ItemDisplayContext.FIRST_PERSON_RIGHT_HAND) {
            return new Vec3(TRANSFORM*3.33, 0, 0);
        }
        return getDefaultTranslation();
    }

    public Vec3 getDefaultRotation() {
        return new Vec3(0, 0, 0);
    }
    public Vec3 rotation(ItemDisplayContext displayMode, AdditionalItemDisplayContext context) {
        if (displayMode == ItemDisplayContext.FIRST_PERSON_RIGHT_HAND) {
            return new Vec3(5, 5, 0);
        }
        if (displayMode == ItemDisplayContext.FIRST_PERSON_LEFT_HAND) {
            return new Vec3(5, -5, 0);
        }
        return getDefaultRotation();
    }

    public Vec3 getDefaultScale() {
        return new Vec3(1, 1, 1);
    }
    public Vec3 scale(ItemDisplayContext displayMode, AdditionalItemDisplayContext context) {
        return new Vec3(0.5, 0.5, 0.5);
    }

    public int getBrightnessOverride() {
        return -1;
    }
    public boolean hasBrightnessOverride() {
        return getBrightnessOverride() != -1;
    }
}
