package io.github.stainlessstasis.destinymod.entity.collision;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public interface OBBEntity {
    /**
     * Half-extents in local space (before rotation).
     * x = half-width, y = half-height, z = half-depth
     */
    Vec3 getHalfExtents();

    /**
     * Yaw in radians.
     */
    float getOBBYaw();

    /**
     * Center of the OBB in world space. Defaults to eye position.
     */
    default Vec3 getOBBCenter() {
        if (!(this instanceof Entity entity)) throw new IllegalStateException();
        return entity.getBoundingBox().getCenter();
    }

    /**
     * Tests whether a ray intersects this OBB.
     * Returns the t value of intersection, or Double.POSITIVE_INFINITY if no hit.
     * Direction should NOT be normalized.
     */
    default double rayIntersect(Vec3 rayOrigin, Vec3 rayDir) {
        Vec3 center = getOBBCenter();
        float yaw = getOBBYaw();

        Vec3 localOrigin = rayOrigin.subtract(center).yRot(-yaw);
        Vec3 localDir = rayDir.yRot(-yaw);

        return rayAABB(localOrigin, localDir, getHalfExtents());
    }

    /**
     * Ray vs AABB, centered at origin with given half-extents.
     * Returns t of entry, or POSITIVE_INFINITY on miss.
     */
    static double rayAABB(Vec3 origin, Vec3 dir, Vec3 half) {
        double tMin = Double.NEGATIVE_INFINITY;
        double tMax = Double.POSITIVE_INFINITY;

        double[] rayOriginComponents = {origin.x, origin.y, origin.z};
        double[] rayDirComponents = {dir.x, dir.y, dir.z};
        double[] halfExtentComponents = {half.x, half.y, half.z};

        for (int i = 0; i < 3; i++) {
            if (Math.abs(rayDirComponents[i]) < 1e-8) {
                // Ray is parallel; must be inside to hit
                if (Math.abs(rayOriginComponents[i]) > halfExtentComponents[i]) return Double.POSITIVE_INFINITY;
            } else {
                double t1 = (-halfExtentComponents[i] - rayOriginComponents[i]) / rayDirComponents[i];
                double t2 = ( halfExtentComponents[i] - rayOriginComponents[i]) / rayDirComponents[i];
                if (t1 > t2) { double temp = t1; t1 = t2; t2 = temp; }
                tMin = Math.max(tMin, t1);
                tMax = Math.min(tMax, t2);
                if (tMin > tMax) return Double.POSITIVE_INFINITY;
            }
        }

        double t = tMin >= 0 ? tMin : tMax;
        if (t < 0 || t > 1) return Double.POSITIVE_INFINITY;
        return t;
    }

    /**
     * Conservative AABB that fully contains the OBB at any rotation.
     * Use this as the entity's actual bounding box so vanilla systems still work correctly.
     */
    default AABB getConservativeAABB() {
        Vec3 center = getOBBCenter();
        Vec3 half = getHalfExtents();
        float yaw = getOBBYaw();

        double worldHalfX = Math.abs(half.x * Math.cos(yaw)) + Math.abs(half.z * Math.sin(yaw));
        double worldHalfY = half.y;
        double worldHalfZ = Math.abs(half.x * Math.sin(yaw)) + Math.abs(half.z * Math.cos(yaw));

        return new AABB(
                center.x - worldHalfX, center.y - worldHalfY, center.z - worldHalfZ,
                center.x + worldHalfX, center.y + worldHalfY, center.z + worldHalfZ
        );
    }
}
