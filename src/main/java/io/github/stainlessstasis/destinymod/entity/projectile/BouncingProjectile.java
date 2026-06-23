package io.github.stainlessstasis.destinymod.entity.projectile;

import com.mojang.math.Constants;
import io.github.stainlessstasis.destinymod.entity.collision.CollisionContext;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;

public interface BouncingProjectile extends DestinyProjectile {
    float getRestitution();
    float getFriction();
    float getSettleSpeedThreshold();

    @Override
    default void onCollisionResult(CollisionContext context) {
        DestinyProjectile.super.onCollisionResult(context);
        handleBounce(context);
    }

    default void handleBounce(CollisionContext context) {
        if (!(this instanceof Entity entity)) return;

        Vec3 position = context.result().getLocation();
        Vec3 normal = context.normal();
        Vec3 newVel = applyBounce(entity.getDeltaMovement(), context);

        if (normal.length() > Constants.EPSILON && newVel.length() < getSettleSpeedThreshold()) {
            onSettle(context);
        } else {
            entity.setPos(position.add(normal.scale(0.005))); // prevent infinite loops
            entity.setDeltaMovement(newVel);
        }
    }

    default Vec3 applyBounce(Vec3 velocity, CollisionContext context) {
        Vec3 normal = context.normal();
        Vec3 relative = velocity.subtract(context.targetVelocity());
        double normalSpeed = relative.dot(normal);
        Vec3 scaledNormal = normal.scale(normalSpeed);

        float mass = 1f;
        float targetMass = context.targetMass();
        double impulse = (1f / mass) + (targetMass > 0 ? 1f / targetMass : 0f);

        double j = -(1 + getRestitution()) * normalSpeed / impulse;
        Vec3 bouncedNormal = normal.scale(j / mass);

        Vec3 tangential = relative.subtract(scaledNormal);
        Vec3 friction = tangential.scale(getFriction());

        return velocity.add(bouncedNormal).subtract(friction);
    }

    void onSettle(CollisionContext context);
}
