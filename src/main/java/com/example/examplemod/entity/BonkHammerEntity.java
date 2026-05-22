package com.example.examplemod.entity;

import com.geckolib.animatable.GeoEntity;
import com.geckolib.animatable.instance.AnimatableInstanceCache;
import com.geckolib.animatable.manager.AnimatableManager;
import com.geckolib.util.GeckoLibUtil;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.arrow.AbstractArrow;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

public class BonkHammerEntity extends AbstractArrow implements GeoEntity {
    private final AnimatableInstanceCache geoCache = GeckoLibUtil.createInstanceCache(this);
    public static final float BOUNCE_FACTOR = 0.45f;
    public static final float FRICTION = 0.7f;
    public static final float STICK_SPEED_THRESHOLD = 0.15f;

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

    @Override
    protected void onHitBlock(BlockHitResult hitResult) {
        vanillaHitBlock(hitResult);

        Direction hitFace = hitResult.getDirection();
        Vec3 motion = this.getDeltaMovement();
        double speed = motion.length();
        if (speed < STICK_SPEED_THRESHOLD) {
            vanillaStickInBlock(hitResult);
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

    /**
     * All the stuff from AbstractArrow's onBlockHit which don't involve the arrow sticking into the block.
     * Does things like alerting the block it hit that it's been hit by a projectile (e.g. target blocks).
     */
    protected void vanillaHitBlock(BlockHitResult hitResult) {
        this.lastState = this.level().getBlockState(hitResult.getBlockPos());

        // From Projectile
        BlockState state = this.level().getBlockState(hitResult.getBlockPos());
        state.onProjectileHit(this.level(), state, hitResult, this);

        ItemStack weaponItem = this.getWeaponItem();
        Level var4 = this.level();
        if (var4 instanceof ServerLevel serverLevel) {
            if (weaponItem != null) {
                this.hitBlockEnchantmentEffects(serverLevel, hitResult, weaponItem);
            }
        }

        this.playSound(this.getHitGroundSoundEvent(), 1.0F, 1.2F / (this.random.nextFloat() * 0.2F + 0.9F));
    }

    /**
     *  All the stuff from AbstractArrow's onBlockHit which make the arrow stick into the block.
     */
    protected void vanillaStickInBlock(BlockHitResult hitResult) {
        Vec3 movement = this.getDeltaMovement();
        Vec3 offsetDirection = new Vec3(Math.signum(movement.x), Math.signum(movement.y), Math.signum(movement.z));
        Vec3 scaledMovement = offsetDirection.scale((double)0.05F);
        this.setPos(this.position().subtract(scaledMovement));
        this.setDeltaMovement(Vec3.ZERO);
        this.setInGround(true);
        this.shakeTime = 7;
        this.setCritArrow(false);
        this.setPierceLevel((byte)0);
        this.setSoundEvent(SoundEvents.ARROW_HIT);
        this.resetPiercedEntities();
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
