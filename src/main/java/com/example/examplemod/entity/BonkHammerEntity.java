package com.example.examplemod.entity;

import com.example.examplemod.util.collision.CollisionContext;
import com.example.examplemod.util.collision.ProjectileCollisionUtils;
import com.example.examplemod.util.world_interaction.BlockDestructionManager;
import com.geckolib.animatable.GeoEntity;
import com.geckolib.animatable.instance.AnimatableInstanceCache;
import com.geckolib.animatable.manager.AnimatableManager;
import com.geckolib.util.GeckoLibUtil;
import com.mojang.math.Constants;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
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
    public static final float RESTITUTION = 0.420f;
    public static final float FRICTION = 0.55f;
    public static final float STICK_SPEED_THRESHOLD = 0.2f;
    private final Set<UUID> collidedThisTick = new HashSet<>();
    private float visualSpinDegrees = 0f;
    private static final float AIR_SPIN_SPEED = 30f;
    private static final float LIQUID_SPIN_SPEED = 10f;
    private boolean hitCeiling = false;
    /**
     * Ok so this serves no purpose, but there's a VERY rare bug where a hammer can get infinitely stuck falling and colliding inside a block.
     * The thing is, it is nearly impossible to recreate, so I need to be able to hotswap changes to fix it.
     * One solution, if it comes up again, is to just check if it has collided way too many times within x ticks, and then either discard it, or try nudging it out of the block.
     * However, because I can't hotswap the code if I add a new field, I'm leaving this here in case it ever comes up again.
     */
    private int _ignoreThis = 0;

    public BonkHammerEntity(EntityType<? extends AbstractArrow> entityType, Level level) {
        super(entityType, level);
        init();
    }

    public BonkHammerEntity(Level level, LivingEntity owner, ItemStack tridentItem) {
        super(DestinyModEntities.HAMMER_OF_SOL.get(), owner, level, tridentItem, null);
        init();
    }

    public static BonkHammerEntity createDefault(EntityType<? extends AbstractArrow> entityType, Level level) {
        return new BonkHammerEntity(entityType, level);
    }

    private void init() {
        this.setSoundEvent(SoundEvents.IRON_FALL);
    }

    private void updateVisualSpin() {
        if (!this.isInGround()) {
            this.visualSpinDegrees += getVisualSpinSpeed();
            this.visualSpinDegrees %= 360f;
        }
    }

    public float getVisualSpinSpeed() {
        return this.isInLiquid() ? LIQUID_SPIN_SPEED : AIR_SPIN_SPEED;
    }

    public float getVisualSpinDegrees() {
        return getVisualSpinDegrees(0f);
    }

    public float getVisualSpinDegrees(float partialTick) {
        if (this.isGrounded()) return this.visualSpinDegrees;
        return this.visualSpinDegrees + (getVisualSpinSpeed() * partialTick);
    }

    @Override
    public void tick() {
        // Modified version of AbstractArrow tick (replaced collision)
        boolean physicsEnabled = !this.isNoPhysics();

        // Custom collision and stuff
        this.hitCeiling = false;
        if (physicsEnabled && !this.isGrounded()) {
            moveAndCollide();
            updateVisualSpin();
        }

        Vec3 movement = this.getDeltaMovement();
        BlockPos blockPos = this.blockPosition();
        BlockState blockState = this.level().getBlockState(blockPos);

        if (this.shakeTime > 0) {
            --this.shakeTime;
        }

        if (this.isInWaterOrRain()) {
            this.clearFire();
        }

        if (isGrounded() && physicsEnabled) {
            if (this.lastState != blockState && this.shouldFall()) {
                this.startFalling();
            } else {
                this.tickDespawn();
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
//                this.setPos(originalPosition.add(movement));
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

        if (result instanceof EntityHitResult entityResult) {
            collidedThisTick.add(entityResult.getEntity().getUUID());

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

        if (result instanceof BlockHitResult blockResult) {
            if (blockResult.getDirection() == Direction.DOWN) {
                this.hitCeiling = true;
            }
            vanillaHitBlock(blockResult);
            if (this.level() instanceof ServerLevel level) {
                double speed = context.sourceVelocity().length();
                if (speed > STICK_SPEED_THRESHOLD) {
                    BlockDestructionManager.addDamage(level, blockResult.getBlockPos(), 0.3f + (float)Math.pow(speed, 1.5f), this, true, true);
                }
            }
        }

        handleCollision(context);
    }

    public void handleCollision(CollisionContext context) {
        Vec3 position = context.result().getLocation();
        Vec3 normal = context.normal();
        Vec3 newVel = applyBounce(this.getDeltaMovement(), context);
        if (!this.hitCeiling && normal.length() > Constants.EPSILON && newVel.length() < STICK_SPEED_THRESHOLD) {
            vanillaStickInBlock();
        } else {
            this.setPos(position.add(normal.scale(0.005))); // prevent infinite collision loop
            this.setDeltaMovement(newVel);
        }
    }

    /**
     * Applies bounce physics given a collision.
     * Reflects velocity off the collision normal using the spell's coefficient of restitution.
     */
    public Vec3 applyBounce(Vec3 velocity, CollisionContext context) {
        Vec3 normal = context.normal();
        Vec3 relative = velocity.subtract(context.targetVelocity());
        double normalSpeed = relative.dot(normal);
        Vec3 scaledNormal = normal.scale(normalSpeed);

        float mass = /* getEnergy(); */ 1f;
        float targetMass = context.targetMass();
        float impulse = (1f / mass) + (targetMass > 0 ? 1f / targetMass : 0f);

        double j = -(1 + RESTITUTION) * normalSpeed / impulse;
        Vec3 bouncedNormal = normal.scale(j / mass);

        Vec3 tangential = relative.subtract(scaledNormal);
        Vec3 friction = tangential.scale(FRICTION);

        return velocity.add(bouncedNormal).subtract(friction);
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

        this.playSound(this.getHitGroundSoundEvent(), 1.0F, 0.8F / (this.random.nextFloat() * 0.2F + 0.9F));
    }

    /**
     *  All the stuff from AbstractArrow's onBlockHit which make the arrow stick into the block.
     */
    protected void vanillaStickInBlock() {
//        Vec3 movement = this.getDeltaMovement();
//        Vec3 offsetDirection = new Vec3(Math.signum(movement.x), Math.signum(movement.y), Math.signum(movement.z));
//        Vec3 scaledMovement = offsetDirection.scale(0.05F);
//        this.setPos(this.position().subtract(scaledMovement));
        this.setDeltaMovement(Vec3.ZERO);
        this.setInGround(true);
        this.shakeTime = 7;
        this.setCritArrow(false);
        this.setPierceLevel((byte)0);
        this.setSoundEvent(SoundEvents.IRON_BREAK);
        this.resetPiercedEntities();
    }

    @Override
    protected void onHit(@NotNull HitResult result) {}

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

    private boolean isBottomFaceUnsupported() {
        AABB bb = this.getBoundingBox();

        AABB bottomFace = new AABB(
                bb.minX, bb.minY - 0.05, bb.minZ,
                bb.maxX, bb.minY, bb.maxZ
        );
        bottomFace.inflate(0.067, 0, 0.067);
        bottomFace.expandTowards(0, -0.05, 0);
        return this.level().noCollision(bottomFace);
    }

    @Override
    public boolean shouldFall() {
        return !this.isGrounded() || isBottomFaceUnsupported();
    }

    @Override
    public void startFalling() {
        this.setInGround(false);
        this.life = 0;
        Vec3 nudge = new Vec3(
                (this.random.nextDouble() - 0.5) * 0.025,
                -0.01,
                (this.random.nextDouble() - 0.5) * 0.025
        );
        this.setDeltaMovement(nudge);
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {

    }

    @Override
    public @NotNull AnimatableInstanceCache getAnimatableInstanceCache() {
        return geoCache;
    }
}
