package io.github.stainlessstasis.destinymod.mixin;

import io.github.stainlessstasis.destinymod.entity.collision.OBBEntity;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;
import java.util.function.Predicate;

@Mixin(ProjectileUtil.class)
public class ProjectileUtilMixin {

    @Inject(
            method = "getEntityHitResult(" +
                    "Lnet/minecraft/world/level/Level;" +
                    "Lnet/minecraft/world/entity/Entity;" +
                    "Lnet/minecraft/world/phys/Vec3;" +
                    "Lnet/minecraft/world/phys/Vec3;" +
                    "Lnet/minecraft/world/phys/AABB;" +
                    "Ljava/util/function/Predicate;F)" +
                    "Lnet/minecraft/world/phys/EntityHitResult;",
            at = @At("RETURN"),
            cancellable = true
    )
    private static void checkOBBEntities(
            Level level, Entity projectile, Vec3 start, Vec3 end, AABB searchArea, Predicate<Entity> filter, float entityMargin, CallbackInfoReturnable<EntityHitResult> cir
    ) {
        Vec3 rayDir = end.subtract(start);

        // Find the best hit distance so we only replace if OBB is closer
        double bestDist = Double.POSITIVE_INFINITY;
        if (cir.getReturnValue() != null) {
            bestDist = cir.getReturnValue().getLocation().distanceTo(start);
        }

        EntityHitResult bestHit = cir.getReturnValue();

        // Check all OBBEntity candidates in the sweep volume
        List<Entity> candidates = level.getEntities(projectile, searchArea,
                entity -> entity instanceof OBBEntity && filter.test(entity));

        for (Entity candidate : candidates) {
            OBBEntity obb = (OBBEntity) candidate;
            double t = obb.rayIntersect(start, rayDir);
            if (t == Double.POSITIVE_INFINITY) continue;

            double dist = t * rayDir.length();
            if (dist < bestDist) {
                bestDist = dist;
                bestHit = new EntityHitResult(candidate, start.add(rayDir.scale(t)));
            }
        }

        if (bestHit != cir.getReturnValue()) {
            cir.setReturnValue(bestHit);
        }
    }
}