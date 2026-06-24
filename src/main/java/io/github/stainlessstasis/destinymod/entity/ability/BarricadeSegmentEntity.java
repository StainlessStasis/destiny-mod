package io.github.stainlessstasis.destinymod.entity.ability;

import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.entity.PartEntity;
import org.jspecify.annotations.NonNull;

public class BarricadeSegmentEntity extends PartEntity<BarricadeEntity> {
    public BarricadeSegmentEntity(BarricadeEntity parent, AABB box) {
        super(parent);
        Vec3 center = box.getCenter();
        this.setPos(center.x, box.minY, center.z);
        this.setBoundingBox(box);
    }

    @Override
    public boolean is(@NonNull Entity other) {
        return this == other || this.getParent() == other;
    }

    @Override
    public boolean hurtServer(@NonNull ServerLevel level, @NonNull DamageSource source, float damage) {
        return !this.isInvulnerableToBase(source) && getParent().hurtServer(level, source, damage);
    }

    @Override
    public boolean canBeHitByProjectile() { return true; }

    @Override
    public boolean isPickable() { return true; }

    @Override
    protected void readAdditionalSaveData(@NonNull ValueInput valueInput) {discard();}
    @Override
    protected void addAdditionalSaveData(@NonNull ValueOutput valueOutput) {}
    @Override
    protected void defineSynchedData(SynchedEntityData.@NonNull Builder builder) {}
    @Override
    public boolean shouldBeSaved() { return false; }
}
