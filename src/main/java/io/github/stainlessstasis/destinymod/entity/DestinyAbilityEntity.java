package io.github.stainlessstasis.destinymod.entity;

import io.github.stainlessstasis.destinymod.destiny_combat.ability.Ability;
import io.github.stainlessstasis.destinymod.destiny_combat.ability.DestinyAbility;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

public abstract class DestinyAbilityEntity extends Entity implements TraceableEntity, DestinyAbility {
    protected final Ability ability;
    protected @Nullable EntityReference<LivingEntity> owner;

    protected DestinyAbilityEntity(EntityType<?> type, Level level, Ability ability) {
        super(type, level);
        this.ability = ability;
        noPhysics = true;
        refreshDimensions();
    }

    public DestinyAbilityEntity(EntityType<?> type, Level level, Vec3 pos, @Nullable LivingEntity owner, Ability ability) {
        this(type, level, ability);
        setPos(pos);
        setOwner(owner);
    }

    @Override
    public @NotNull Ability getDestinyAbility() {
        return ability;
    }

    public void setOwner(@Nullable LivingEntity owner) {
        this.owner = EntityReference.of(owner);
    }
    @Override
    public @Nullable LivingEntity getOwner() {
        return EntityReference.getLivingEntity(owner, level());
    }

    @Override
    public boolean hurtServer(@NonNull ServerLevel serverLevel, @NonNull DamageSource damageSource, float v) {
        return false;
    }
    @Override
    protected void readAdditionalSaveData(@NonNull ValueInput valueInput) {
        discard();
    }
    @Override
    protected void addAdditionalSaveData(@NonNull ValueOutput valueOutput) {}
}
