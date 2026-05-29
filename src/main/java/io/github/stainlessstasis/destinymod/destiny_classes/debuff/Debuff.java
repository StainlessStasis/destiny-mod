package io.github.stainlessstasis.destinymod.destiny_classes.debuff;

import net.minecraft.world.entity.LivingEntity;

public interface Debuff {
    boolean isActive();
    void tick (LivingEntity entity);
    void clear();
}
