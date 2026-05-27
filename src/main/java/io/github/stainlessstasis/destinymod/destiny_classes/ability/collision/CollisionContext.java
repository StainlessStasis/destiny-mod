package io.github.stainlessstasis.destinymod.destiny_classes.ability.collision;

import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

/**
 * Holds the result of a projectile collision test.
 * @param result The hit result (BlockHitResult or EntityHitResult).
 * @param normal The surface normal at the point of impact.
 * @param sourceVelocity The velocity of the source of this collision.
 * @param targetVelocity The velocity of the target that was hit.
 * @param sourceMass The mass of the source of this collision.
 * @param targetMass The mass of the target that was hit, which is used for calculating impulse.
 *                   Blocks and most other entities will always have a mass of 0.
 * @param timeOfImpact Normalized time within the current tick when the collision occurs.
 */
public record CollisionContext(
        HitResult result,
        Vec3 normal,
        Vec3 sourceVelocity,
        Vec3 targetVelocity,
        float sourceMass,
        float targetMass,
        double timeOfImpact
) {}