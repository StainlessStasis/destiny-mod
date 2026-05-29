package io.github.stainlessstasis.destinymod.destiny_classes.debuff;

import net.minecraft.world.entity.EntityReference;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public interface Debuff {
    boolean isActive();
    void tick (LivingEntity entity);
    void clear();

    default @Nullable EntityReference<LivingEntity> getOwnerReference() {
        return null;
    }

    default Optional<LivingEntity> getOwnerOptional(Level level) {
        return Optional.ofNullable(getOwner(level));
    }

    default @Nullable LivingEntity getOwner(Level level) {
        EntityReference<LivingEntity> ref = this.getOwnerReference();
        return ref == null ? null : EntityReference.getLivingEntity(ref, level);
    }
}
