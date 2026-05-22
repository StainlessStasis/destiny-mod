package com.example.examplemod.entity;

import com.example.examplemod.collision.CollisionContext;
import com.example.examplemod.collision.ProjectileCollisionUtils;
import com.geckolib.animatable.GeoEntity;
import com.geckolib.animatable.instance.AnimatableInstanceCache;
import com.geckolib.animatable.manager.AnimatableManager;
import com.geckolib.util.GeckoLibUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.arrow.AbstractArrow;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.*;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

public class BonkHammerEntity extends AbstractArrow implements GeoEntity {
    private final AnimatableInstanceCache geoCache = GeckoLibUtil.createInstanceCache(this);
    public static final float BOUNCE_FACTOR = 0.45f;
    public static final float FRICTION = 0.7f;
    public static final float STICK_SPEED_THRESHOLD = 0.15f;
    private final Set<UUID> collidedThisTick = new HashSet<>();

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
        moveAndCollide();

        // Modified version of AbstractArrow tick (replaced collision)
        boolean physicsEnabled = !this.isNoPhysics();
        Vec3 movement = this.getDeltaMovement();
        BlockPos blockPos = this.blockPosition();
        BlockState blockState = this.level().getBlockState(blockPos);

        if (this.shakeTime > 0) {
            --this.shakeTime;
        }

        if (this.isInWaterOrRain()) {
            this.clearFire();
        }

        if (this.isInGround() && physicsEnabled) {
            if (!this.level().isClientSide()) {
                if (this.lastState != blockState && this.shouldFall()) {
                    this.startFalling();
                } else {
                    this.tickDespawn();
                }
            }

            ++this.inGroundTime;
            if (this.isAlive()) {
                this.applyEffectsFromBlocks();
            }

            if (!this.level().isClientSide()) {
                this.setSharedFlagOnFire(this.getRemainingFireTicks() > 0);
            }
        } else {
            this.inGroundTime = 0;
            Vec3 originalPosition = this.position();
            if (this.isInWater()) {
                this.applyInertia(this.getWaterInertia());
                this.addBubbleParticles(originalPosition);
            }

            if (this.isCritArrow()) {
                for(int i = 0; i < 4; ++i) {
                    this.level().addParticle(ParticleTypes.CRIT, originalPosition.x + movement.x * (double)i / (double)4.0F, originalPosition.y + movement.y * (double)i / (double)4.0F, originalPosition.z + movement.z * (double)i / (double)4.0F, -movement.x, -movement.y + 0.2, -movement.z);
                }
            }

            float yRot;
            if (!physicsEnabled) {
                yRot = (float)(Mth.atan2(-movement.x, -movement.z) * (double)180.0F / (double)(float)Math.PI);
            } else {
                yRot = (float)(Mth.atan2(movement.x, movement.z) * (double)180.0F / (double)(float)Math.PI);
            }

            float xRot = (float)(Mth.atan2(movement.y, movement.horizontalDistance()) * (double)180.0F / (double)(float)Math.PI);
            this.setXRot(lerpRotation(this.getXRot(), xRot));
            this.setYRot(lerpRotation(this.getYRot(), yRot));
            this.checkLeftOwner();
            if (physicsEnabled) {
//                BlockHitResult blockHitResult = this.level().clipIncludingBorder(new ClipContext(originalPosition, originalPosition.add(movement), ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, this));
//                this.stepMoveAndHit(blockHitResult);
            } else {
                this.setPos(originalPosition.add(movement));
                this.applyEffectsFromBlocks();
            }

            if (!this.isInWater()) {
                this.applyInertia(0.99F);
            }

            if (physicsEnabled && !this.isInGround()) {
                this.applyGravity();
            }

            // From Projectile
            if (!this.hasBeenShot) {
                this.gameEvent(GameEvent.PROJECTILE_SHOOT, this.getOwner());
                this.hasBeenShot = true;
            }

            this.checkLeftOwner();
            super.tick();
            this.leftOwnerChecked = false;
        }
    }

    public void moveAndCollide() {
        moveAndCollide(this.getDeltaMovement());
    }

    public void moveAndCollide(Vec3 movement) {
        Optional<CollisionContext> collision = ProjectileCollisionUtils.checkCollisions(this, collidedThisTick, movement);
        if (collision.isPresent()) {
            onCollisionResult(collision.get());
        } else {
            this.setPos(this.position().add(movement));
        }
    }

    public void onCollisionResult(CollisionContext context) {
        HitResult result = context.result();

        if (result instanceof EntityHitResult entityHit) {
            collidedThisTick.add(entityHit.getEntity().getUUID());

            // OLD CODE FROM THE PLACE THIS CAME FROM. Here just as a reference in case this functionality is added
            // if the other entity is also a spell, resolve the collision from both sides
//            if (entityHit.getEntity() instanceof Spell<?> other) {
//                if (entityHit.getEntity() instanceof SpellProjectileEntity otherProjectile) {
//                    otherProjectile.collidedThisTick.add(this.getUUID()); // prevent calculating the same collision twice by the other projectile
//                }
//                SpellCollisionResolvers.resolve(this, other, context);
//                return;
//            }
        }

        // collide with non-spell entity or blocks
        handleCollision(context);
    }

    public void handleCollision(CollisionContext context) {
        Vec3 position = context.result().getLocation();
        this.setPos(position.x, position.y, position.z);
        Vec3 newVel = applyBounce(this.getDeltaMovement(), context);
        if (newVel.length() < STICK_SPEED_THRESHOLD) {
            vanillaStickInBlock();
        }
        this.setDeltaMovement(newVel);
    }

    /**
     * Applies bounce physics given a collision.
     * Reflects velocity off the collision normal using the spell's coefficient of restitution.
     */
    public Vec3 applyBounce(Vec3 velocity, CollisionContext context) {
        Vec3 normal = context.normal();
        Vec3 relative = velocity.subtract(context.targetVelocity());
        double dot = relative.dot(normal);

        float mass = /* getEnergy(); */ 1f;
        float targetMass = context.targetMass();
        float impulse = (1f / mass) + (targetMass > 0 ? 1f / targetMass : 0f);

        double j = -(1 + BOUNCE_FACTOR) * dot / impulse;
        return velocity.add(normal.scale(j / mass));
    }

    @Override
    protected void onHit(@NotNull HitResult result) {}

    @Override
    protected void onHitBlock(BlockHitResult hitResult) {
        System.out.println("ON HIT BLOCK");
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
    protected void vanillaStickInBlock() {
        Vec3 movement = this.getDeltaMovement();
        Vec3 offsetDirection = new Vec3(Math.signum(movement.x), Math.signum(movement.y), Math.signum(movement.z));
        Vec3 scaledMovement = offsetDirection.scale(0.05F);
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
