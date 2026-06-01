package io.github.stainlessstasis.destinymod.client.effects;

import io.github.stainlessstasis.destinymod.DMColor;
import io.github.stainlessstasis.destinymod.compat.LDL.FadeOutDynamicLightBehavior;
import io.github.stainlessstasis.destinymod.compat.LDL.LDLCompat;
import io.github.stainlessstasis.destinymod.destiny_classes.ability.solar.Ignition;
import io.github.stainlessstasis.destinymod.entity.SunspotEntity;
import io.github.stainlessstasis.destinymod.task.CancellableRunnable;
import io.github.stainlessstasis.destinymod.task.ClientTaskScheduler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.SingleQuadParticle;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.neoforged.fml.ModList;

public class ClientAudioAndVFX {
    // Sound seeds from https://github.com/Owen1212055/mc-sound-seeds/blob/main/sound_seeds.json
    public static final long LIGHTNING_THUNDER_1 = -3143421179731086385L;
    public static final long LIGHTNING_THUNDER_2 = 4923755067258430535L;
    public static final long LIGHTNING_THUNDER_3 = -1383406444080597295L;

    public static void ignition(Level level, Vec3 center) {
        float radius = Ignition.RANGE;

        // SOUNDS
        float volume = radius/2f;
        level.playSeededSound(Minecraft.getInstance().player, center.x, center.y, center.z, SoundEvents.LIGHTNING_BOLT_THUNDER, SoundSource.AMBIENT, 1.2f*volume, 2f, LIGHTNING_THUNDER_3);
        level.playLocalSound(center.x, center.y, center.z, SoundEvents.FIRECHARGE_USE, SoundSource.AMBIENT, volume, 1.3f, true);
        level.playLocalSound(center.x, center.y, center.z, SoundEvents.FIRECHARGE_USE, SoundSource.AMBIENT, volume, 0.6f, true);
        level.playLocalSound(center.x, center.y, center.z, SoundEvents.LAVA_EXTINGUISH, SoundSource.AMBIENT, 0.8f*volume, 1.5f, true);
        level.playLocalSound(center.x, center.y, center.z, SoundEvents.BLAZE_BURN, SoundSource.AMBIENT, volume, 1.2f, true);

        // LDL COMPAT
        int lightRadius = (int) (Ignition.RANGE * 2);
        addFadingLight(center, lightRadius, 20);

        // PARTICLES
        var particleEngine = Minecraft.getInstance().particleEngine;
        DMColor solar = DMColor.SOLAR;
        DMColor solarDark = DMColor.SOLAR_DARK;
        double x = center.x; double y = center.y; double z = center.z;

        ClientTaskScheduler.INSTANCE.runTaskMultiple(5, 0, 1, new CancellableRunnable() {
            @Override
            protected void execute() {
                for (int i = 0; i < 20; i++) {
                    double u = Math.random();
                    double v = Math.random();
                    double theta = u * 2 * Math.PI;
                    double phi = Math.acos(2 * v - 1);

                    double randomRadius = Math.cbrt(Math.random()) * radius;
                    double dx = Math.sin(phi) * Math.cos(theta) * randomRadius;
                    double dy = Math.sin(phi) * Math.sin(theta) * randomRadius;
                    double dz = Math.cos(phi) * randomRadius;

                    var particleType = Math.random() < 0.7 ? ParticleTypes.LAVA : ParticleTypes.FLAME;
                    Particle particle = particleEngine.createParticle(
                            particleType,
                            x + dx, y + dy, z + dz,
                            dx * 0.2, dy * 0.2, dz * 0.2
                    );
                    if (particle != null) {
                        if (particle instanceof SingleQuadParticle singleQuadParticle) {
                            DMColor color = Math.random() < 0.8 ? solar : solarDark;
                            singleQuadParticle.setColor(color.getRed(), color.getGreen(), color.getBlue());
                        }
                        particleEngine.add(particle);
                    }

                    if (i < 5) {
                        Particle explosionParticle = particleEngine.createParticle(
                                ParticleTypes.EXPLOSION,
                                x+dx, y+dy, z+dz, 0, 0, 0
                        );
                        if (explosionParticle != null) {
                            if (explosionParticle instanceof SingleQuadParticle singleQuadParticle) {
                                DMColor color = i > 3 ? DMColor.SOLAR : DMColor.SOLAR_DARK;
                                singleQuadParticle.setColor(color.getRed(), color.getGreen(), color.getBlue());
                            }
                            particleEngine.add(explosionParticle);
                        }
                    }
                }
            }
        });
    }

