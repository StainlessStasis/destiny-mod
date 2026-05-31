package io.github.stainlessstasis.destinymod.client.particle;

import io.github.stainlessstasis.destinymod.DMColor;
import io.github.stainlessstasis.destinymod.destiny_classes.ability.solar.Ignition;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.SingleQuadParticle;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public class ClientParticleEffects {
    public static void ignition(Vec3 center) {
        var particleEngine = Minecraft.getInstance().particleEngine;
        DMColor solar = DMColor.SOLAR;
        DMColor solarDark = DMColor.SOLAR_DARK;
        double x = center.x; double y = center.y; double z = center.z;

        int particleAmount = 80;
        for (int i = 0; i < particleAmount; i++) {
            double u = Math.random();
            double v = Math.random();
            double theta = u * 2 * Math.PI;
            double phi = Math.acos(2 * v - 1);

            double randomRadius = Math.cbrt(Math.random()) * Ignition.RANGE;
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
                        DMColor color = DMColor.SOLAR;
                        singleQuadParticle.setColor(color.getRed(), color.getGreen(), color.getBlue());
                    }
                    particleEngine.add(explosionParticle);
                }
            }
        }
    }
}
