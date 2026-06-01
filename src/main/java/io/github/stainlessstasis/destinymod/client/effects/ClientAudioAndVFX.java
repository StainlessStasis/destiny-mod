package io.github.stainlessstasis.destinymod.client.effects;

import io.github.stainlessstasis.destinymod.DMColor;
import io.github.stainlessstasis.destinymod.destiny_classes.ability.solar.Ignition;
import io.github.stainlessstasis.destinymod.task.CancellableRunnable;
import io.github.stainlessstasis.destinymod.task.ClientTaskScheduler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.SingleQuadParticle;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

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
}
