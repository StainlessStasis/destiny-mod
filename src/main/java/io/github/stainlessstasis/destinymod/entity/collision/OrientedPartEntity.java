package io.github.stainlessstasis.destinymod.entity.collision;

import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.entity.PartEntity;
import org.jspecify.annotations.NonNull;

public abstract class OrientedPartEntity<T extends Entity> extends PartEntity<T> {
    public OrientedPartEntity(T parent) {
        super(parent);
    }

    @Override
    public boolean is(@NonNull Entity other) {
        if (super.is(other)) return true;

        var parent = getParent();
        if (other instanceof PartEntity<?> otherPart) {
            if (parent.is(otherPart.getParent())) return true;
        }
        return parent == other;
    }

    public Vec3 getSurfaceNormal(Vec3 rawAABBNormal) {
        return getSurfaceNormal(rawAABBNormal, Vec3.ZERO);
    }

    /**
     * Takes the raw normal from a collision against an AABB and uses this part's parent's yaw
     * to transform the normal into something more accurate for a collision against a rotated entity.
     * Used by barricades.
     */
    public Vec3 getSurfaceNormal(Vec3 rawAABBNormal, Vec3 incomingVelocity) {
        float yawRad = (float) Math.toRadians(getParent().getYRot());
        Vec3 facingDir = new Vec3(-Mth.sin(yawRad), 0, Mth.cos(yawRad));
        Vec3 rightDir = new Vec3(Mth.cos(yawRad), 0, Mth.sin(yawRad));

        // determine which face was hit by seeing which axis most closely aligns with the raw AABB normal
        double facingDot = Math.abs(facingDir.dot(rawAABBNormal));
        double rightDot = Math.abs(rightDir.dot(rawAABBNormal));
        double upDot = Math.abs(rawAABBNormal.y);

        if (facingDot >= rightDot && facingDot >= upDot) {
            // hit the front or back face
            return facingDir.dot(rawAABBNormal) >= 0 ? facingDir : facingDir.scale(-1);
        } else if (rightDot >= facingDot && rightDot >= upDot) {
            // hit a side edge
            return rightDir.dot(rawAABBNormal) >= 0 ? rightDir : rightDir.scale(-1);
        } else {
            // hit top or bottom
            return rawAABBNormal;
        }
    }
}
