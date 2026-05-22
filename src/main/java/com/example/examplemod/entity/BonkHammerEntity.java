package com.example.examplemod.entity;

import com.geckolib.animatable.GeoEntity;
import com.geckolib.animatable.instance.AnimatableInstanceCache;
import com.geckolib.animatable.manager.AnimatableManager;
import com.geckolib.util.GeckoLibUtil;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.arrow.AbstractArrow;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

public class BonkHammerEntity extends AbstractArrow implements GeoEntity {
    private final AnimatableInstanceCache geoCache = GeckoLibUtil.createInstanceCache(this);
    private static final float BOUNCE_FACTOR = 0.45f;
    private static final float FRICTION = 0.7f;
    private static final float STICK_SPEED_THRESHOLD = 0.15f;

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
    public void tick() {
        super.tick();
    }

    // TODO: copy over AbstractArrow stuff and directly modify it to ensure stuff works on all block hits that don't stick (e.g. target blocks)
    @Override
    protected void onHitBlock(BlockHitResult hitResult) {
        Direction hitFace = hitResult.getDirection();
        Vec3 motion = this.getDeltaMovement();
        double speed = motion.length();
        if (speed < STICK_SPEED_THRESHOLD) {
            super.onHitBlock(hitResult);
            return;
        }

        double x = motion.x;
        double y = motion.y;
        double z = motion.z;
        switch (hitFace) {
            case UP -> {
                y = -y * BOUNCE_FACTOR;
                x *= FRICTION;
                z *= FRICTION;
            }
            case DOWN -> {
                y = -y * BOUNCE_FACTOR;
            }
            case NORTH, SOUTH -> {
                z = -z * BOUNCE_FACTOR;
                x *= FRICTION;
            }
            case EAST, WEST -> {
                x = -x * BOUNCE_FACTOR;
                z *= FRICTION;
            }
        }
        this.setDeltaMovement(new Vec3(x, y, z));
    }

    protected void vanillaHitBlock(BlockHitResult hitResult) {
        this.lastState = this.level().getBlockState(hitResult.getBlockPos());
        // From Projectile
        BlockState state = this.level().getBlockState(hitResult.getBlockPos());
        state.onProjectileHit(this.level(), state, hitResult, this);
    }

    @Override
    protected double getDefaultGravity() {
        return 0.03;
    }

    @Override
    protected @NotNull ItemStack getDefaultPickupItem() {
        return ItemStack.EMPTY;
    }

    public boolean isGrounded() {
        return isInGround();
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {

    }

    @Override
    public @NotNull AnimatableInstanceCache getAnimatableInstanceCache() {
        return geoCache;
    }
}
