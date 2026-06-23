package io.github.stainlessstasis.destinymod.entity.ability;

import io.github.stainlessstasis.destinymod.destiny_combat.ability.Ability;
import io.github.stainlessstasis.destinymod.entity.collision.OBBEntity;
import io.github.stainlessstasis.destinymod.registry.datapack.Abilities;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

public class BarricadeEntity extends DestinyAbilityEntity implements OBBEntity {
    public static final float WIDTH = 3.25f;
    public static final float HEIGHT = 2.1f;
    public static final float DEPTH = 0.5f;
    public static final Vec3 HALF_EXTENTS = new Vec3(WIDTH/2, HEIGHT/2, DEPTH/2);

    private Vec3 obbCenter = Vec3.ZERO;

    public static BarricadeEntity createDefault(EntityType<? extends DestinyAbilityEntity> entityType, Level level) {
        return new BarricadeEntity(entityType, level, Vec3.ZERO, null, Abilities.BARRICADE.get(level));
    }

    public BarricadeEntity(EntityType<?> type, Level level, Vec3 pos, @Nullable LivingEntity owner, Ability ability) {
        super(type, level, pos, owner, ability);
        setOBBCenter(new Vec3(pos.x, pos.y + (HEIGHT/2f), pos.z));
        setBoundingBox(getConservativeAABB());

        if (owner != null) {
            setYRot(owner.getYRot());
        }
    }

    @Override
    public void tick() {
        super.tick();
        if (level() instanceof ServerLevel level) {
            debugRenderOBB(level);
        }
    }

    private void debugRenderOBB(ServerLevel level) {
        Vec3 center = getOBBCenter();
        Vec3 half = getHalfExtents();
        float yaw = getOBBYaw();

        double[] xs = {-half.x, half.x};
        double[] ys = {-half.y, half.y};
        double[] zs = {-half.z, half.z};

        for (double x : xs) {
            for (double y : ys) {
                for (double z : zs) {
                    Vec3 corner = new Vec3(x, y, z).yRot(-yaw).add(center);
                    level.sendParticles(ParticleTypes.FLAME,
                            corner.x, corner.y, corner.z,
                            1, 0, 0, 0, 0);
                }
            }
        }
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.@NonNull Builder builder) {}

    @Override
    protected void reapplyPosition() {
        super.reapplyPosition();
        if (obbCenter != null) {
            setBoundingBox(getConservativeAABB());
        }
    }

    public void setOBBCenter(Vec3 center) {
        this.obbCenter = center;
    }

    @Override
    public Vec3 getOBBCenter() {
        return obbCenter;
    }

    @Override
    public Vec3 getHalfExtents() {
        return new Vec3(HALF_EXTENTS.x, HALF_EXTENTS.y, HALF_EXTENTS.z);
    }

    @Override
    public float getOBBYaw() {
        return (float) Math.toRadians(getYRot());
    }

    // prevent vanilla projectiles from colliding with the AABB,
    // since it is replaced with an OBB test in ProjectileUtilMixin
    @Override
    public boolean canBeHitByProjectile() {
        return false;
    }
}
