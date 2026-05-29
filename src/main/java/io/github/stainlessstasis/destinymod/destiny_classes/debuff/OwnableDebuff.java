package io.github.stainlessstasis.destinymod.destiny_classes.debuff;

import net.minecraft.world.entity.EntityReference;
import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public abstract class OwnableDebuff implements Debuff {
    @Nullable
    protected EntityReference<LivingEntity> ownerReference = null;

    protected OwnableDebuff() {}

    protected OwnableDebuff(Optional<EntityReference<LivingEntity>> ownerReference) {
        this.ownerReference = ownerReference.orElse(null);
    }

    public void setOwner(@Nullable LivingEntity attacker) {
        this.ownerReference = attacker == null ? null : EntityReference.of(attacker);
    }

    @Override
    @Nullable
    public EntityReference<LivingEntity> getOwnerReference() {
        return this.ownerReference;
    }

    @Override
    public void clear() {
        this.ownerReference = null;
    }
}
