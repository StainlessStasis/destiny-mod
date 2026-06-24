package io.github.stainlessstasis.destinymod.entity.ability;

import io.github.stainlessstasis.destinymod.destiny_combat.ability.Ability;
import io.github.stainlessstasis.destinymod.registry.datapack.Abilities;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.entity.PartEntity;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class BarricadeEntity extends DestinyAbilityEntity {
    public static final float WIDTH = 3f;
    public static final float HALF_WIDTH = WIDTH/2f;
    public static final float HEIGHT = 2.2f;
    public static final float DEPTH = 0.25f;
    public static final float HALF_DEPTH = DEPTH/2f;
    public static final float SEGMENT_SIZE = 0.25f;
    public static final float HALF_SEGMENT_SIZE = SEGMENT_SIZE /2f;
    public static final int SEGMENT_COUNT = (int) Math.ceil((WIDTH / SEGMENT_SIZE));

    public static BarricadeEntity createDefault(EntityType<? extends DestinyAbilityEntity> entityType, Level level) {
        return new BarricadeEntity(entityType, level, Vec3.ZERO, null, Abilities.BARRICADE.get(level));
    }

    private final List<BarricadePart> segments = new ArrayList<>();

    public BarricadeEntity(EntityType<?> type, Level level, Vec3 pos, @Nullable LivingEntity owner, Ability ability) {
        super(type, level, pos, owner, ability);
        if (owner != null) {
            setYRot(owner.getYRot());
        }
        refreshDimensions();
        makeBoundingBox(position());
        if (level instanceof ServerLevel serverLevel) {
            spawnSegments(serverLevel);
        }
    }

    @Override
    public void tick() {
        super.tick();
        if (level() instanceof ServerLevel level) {
            debugSegments(level);
        }
    }

    @Override
    public boolean hurtServer(@NonNull ServerLevel serverLevel, @NonNull DamageSource damageSource, float v) {
        System.out.println("OUCH");
        return true;
    }

    public void spawnSegments(ServerLevel level) {
        float yawRad = (float) Math.toRadians(getYRot());
        float cos = Mth.cos(yawRad);
        float sin = Mth.sin(yawRad);

        for (int i = 0; i < SEGMENT_COUNT; i++) {
            // center of this segment in local space
            float localX = -WIDTH / 2f + (i * SEGMENT_SIZE) + HALF_SEGMENT_SIZE;

            double x = getX() + (localX * cos);
            double z = getZ() + (localX * sin);
            double halfX = Math.abs(HALF_SEGMENT_SIZE * cos) + Math.abs(HALF_DEPTH * sin);
            double halfZ = Math.abs(HALF_SEGMENT_SIZE * sin) + Math.abs(HALF_DEPTH * cos);

            AABB box = new AABB(
                    x - halfX, getY(), z - halfZ,
                    x + halfX, getY() + HEIGHT, z + halfZ
            );

            BarricadePart segment = new BarricadePart(this, box);
            segments.add(segment);
        }
    }

    public void debugSegments(ServerLevel level) {
        for (BarricadePart segment : segments) {
            AABB box = segment.getBoundingBox();
            double[][] corners = {
                    {box.minX, box.minY, box.minZ},
                    {box.maxX, box.minY, box.minZ},
                    {box.minX, box.minY, box.maxZ},
                    {box.maxX, box.minY, box.maxZ},
                    {box.minX, box.maxY, box.minZ},
                    {box.maxX, box.maxY, box.minZ},
                    {box.minX, box.maxY, box.maxZ},
                    {box.maxX, box.maxY, box.maxZ},
            };
            for (double[] c : corners) {
                level.sendParticles(ParticleTypes.FLAME, c[0], c[1], c[2], 1, 0, 0, 0, 0);
            }
        }
    }

    @Override
    public boolean isMultipartEntity() {
        return true;
    }

    @Override
    public PartEntity<?>@NonNull[] getParts() {
        return segments.toArray(new BarricadePart[0]);
    }

    @Override
    public boolean isValidAbilityTarget() {
        return true;
    }

    @Override
    public boolean canBeHitByProjectile() {
        return false;
    }

    @Override
    public void remove(@NonNull RemovalReason reason) {
        super.remove(reason);
        segments.forEach(segment -> segment.remove(reason));
        segments.clear();
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.@NonNull Builder builder) {}
}
