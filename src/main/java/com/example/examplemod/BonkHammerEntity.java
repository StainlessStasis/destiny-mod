package com.example.examplemod;

import com.example.examplemod.entity.DestinyModEntities;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.arrow.AbstractArrow;
import net.minecraft.world.entity.projectile.arrow.ThrownTrident;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

public class BonkHammerEntity extends AbstractArrow {
    public BonkHammerEntity(EntityType<? extends AbstractArrow> entityType, Level level) {
        super(entityType, level);
    }

    public BonkHammerEntity(Level level, LivingEntity owner, ItemStack tridentItem) {
        super(DestinyModEntities.HAMMER_OF_SOL.get(), owner, level, tridentItem, null);
    }

    public static BonkHammerEntity createDefault(EntityType<? extends AbstractArrow> entityType, Level level) {
        return new BonkHammerEntity(entityType, level);
    }

    @Override
    protected double getDefaultGravity() {
        System.out.println("Is client? "+this.level().isClientSide());
        System.out.println("Entity type: "+this.getType());
        return 0.005;
    }

    @Override
    protected @NotNull ItemStack getDefaultPickupItem() {
        return ItemStack.EMPTY;
    }
}
