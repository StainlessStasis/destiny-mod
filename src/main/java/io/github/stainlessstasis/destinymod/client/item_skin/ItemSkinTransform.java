package io.github.stainlessstasis.destinymod.client.item_skin;

import io.github.stainlessstasis.destinymod.client.item_skin.models.AdditionalItemDisplayContext;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.phys.Vec3;

public interface ItemSkinTransform {
    public static final float TRANSFORM = 0.1875f;

    default Vec3 getDefaultTranslation() {
        return new Vec3(0, 0, 0);
    }
    default Vec3 translation(ItemDisplayContext displayMode, AdditionalItemDisplayContext context) {
        if (context.isThirdPerson()) {
            return new Vec3(TRANSFORM*1.33, TRANSFORM, TRANSFORM*2);
        }
        if (displayMode == ItemDisplayContext.FIRST_PERSON_RIGHT_HAND) {
            return new Vec3(TRANSFORM*3.33, 0, 0);
        }
        return getDefaultTranslation();
    }

    default Vec3 getDefaultRotation() {
        return new Vec3(0, 0, 0);
    }
    default Vec3 rotation(ItemDisplayContext displayMode, AdditionalItemDisplayContext context) {
        if (displayMode == ItemDisplayContext.FIRST_PERSON_RIGHT_HAND) {
            return new Vec3(5, 5, 0);
        }
        if (displayMode == ItemDisplayContext.FIRST_PERSON_LEFT_HAND) {
            return new Vec3(5, -5, 0);
        }
        return getDefaultRotation();
    }

    default Vec3 getDefaultScale() {
        return new Vec3(1, 1, 1);
    }
    default Vec3 scale(ItemDisplayContext displayMode, AdditionalItemDisplayContext context) {
        return new Vec3(0.5, 0.5, 0.5);
    }
}
