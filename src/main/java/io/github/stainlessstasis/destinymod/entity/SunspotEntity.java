package io.github.stainlessstasis.destinymod.entity;

import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

public class SunspotEntity extends Entity implements TraceableEntity {
    private @Nullable EntityReference<LivingEntity> owner;

    public SunspotEntity(EntityType<? extends Entity> type, Level level) {
        super(type, level);
    }

    public SunspotEntity(EntityType<? extends Entity> type, Level level, @Nullable LivingEntity owner) {
        super(type, level);
        setOwner(owner);
    }

    public static SunspotEntity createDefault(EntityType<? extends Entity> entityType, Level level) {
        return new SunspotEntity(entityType, level);
    }

    public void setOwner(@Nullable LivingEntity owner) {
        this.owner = EntityReference.of(owner);
    }

    @Override
    public @Nullable Entity getOwner() {
        return null;
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.@NonNull Builder builder) {}
    @Override
    public boolean hurtServer(@NonNull ServerLevel serverLevel, @NonNull DamageSource damageSource, float v) {return false;}
    @Override
    protected void readAdditionalSaveData(@NonNull ValueInput valueInput) {
        discard();
    }
    @Override
    protected void addAdditionalSaveData(@NonNull ValueOutput valueOutput) {}
}
