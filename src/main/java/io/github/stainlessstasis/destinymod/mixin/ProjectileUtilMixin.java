package io.github.stainlessstasis.destinymod.mixin;

import io.github.stainlessstasis.destinymod.entity.collision.OBBEntity;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Predicate;

@Mixin(ProjectileUtil.class)
public class ProjectileUtilMixin {

    @Inject(
            method = "getManyEntityHitResult(" +
                    "Lnet/minecraft/world/level/Level;" +
                    "Lnet/minecraft/world/entity/Entity;" +
                    "Lnet/minecraft/world/phys/Vec3;" +
                    "Lnet/minecraft/world/phys/Vec3;" +
                    "Lnet/minecraft/world/phys/AABB;" +
                    "Ljava/util/function/Predicate;F" +
                    "Lnet/minecraft/world/level/ClipContext$Block;Z)" +
                    "Ljava/util/Collection;",
            at = @At("RETURN"),
            cancellable = true
    )
    private static void checkOBBEntities(
            Level level, Entity projectile, Vec3 from, Vec3 to, AABB searchArea, Predicate<Entity> filter, float entityMargin,
            ClipContext.Block clipType, boolean includeFromEntity, CallbackInfoReturnable<Collection<EntityHitResult>> cir
    ) {
        Vec3 rayDir = to.subtract(from);

        // TODO: use CombatUtils method
        List<Entity> obbCandidates = level.getEntities(projectile, searchArea,
                entity -> entity instanceof OBBEntity && filter.test(entity));

        if (obbCandidates.isEmpty()) return;
        System.out.println("CANDIDATES: "+obbCandidates);

        List<EntityHitResult> results = new ArrayList<>(cir.getReturnValue());

        for (Entity candidate : obbCandidates) {
            // skip if already hit by vanilla AABB test
            boolean alreadyHit = results.stream().anyMatch(result -> result.getEntity() == candidate);
            if (alreadyHit) continue;

            OBBEntity obb = (OBBEntity) candidate;
            double t = obb.rayIntersect(from, rayDir);
            if (t == Double.POSITIVE_INFINITY) continue;

            Vec3 hitPos = from.add(rayDir.scale(t));
            results.add(new EntityHitResult(candidate, hitPos));
        }

        if (results.size() != cir.getReturnValue().size()) {
            cir.setReturnValue(results);
        }
    }
}