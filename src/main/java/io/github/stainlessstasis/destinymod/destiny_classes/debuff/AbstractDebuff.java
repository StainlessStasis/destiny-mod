package io.github.stainlessstasis.destinymod.destiny_classes.debuff;

import net.minecraft.world.entity.LivingEntity;

public abstract class AbstractDebuff implements Debuff {
    protected int tickCount = 0;

    @Override
    public void tick(LivingEntity entity) {
        tickCount++;
    }
}