    public static void sunspot(Level level, Vec3 center, RandomSource random) {
        double x = center.x;
        double y = center.y;
        double z = center.z;
        var particleEngine = Minecraft.getInstance().particleEngine;

        int ringParticles = 24;
        for (int i = 0; i < ringParticles; i++) {
            double angle = (i * 2f * Math.PI) / ringParticles;
            double fuzz = (random.nextFloat() - 0.5f) * 0.2f;
            double currentRadius = SunspotEntity.RADIUS + fuzz;
            double dx = Math.cos(angle) * currentRadius;
            double dz = Math.sin(angle) * currentRadius;
            double drift = 0.02f;
            double rise = 0.5f + random.nextFloat() * 0.5f;
            double vx = (dx * 0.005f) + (-Math.sin(angle) * drift);
            double vy = (0.02f + random.nextFloat() * 0.03f) * rise;
            double vz = (dz * 0.005f) + (Math.cos(angle) * drift);

            var particleType = random.nextFloat() < 0.2f ? ParticleTypes.SMALL_FLAME : ParticleTypes.FLAME;
            Particle particle = particleEngine.createParticle(particleType, x + dx, y, z + dz, vx, vy, vz);

            if (particle instanceof SingleQuadParticle singleQuadParticle) {
                DMColor color = random.nextFloat() > 0.7f ? DMColor.SOLAR_DARK : DMColor.SOLAR;
                singleQuadParticle.setColor(color.getRed(), color.getGreen(), color.getBlue());
                singleQuadParticle.scale(1f + (random.nextFloat() / 2f));
                particleEngine.add(singleQuadParticle);
            }
        }

        int coreParticles = 26;
        for (int i = 0; i < coreParticles; i++) {
            double angle = random.nextFloat() * 2f * Math.PI;
            double randomRadius = Math.pow(random.nextFloat(), 1.5f) * SunspotEntity.RADIUS;
            double dx = Math.cos(angle) * randomRadius;
            double dz = Math.sin(angle) * randomRadius;
            double vx = (random.nextFloat() - 0.5f) * 0.02f;
            double vy = 0.01f + random.nextFloat() * 0.02f;
            double vz = (random.nextFloat() - 0.5f) * 0.02f;

            var particleType = random.nextFloat() < 0.25f ? ParticleTypes.LAVA : ParticleTypes.FLAME;
            Particle particle = particleEngine.createParticle(particleType, x + dx, y, z + dz, vx, vy, vz);

            if (particle instanceof SingleQuadParticle singleQuadParticle) {
                DMColor color = random.nextFloat() > 0.85 ? DMColor.SOLAR_DARK : DMColor.SOLAR;
                singleQuadParticle.setColor(color.getRed(), color.getGreen(), color.getBlue());

                float scale = particleType == ParticleTypes.LAVA ? 1f : 1.3f + random.nextFloat() * 0.5f;
                singleQuadParticle.scale(scale);

                particleEngine.add(singleQuadParticle);
            }
        }
    }

    public static void addFadingLight(Vec3 pos, int radius, int ticks) {
        if (!ModList.get().isLoaded("lambdynlights")) {
            return;
        }

        FadeOutDynamicLightBehavior light = new FadeOutDynamicLightBehavior(pos, ticks, radius);
        LDLCompat.BEHAVIOR_MANAGER.add(light);
        ClientTaskScheduler.INSTANCE.runTaskRepeating(0, 1, new CancellableRunnable() {
            @Override
            protected void execute() {
                light.tick();
                if (light.isRemoved() || getCurrentIteration() > ticks) {
                    LDLCompat.BEHAVIOR_MANAGER.remove(light);
                    cancel();
                }
            }
        });
    }
}
