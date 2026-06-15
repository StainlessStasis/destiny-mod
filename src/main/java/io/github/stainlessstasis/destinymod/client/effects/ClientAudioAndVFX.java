package io.github.stainlessstasis.destinymod.client.effects;

import io.github.stainlessstasis.destinymod.DMColor;
import io.github.stainlessstasis.destinymod.compat.LDL.FadeOutDynamicLightBehavior;
import io.github.stainlessstasis.destinymod.compat.LDL.LDLCompat;
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

import java.util.function.Consumer;

public class ClientAudioAndVFX {
    // Sound seeds from https://github.com/Owen1212055/mc-sound-seeds/blob/main/sound_seeds.json
    public static final long LIGHTNING_THUNDER_1 = -3143421179731086385L;
    public static final long LIGHTNING_THUNDER_2 = 4923755067258430535L;
    public static final long LIGHTNING_THUNDER_3 = -1383406444080597295L;
    public static final long AMETHYST_RESONATE_1 = 49309479271869866L;
    public static final long AMETHYST_RESONATE_2 = -1197613418293443803L;
    public static final long AMETHYST_RESONATE_3 = -2672010492342827392L;
    public static final long AMETHYST_RESONATE_4 = 186042584424253856L;

    public static void ignition(Level level, Vec3 center, float radius) {
        // SOUNDS
        float volume = radius/2f;
        level.playSeededSound(Minecraft.getInstance().player, center.x, center.y, center.z, SoundEvents.LIGHTNING_BOLT_THUNDER, SoundSource.AMBIENT, 1.2f*volume, 2f, LIGHTNING_THUNDER_3);
        level.playLocalSound(center.x, center.y, center.z, SoundEvents.FIRECHARGE_USE, SoundSource.AMBIENT, volume, 1.3f, true);
        level.playLocalSound(center.x, center.y, center.z, SoundEvents.FIRECHARGE_USE, SoundSource.AMBIENT, volume, 0.6f, true);
        level.playLocalSound(center.x, center.y, center.z, SoundEvents.LAVA_EXTINGUISH, SoundSource.AMBIENT, 0.8f*volume, 1.5f, true);
        level.playLocalSound(center.x, center.y, center.z, SoundEvents.BLAZE_BURN, SoundSource.AMBIENT, volume, 1.2f, true);

        // LDL COMPAT
        int lightRadius = (int) (radius * 2);
        addFadingLight(center, lightRadius, 20);

        // PARTICLES
        var particleEngine = Minecraft.getInstance().particleEngine;
        DMColor solar = DMColor.SOLAR;
        DMColor solarLight = DMColor.SOLAR_LIGHT;
        double x = center.x; double y = center.y; double z = center.z;

        ClientTaskScheduler.INSTANCE.runTaskMultiple(5, 0, 1, new CancellableRunnable() {
            @Override
            protected void execute() {
                forEachPointOnSphere(20, (context) -> {
                    Vec3 dir = context.direction();
                    double randomRadius = Math.cbrt(Math.random()) * radius;
                    Vec3 offset = dir.scale(randomRadius);
                    double px = x + offset.x;
                    double py = y + offset.y;
                    double pz = z + offset.z;

                    var particleType = Math.random() < 0.7 ? ParticleTypes.LAVA : ParticleTypes.FLAME;
                    Particle particle = particleEngine.createParticle(
                            particleType,
                            px, py, pz,
                            dir.x * 0.2, dir.y * 0.2, dir.z * 0.2
                    );
                    if (particle != null) {
                        if (particle instanceof SingleQuadParticle singleQuadParticle) {
                            DMColor color = Math.random() < 0.8 ? solar : solarLight;
                            singleQuadParticle.setColor(color.getRed(), color.getGreen(), color.getBlue());
                        }
                        particleEngine.add(particle);
                    }

                    if (context.index() < 5) {
                        Particle explosionParticle = particleEngine.createParticle(
                                ParticleTypes.EXPLOSION,
                                px, py, pz, 0, 0, 0
                        );
                        if (explosionParticle != null) {
                            if (explosionParticle instanceof SingleQuadParticle singleQuadParticle) {
                                DMColor color = context.index() > 3 ? DMColor.SOLAR : DMColor.SOLAR_DARK;
                                singleQuadParticle.setColor(color.getRed(), color.getGreen(), color.getBlue());
                            }
                            particleEngine.add(explosionParticle);
                        }
                    }
                });
            }
        });
    }

