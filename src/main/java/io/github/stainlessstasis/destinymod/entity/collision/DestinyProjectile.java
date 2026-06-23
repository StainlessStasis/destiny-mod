package io.github.stainlessstasis.destinymod.entity.collision;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;

public interface DestinyProjectile {
    Set<UUID> getCollidedThisTick();

    default void moveAndCollide() {
        if (!(this instanceof Entity entity)) return;
        moveAndCollide(entity.getDeltaMovement());
    }

    default void moveAndCollide(Vec3 movement) {
        if (!(this instanceof Entity entity)) return;

        Optional<CollisionContext> collision = ProjectileCollisionUtils.checkCollisions(entity, getCollidedThisTick(), movement, true);
        if (collision.isPresent()) {
            onCollisionResult(collision.get());
        } else {
            entity.setPos(entity.position().add(movement));
        }
    }

    default void onCollisionResult(CollisionContext context) {
        if (context.result() instanceof BlockHitResult blockHitResult) {
            handleBlockCollision(context, blockHitResult);
        }
        if (context.result() instanceof EntityHitResult entityHitResult) {
            handleEntityCollision(context, entityHitResult);
        }
    }

    void handleBlockCollision(CollisionContext context, BlockHitResult result);
    void handleEntityCollision(CollisionContext context, EntityHitResult result);
}
