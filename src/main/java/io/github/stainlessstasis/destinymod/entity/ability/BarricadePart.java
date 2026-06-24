package io.github.stainlessstasis.destinymod.entity.ability;

import io.github.stainlessstasis.destinymod.entity.collision.OrientedPartEntity;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jspecify.annotations.NonNull;

public class BarricadePart extends OrientedPartEntity<BarricadeEntity> {
    public BarricadePart(BarricadeEntity parent, AABB box) {
        super(parent);
        Vec3 center = box.getCenter();
        setPos(center.x, box.minY, center.z);
        noPhysics = true;

        float width = (float)(box.maxX - box.minX);
        float height = (float)(box.maxY - box.minY);
        dimensions = EntityDimensions.scalable(width, height);
        setBoundingBox(box);
        refreshDimensions();
    }

    @Override
    public boolean hurtServer(@NonNull ServerLevel level, @NonNull DamageSource source, float damage) {
        if (isInvulnerableToBase(source)) return false;
        return getParent().hurtServer(level, source, damage);
    }

    @Override
    public @NonNull EntityDimensions getDimensions(@NonNull Pose pose) {
        return dimensions;
    }

    @Override
    public boolean canBeHitByProjectile() {
        return isAlive();
    }

    @Override
    protected void readAdditionalSaveData(@NonNull ValueInput valueInput) {discard();}
    @Override
    protected void addAdditionalSaveData(@NonNull ValueOutput valueOutput) {}
    @Override
    protected void defineSynchedData(SynchedEntityData.@NonNull Builder builder) {}
    @Override
    public boolean shouldBeSaved() { return false; }
}