    public static void anvilDrop(Level level, Vec3 center, float radius) {
        // SOUNDS
        float volume = Math.clamp(radius/3f, 0.5f, 1.5f);
        level.playLocalSound(center.x, center.y, center.z, SoundEvents.ANVIL_LAND, SoundSource.AMBIENT, volume*0.8f, 1.1f, false);
        level.playLocalSound(center.x, center.y, center.z, SoundEvents.FIRECHARGE_USE, SoundSource.AMBIENT, volume, 1.7f, false);
        level.playLocalSound(center.x, center.y, center.z, SoundEvents.IRON_FALL, SoundSource.AMBIENT, volume, 0.7f, false);

        // PARTICLES
        var particleEngine = Minecraft.getInstance().particleEngine;
        DMColor solar = DMColor.SOLAR;
        DMColor solarLight = DMColor.SOLAR_LIGHT;
        int totalParticles = (int) (radius * 7);

        forEachPointOnSphere(totalParticles, (context) -> {
            Vec3 dir = context.direction();
            double randomRadius = Math.cbrt(Math.random()) * radius/2;
            Vec3 offset = dir.scale(randomRadius);

            double px = center.x + offset.x;
            double py = center.y + offset.y;
            double pz = center.z + offset.z;

            var particleType = Math.random() < 0.9 ? ParticleTypes.FLAME : ParticleTypes.LAVA;

            Particle particle = particleEngine.createParticle(
                    particleType,
                    px, py, pz,
                    dir.x * 0.1, dir.y * 0.1, dir.z * 0.1
            );

            if (particle != null) {
                if (particle instanceof SingleQuadParticle singleQuadParticle) {
                    DMColor color = Math.random() < 0.75 ? solar : solarLight;
                    singleQuadParticle.setColor(color.getRed(), color.getGreen(), color.getBlue());
                }
                particleEngine.add(particle);
            }

            if (context.index() % 10 == 0) {
                Particle explosionParticle = particleEngine.createParticle(
                        ParticleTypes.EXPLOSION,
                        px, py, pz, 0, 0, 0
                );
                if (explosionParticle != null) {
                    if (explosionParticle instanceof SingleQuadParticle singleQuadParticle) {
                        DMColor color = DMColor.SOLAR_DARK;
                        singleQuadParticle.setColor(color.getRed(), color.getGreen(), color.getBlue());
                        float scale = Math.clamp(radius * 0.3f, 0.4f, 0.8f);
                        singleQuadParticle.scale(scale);
                    }
                    particleEngine.add(explosionParticle);
                }
            }
        });
    }

