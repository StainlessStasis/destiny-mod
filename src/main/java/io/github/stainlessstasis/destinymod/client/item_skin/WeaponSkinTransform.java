package io.github.stainlessstasis.destinymod.client.item_skin;

import org.joml.Vector3f;

public interface WeaponSkinTransform {
    default Vector3f translation() { return new Vector3f(0, 0, 0); }
    default Vector3f rotation() { return new Vector3f(0, 0, 0); } // degrees, XYZ
    default Vector3f scale() { return new Vector3f(1, 1, 1); }
}
