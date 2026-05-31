package io.github.stainlessstasis.destinymod.destiny_classes.ability.solar;

import io.github.stainlessstasis.destinymod.destiny_classes.DestinyElement;
import io.github.stainlessstasis.destinymod.destiny_classes.damage.DestinyDamageBuilder;
import io.github.stainlessstasis.destinymod.destiny_classes.damage.DestinyModDamageTypes;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jspecify.annotations.Nullable;

import java.util.List;
import java.util.function.Predicate;

public class Ignition {
    public static final float RANGE = 5f;
    public static final float RANGE_SQUARED = RANGE*RANGE;
    public static final float DAMAGE = 25f;

    public static void ignite(LivingEntity entity, @Nullable Entity directEntity, @Nullable Entity causingEntity) {
        if (!(entity.level() instanceof ServerLevel level)) return;

        Vec3 ignitionPos = entity.getEyePosition();
        AABB searchArea = AABB.ofSize(entity.getEyePosition(), 1f, 1f, 1f).inflate(RANGE);
        Predicate<LivingEntity> filter = target -> target.distanceToSqr(ignitionPos) <= RANGE_SQUARED;
        List<LivingEntity> victims = level.getEntitiesOfClass(LivingEntity.class, searchArea, filter);

        for (LivingEntity victim : victims) {
            float distSq = (float) victim.distanceToSqr(ignitionPos);
            float falloff = 1f - (distSq / RANGE_SQUARED);
            falloff = Math.max(0f, Math.min(1f, falloff));
            float damage = DAMAGE * falloff;
            if (damage <= 0.01f) continue;

            DestinyDamageBuilder.create(DestinyModDamageTypes.IGNITION, victim)
                    .directSource(directEntity)
                    .attacker(causingEntity)
                    .element(DestinyElement.SOLAR)
                    .damage(damage)
                    .invulnerabilityTicks(0)
                    .knockback(false)
                    .execute();
        }

        spawnIgnitionEffects(entity);
    }

    private static void spawnIgnitionEffects(Entity entity) {
        if (!(entity.level() instanceof ServerLevel level)) return;

        Vec3 center = entity.getEyePosition();
        double x = center.x; double y = center.y; double z = center.z;
        level.sendParticles(ParticleTypes.EXPLOSION, x, y, z, 1, 0, 0, 0, 0);

        int particleAmount = 80;
        for (int i = 0; i < particleAmount; i++) {
            double u = Math.random();
            double v = Math.random();
            double theta = u * 2 * Math.PI;
            double phi = Math.acos(2 * v - 1);

            double randomRadius = Math.cbrt(Math.random()) * RANGE;
            double dx = Math.sin(phi) * Math.cos(theta) * randomRadius;
            double dy = Math.sin(phi) * Math.sin(theta) * randomRadius;
            double dz = Math.cos(phi) * randomRadius;

            var particleType = Math.random() < 0.7 ? ParticleTypes.LAVA : ParticleTypes.FLAME;
            level.sendParticles(particleType, x, y, z, 1, dx, dy, dz, 0.2);
        }
    }
}