    public static void sunspot(Level level, Vec3 center, RandomSource random, int tickCount) {
        double x = center.x;
        double y = center.y;
        double z = center.z;
        var particleEngine = Minecraft.getInstance().particleEngine;

        // SOUNDS
        boolean isFirstTick = tickCount == 1;
        if (isFirstTick) {
            level.playLocalSound(center.x, center.y, center.z, SoundEvents.BLAZE_SHOOT, SoundSource.AMBIENT, 1f, 1.5f, true);
            level.playLocalSound(center.x, center.y, center.z, SoundEvents.GENERIC_BURN, SoundSource.AMBIENT, 0.5f, 0.7f, true);
        }
        if (isFirstTick || tickCount % 30 == 0) {
            float randomPitch = random.nextFloat() * 0.1f;
            level.playLocalSound(center.x, center.y, center.z, SoundEvents.APPLY_EFFECT_RAID_OMEN, SoundSource.AMBIENT, 0.3f, 0.5f+randomPitch, true);
            randomPitch = random.nextFloat() * 0.2f;
            level.playLocalSound(center.x, center.y, center.z, SoundEvents.BLAZE_BURN, SoundSource.AMBIENT, 0.8f, 0.5f+randomPitch, true);
            level.playLocalSound(x, y, z, SoundEvents.CAMPFIRE_CRACKLE, SoundSource.AMBIENT, 1f, 0.9f+randomPitch, true);
        }

        // PARTICLES
        int pillarParticles = 3;
        for (int i = 0; i < pillarParticles; i++) {
            double angle = random.nextFloat() * 2f * Math.PI;
            double pillarRadius = random.nextFloat() * (SunspotEntity.RADIUS * 0.3f);
            double dx = Math.cos(angle) * pillarRadius;
            double dy = random.nextFloat();
            double dz = Math.sin(angle) * pillarRadius;
            double vx = (random.nextFloat() - 0.5f) * 0.02f;
            double vy = 0.15f + random.nextFloat() * 0.01f;
            double vz = (random.nextFloat() - 0.5f) * 0.02f;

            Particle particle = particleEngine.createParticle(
                    ParticleTypes.FLAME,
                    x + dx, y + dy, z + dz,
                    vx, vy, vz
            );
            if (particle instanceof SingleQuadParticle singleQuadParticle) {
                DMColor color = random.nextFloat() > 0.7f ? DMColor.SOLAR_LIGHT : DMColor.SOLAR;
                singleQuadParticle.setColor(color.getRed(), color.getGreen(), color.getBlue());

                float scale = 1.5f + random.nextFloat() * 0.75f;
                singleQuadParticle.scale(scale);

                particleEngine.add(singleQuadParticle);
            }
        }

        if (tickCount%4 != 0) return;

        int ringParticles = 30;
        for (int i = 0; i < ringParticles; i++) {
            double angle = (i * 2f * Math.PI) / ringParticles;
            double fuzz = (random.nextFloat() - 0.5f) * 0.3f;
            double currentRadius = SunspotEntity.RADIUS + fuzz;
            double dx = Math.cos(angle) * currentRadius;
            double dy = (random.nextFloat() - 0.2f) * 0.2f;
            double dz = Math.sin(angle) * currentRadius;
            double drift = 0.1f;
            double rise = 1f + random.nextFloat() * 0.5f;
            double vx = (-Math.sin(angle) * drift);
            double vy = (0.03f + random.nextFloat() * 0.04f) * rise;
            double vz = (Math.cos(angle) * drift);

            Particle particle = particleEngine.createParticle(ParticleTypes.SMALL_FLAME, x + dx, y + dy, z + dz, vx, vy, vz);
            if (particle instanceof SingleQuadParticle singleQuadParticle) {
                DMColor color = random.nextFloat() > 0.7f ? DMColor.SOLAR_DARK : DMColor.SOLAR;
                singleQuadParticle.setColor(color.getRed(), color.getGreen(), color.getBlue());
                singleQuadParticle.scale(1.5f + (random.nextFloat()*0.5f));
                particleEngine.add(singleQuadParticle);
            }
        }

        int coreParticles = 40;
        for (int i = 0; i < coreParticles; i++) {
            double angle = random.nextFloat() * 2f * Math.PI;
            double randomRadius = Math.sqrt(random.nextFloat()) * SunspotEntity.RADIUS;
            double dx = Math.cos(angle) * randomRadius;
            double dy = random.nextFloat() * 0.25f;
            double dz = Math.sin(angle) * randomRadius;
            double vx = (random.nextFloat() - 0.5f) * 0.04f;
            double vy = 0.02f + random.nextFloat() * 0.01f;
            double vz = (random.nextFloat() - 0.5f) * 0.04f;

            var particleType = random.nextFloat() < 0.25f ? ParticleTypes.LAVA : ParticleTypes.FLAME;
            Particle particle = particleEngine.createParticle(particleType, x + dx, y + dy, z + dz, vx, vy, vz);

            if (particle instanceof SingleQuadParticle singleQuadParticle) {
                DMColor color = random.nextFloat() > 0.85 ? DMColor.SOLAR_DARK : DMColor.SOLAR;
                singleQuadParticle.setColor(color.getRed(), color.getGreen(), color.getBlue());

                float scale = particleType == ParticleTypes.LAVA ? 1.1f : 1.8f + random.nextFloat() * 0.5f;
                singleQuadParticle.scale(scale);

                particleEngine.add(singleQuadParticle);
            }
        }
    }

