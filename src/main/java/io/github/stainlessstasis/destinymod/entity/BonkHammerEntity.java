package io.github.stainlessstasis.destinymod.entity;

import io.github.stainlessstasis.destinymod.ability.AbilityType;
import io.github.stainlessstasis.destinymod.ability.PlayerAbilities;
import io.github.stainlessstasis.destinymod.ability.collision.CollisionContext;
import io.github.stainlessstasis.destinymod.ability.collision.ProjectileCollisionUtils;
import io.github.stainlessstasis.destinymod.ability.cooldown.AbilityCooldownManager;
import io.github.stainlessstasis.destinymod.ability.world_interaction.BlockDestructionManager;
import com.geckolib.animatable.GeoEntity;
import com.geckolib.animatable.instance.AnimatableInstanceCache;
import com.geckolib.animatable.manager.AnimatableManager;
import com.geckolib.util.GeckoLibUtil;
import com.google.common.collect.Lists;
import com.mojang.math.Constants;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.protocol.game.ClientboundGameEventPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.arrow.AbstractArrow;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.*;
import org.jetbrains.annotations.NotNull;

import java.util.*;

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
    private boolean hasEverCollided = false;
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

        if (this.firstTick && this.level() instanceof ServerLevel level) {
            level.playSound(null, this.getX(), this.getY(), this.getZ(), SoundEvents.TRIDENT_THROW, SoundSource.AMBIENT, 0.8f, 0.7f);
            level.playSound(null, this.getX(), this.getY(), this.getZ(), SoundEvents.BLAZE_SHOOT, SoundSource.AMBIENT, 0.5f, 1.5f);
        }

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
            if (!this.isInLiquid()) {
                this.applyInertia(0.99F);
            } else {
                this.applyInertia(this.getWaterInertia());
                if (this.isInWater()) {
                    this.addBubbleParticles(originalPosition);
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

        tryCollectHammer();
        this.firstTick = false;
    }

    @Override
    protected void tickDespawn() {
        ++this.life;
        if (this.life >= 200) {
            this.discard();
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
        this.hasEverCollided = true;
        handleBlockCollision(context);
        handleEntityCollision(context);
        handleCollision(context);
    }

    protected void handleBlockCollision(CollisionContext context) {
        if (!(context.result() instanceof BlockHitResult result)) return;

        if (result.getDirection() == Direction.DOWN) {
            this.hitCeiling = true;
        }

        vanillaHitBlock(result);

        if (this.level() instanceof ServerLevel level) {
            double speed = context.sourceVelocity().length();
            if (speed > STICK_SPEED_THRESHOLD) {
                BlockDestructionManager.addDamage(level, result.getBlockPos(), 0.3f + (float)Math.pow(speed, 1.5f), this, true, true);
            }
        }
    }

    protected void handleEntityCollision(CollisionContext context) {
        if (!(context.result() instanceof EntityHitResult result)) return;

        collidedThisTick.add(result.getEntity().getUUID());
        vanillaHitEntity(result);
    }

    protected void handleCollision(CollisionContext context) {
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
    protected Vec3 applyBounce(Vec3 velocity, CollisionContext context) {
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

        this.playHitSound(true);
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

    /**
     *  All the stuff from AbstractArrow's onEntityHit, but just the important parts (damage, knockback).
     *  So yeah like half of it is gone
     */
    protected void vanillaHitEntity(EntityHitResult hitResult) {
        Entity entity = hitResult.getEntity();
        Entity currentOwner = this.getOwner();
        DamageSource damageSource = this.damageSources().arrow(this, currentOwner != null ? currentOwner : this);
        double damage = this.baseDamage;

        if (this.getPierceLevel() > 0) {
            if (this.piercingIgnoreEntityIds == null) {
                this.piercingIgnoreEntityIds = new IntOpenHashSet(5);
            }

            if (this.piercedAndKilledEntities == null) {
                this.piercedAndKilledEntities = Lists.newArrayListWithCapacity(5);
            }

            if (this.piercingIgnoreEntityIds.size() >= this.getPierceLevel() + 1) {
                this.discard();
                return;
            }

            this.piercingIgnoreEntityIds.add(entity.getId());
        }

        if (entity.is(EntityType.ENDERMAN)) {
            return;
        }

        if (currentOwner instanceof LivingEntity livingOwner) {
            livingOwner.setLastHurtMob(entity);
        }

        int remainingFireTicks = entity.getRemainingFireTicks();
        if (this.isOnFire()) {
            entity.igniteForSeconds(5.0F);
        }

        // It's YOUR code Mojang, why is it deprecated???
        if (!entity.hurtOrSimulate(damageSource, (float)damage)) {
            entity.setRemainingFireTicks(remainingFireTicks);
            return;
        }

        if (entity instanceof LivingEntity mob) {
            this.doKnockback(mob, damageSource);
            Level level = this.level();
            if (level instanceof ServerLevel serverLevel) {
                EnchantmentHelper.doPostAttackEffectsWithItemSource(serverLevel, mob, damageSource, this.getWeaponItem());
            }

            this.doPostHurtEffects(mob);
            if (mob instanceof Player && currentOwner instanceof ServerPlayer ownerPlayer) {
                if (!this.isSilent() && mob != ownerPlayer) {
                    ownerPlayer.connection.send(new ClientboundGameEventPacket(ClientboundGameEventPacket.PLAY_ARROW_HIT_SOUND, 0.0F));
                }
            }

            if (!entity.isAlive() && this.piercedAndKilledEntities != null) {
                this.piercedAndKilledEntities.add(mob);
            }

            // TODO: custom statistics?
//            if (!this.level().isClientSide() && currentOwner instanceof ServerPlayer player) {
//                if (this.piercedAndKilledEntities != null) {
//                    CriteriaTriggers.KILLED_BY_ARROW.trigger(player, this.piercedAndKilledEntities, this.firedFromWeapon);
//                } else if (!entity.isAlive()) {
//                    CriteriaTriggers.KILLED_BY_ARROW.trigger(player, List.of(entity), this.firedFromWeapon);
//                }
//            }
        }

        this.playHitSound(false);
    }

    protected void tryCollectHammer() {
        if (this.level().isClientSide()) return;
        if (!(this.getOwner() instanceof Player player)) return;
        if (!canCollectHammer()) return;
        if (!(this.getBoundingBox().inflate(0.5f).intersects(player.getBoundingBox()))) return;

        float pitch = 0.2F / (this.random.nextFloat() * 0.2F + 0.9F);
        this.playSound(SoundEvents.ITEM_PICKUP, 0.3F, pitch);

        AbilityCooldownManager.reduceCooldownPercent(player, PlayerAbilities.getEquippedMelee(player), 0.5f);

        this.discard();
    }

    protected boolean canCollectHammer() {
        if (this.firstTick) return false;
        return this.tickCount > 15 || (this.tickCount > 5 && this.hasEverCollided) || !this.collidedThisTick.isEmpty();
    }

    protected void playHitSound(boolean hitGround) {
        var sound = hitGround ? this.getHitGroundSoundEvent() : this.soundEvent;
        float pitch = 0.8F / (this.random.nextFloat() * 0.2F + 0.9F);
        this.playSound(sound, 1.0F, pitch);
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
