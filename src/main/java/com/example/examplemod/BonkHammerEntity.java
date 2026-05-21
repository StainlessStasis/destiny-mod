package com.example.examplemod;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.arrow.ThrownTrident;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class BonkHammerEntity extends ThrownTrident {
    public BonkHammerEntity(EntityType<? extends ThrownTrident> type, Level level) {
        super(type, level);
    }

    public BonkHammerEntity(Level level, LivingEntity owner) {
        this(level, owner, ItemStack.EMPTY);
    }

    public BonkHammerEntity(Level level, LivingEntity owner, ItemStack stack) {
        super(level, owner, stack);
    }
}