    public static void thermitePulseStep(Level level, Vec3 pos, Vec3 forwardDir, Vec3 rightDir, float width, float height, boolean isFirstStep) {
        var random = level.getRandom();
        var particleEngine = Minecraft.getInstance().particleEngine;

        // SOUNDS
        if (isFirstStep) {
            level.playLocalSound(pos.x, pos.y, pos.z,
                    SoundEvents.FLINTANDSTEEL_USE, SoundSource.AMBIENT,
                    1f, 0.5f + random.nextFloat() * 0.1f, false);
            level.playLocalSound(pos.x, pos.y, pos.z,
                    SoundEvents.FIRECHARGE_USE, SoundSource.AMBIENT,
                    1f, 0.7f + random.nextFloat() * 0.1f, false);
            level.playLocalSound(pos.x, pos.y, pos.z,
                    SoundEvents.FIRECHARGE_USE, SoundSource.AMBIENT,
                    1f, 1.2f + random.nextFloat() * 0.1f, false);
            level.playLocalSound(pos.x, pos.y, pos.z,
                    SoundEvents.FIRE_AMBIENT, SoundSource.AMBIENT,
                    1f, 1.1f + random.nextFloat() * 0.2f, false);
        } else {
            if (random.nextFloat() < 0.07f) {
                level.playLocalSound(pos.x, pos.y, pos.z,
                        SoundEvents.LAVA_EXTINGUISH, SoundSource.AMBIENT,
                        0.5f, 1.2f + random.nextFloat() * 0.3f, false);
            }
        }

        // PARTICLES
        DMColor solarDark = DMColor.SOLAR_DARK;
        DMColor solar = DMColor.SOLAR;
        DMColor solarLight = DMColor.SOLAR_LIGHT;
        int density = 4;
        for (int i = 0; i < density; i++) {
            double widthBias = (random.nextFloat() - 0.5f) * width;
            Vec3 groundPos = pos.add(rightDir.scale(widthBias));

            double vx = (random.nextFloat() - 0.5f) * 0.03f;
            double vy = 0.005f + random.nextFloat() * 0.01f;
            double vz = (random.nextFloat() - 0.5f) * 0.03f;

            if (i == 0) {
                Particle sweep = particleEngine.createParticle(ParticleTypes.SWEEP_ATTACK, groundPos.x, groundPos.y + 0.2, groundPos.z, vx, vy, vz);
                if (sweep instanceof SingleQuadParticle particle) {
                    DMColor color = random.nextFloat() > 0.7f ? solarDark : solar;
                    particle.setColor(color.getRed(), color.getGreen(), color.getBlue());
                    particle.setLifetime((int) (2 + (random.nextFloat()*2f)));
                    particle.scale(random.nextFloat() * 2f);
                }
            }

            if (random.nextFloat() < 0.15f) {
                double spread = (random.nextFloat() - 0.5f) * 0.1f;
                vx = rightDir.x * spread + (random.nextFloat() - 0.5f) * 0.05f;
                vy = 0.5f + random.nextFloat() * 0.8f;
                vz = rightDir.z * spread + (random.nextFloat() - 0.5f) * 0.05f;

                Particle lava = particleEngine.createParticle(ParticleTypes.LAVA, groundPos.x, groundPos.y + 0.05, groundPos.z, vx, vy, vz);
                if (lava instanceof SingleQuadParticle particle) {
                    particle.setColor(solarLight.getRed(), solarLight.getGreen(), solarLight.getBlue());
                }
            }

            ClientTaskScheduler.INSTANCE.runTaskLater(2, new CancellableRunnable() {
                @Override
                protected void execute() {
                    double vx = (random.nextFloat() - 0.5f) * 0.03f;
                    double vy;
                    double vz = (random.nextFloat() - 0.5f) * 0.03f;

                    Particle smoke = particleEngine.createParticle(ParticleTypes.CAMPFIRE_SIGNAL_SMOKE, groundPos.x, groundPos.y, groundPos.z, vx, 0, vz);
                    if (smoke instanceof SingleQuadParticle particle) {
                        DMColor color = random.nextFloat() > 0.2f ? solarLight : solar;
                        particle.setColor(color.getRed(), color.getGreen(), color.getBlue());
                        particle.setLifetime((int) (10 + (random.nextFloat()*25)));
                        particle.scale(random.nextFloat()*2f);
                    }

                    if (random.nextFloat() < 0.7f) {
                        double biasedHeight = Math.pow(random.nextFloat(), 1.5) * (height / 2);
                        vx = (random.nextFloat() - 0.5f) * 0.01f;
                        vy = 0.05f + random.nextFloat() * 0.25f;
                        vz = (random.nextFloat() - 0.5f) * 0.01f;

                        Particle flame = particleEngine.createParticle(ParticleTypes.FLAME, groundPos.x, groundPos.y + biasedHeight, groundPos.z, vx, vy, vz);
                        if (flame instanceof SingleQuadParticle particle) {
                            DMColor color = random.nextFloat() > 0.4f ? solar : solarLight;
                            particle.setColor(color.getRed(), color.getGreen(), color.getBlue());
                            particle.scale(1.25f + random.nextFloat() * 0.5f);
                            particle.setLifetime((int) (10 + (random.nextFloat() * 10)));
                        }
                    }
                }
            });
        }
    }

    public record SpherePointContext(Vec3 direction, int index) {}
    public static void forEachPointOnSphere(int count, Consumer<SpherePointContext> action) {
        for (int i = 0; i < count; i++) {
            double u = Math.random();
            double v = Math.random();

            double theta = u * 2 * Math.PI;
            double phi = Math.acos(2 * v - 1);

            double dx = Math.sin(phi) * Math.cos(theta);
            double dy = Math.sin(phi) * Math.sin(theta);
            double dz = Math.cos(phi);

            action.accept(new SpherePointContext(new Vec3(dx, dy, dz), i));
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
