package io.github.stainlessstasis.destinymod.client.item_skin;

import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.phys.Vec3;

public interface ItemSkinTransform {
    default Vec3 getDefaultTranslation() {
        return new Vec3(0, 0, 0);
    }
    default Vec3 translation(ItemDisplayContext context) {
        return getDefaultTranslation();
    }
    default Vec3 getDefaultRotation() {
        return new Vec3(0, 0, 0);
    }
    default Vec3 rotation(ItemDisplayContext context) {
        return getDefaultRotation();
    }
    default Vec3 getDefaultScale() {
        return new Vec3(1, 1, 1);
    }
    default Vec3 scale(ItemDisplayContext context) {
        return getDefaultScale();
    }
}
