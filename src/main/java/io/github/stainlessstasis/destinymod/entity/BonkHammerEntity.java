package io.github.stainlessstasis.destinymod.entity;

import io.github.stainlessstasis.destinymod.destiny_combat.ability.registry.Abilities;
import io.github.stainlessstasis.destinymod.destiny_combat.ability.Ability;
import io.github.stainlessstasis.destinymod.destiny_combat.ability.DestinyAbility;
import io.github.stainlessstasis.destinymod.destiny_combat.ability.collision.CollisionContext;
import io.github.stainlessstasis.destinymod.destiny_combat.ability.collision.ProjectileCollisionUtils;
import io.github.stainlessstasis.destinymod.destiny_combat.ability.cooldown.AbilityCooldownManager;
import com.geckolib.animatable.GeoEntity;
import com.geckolib.animatable.instance.AnimatableInstanceCache;
import com.geckolib.animatable.manager.AnimatableManager;
import com.geckolib.util.GeckoLibUtil;
import com.google.common.collect.Lists;
import com.mojang.math.Constants;
import io.github.stainlessstasis.destinymod.destiny_combat.ability.registry.Aspects;
import io.github.stainlessstasis.destinymod.destiny_combat.ability.registry.property.AbilityProperties;
import io.github.stainlessstasis.destinymod.destiny_combat.ability.registry.property.AbilityProperty;
import io.github.stainlessstasis.destinymod.destiny_combat.ability.registry.property.HeatseekerProperty;
import io.github.stainlessstasis.destinymod.destiny_combat.damage.DestinyDamageBuilder;
import io.github.stainlessstasis.destinymod.destiny_combat.damage.DMDamageTypes;
import io.github.stainlessstasis.destinymod.destiny_combat.status_effect.StatusEffectManager;
import io.github.stainlessstasis.destinymod.destiny_classes.player_equipped.PlayerSubclassData;
import io.github.stainlessstasis.destinymod.destiny_combat.status_effect.buff.SolInvictus;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.arrow.AbstractArrow;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.phys.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jspecify.annotations.NonNull;

import java.util.*;

public class BonkHammerEntity extends AbstractArrow implements GeoEntity, DestinyAbility {
    private static final EntityDataAccessor<Boolean> HAS_MELTING_POINT = SynchedEntityData.defineId(BonkHammerEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> HAS_HEATSEEKER = SynchedEntityData.defineId(BonkHammerEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Float> HOMING_STRENGTH = SynchedEntityData.defineId(BonkHammerEntity.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Integer> BONUS_SCORCH = SynchedEntityData.defineId(BonkHammerEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Boolean> HAS_ANVIL_DROP = SynchedEntityData.defineId(BonkHammerEntity.class, EntityDataSerializers.BOOLEAN);
    public static final float AIR_SPIN_SPEED = 30f;
    public static final float LIQUID_SPIN_SPEED = 10f;
    public static final float RESTITUTION = 0.420f;
    public static final float FRICTION = 0.55f;
    public static final float STICK_SPEED_THRESHOLD = 0.2f;

    private final AnimatableInstanceCache geoCache = GeckoLibUtil.createInstanceCache(this);
    private final Ability ability;
    private final Set<UUID> collidedThisTick = new HashSet<>();
    private float visualSpinDegrees = 0f;
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
        ability = Abilities.THROWING_HAMMER.get(level());
        init();
    }

    public BonkHammerEntity(Level level, LivingEntity owner, ItemStack weaponItem) {
        super(DestinyModEntities.HAMMER_OF_SOL.get(), owner, level, weaponItem, null);
        ability = Abilities.THROWING_HAMMER.get(level());
        init();
    }

    public static BonkHammerEntity createDefault(EntityType<? extends AbstractArrow> entityType, Level level) {
        return new BonkHammerEntity(entityType, level);
    }

    private void init() {
        this.setSoundEvent(SoundEvents.IRON_FALL);
    }

    private void initAspects(Player player) {
        setHasMeltingPoint(PlayerSubclassData.isAspectEquipped(player, Aspects.MELTING_POINT) && StatusEffectManager.isActive(player, SolInvictus.class));
        setHasHeatseeker(PlayerSubclassData.isAspectEquipped(player, Aspects.HEATSEEKER));
        PlayerSubclassData.getEquippedProperty(player, HeatseekerProperty.class).ifPresent(heatseeker -> {
            setHomingStrength(heatseeker.homingStrength());
            setBonusScorch(heatseeker.bonusScorch());
        });
        setHasAnvilDrop(PlayerSubclassData.isAspectEquipped(player, Aspects.ANVIL_DROP));
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

            if (getOwner() instanceof Player player) {
                initAspects(player);
            }
        }

        // Custom collision and stuff
        this.hitCeiling = false;
        if (physicsEnabled && !this.isGrounded()) {
            applyHoming();
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
                if (this.isInWater() && this.level().isClientSide()) {
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

//        if (this.level() instanceof ServerLevel level) {
//            double speed = context.sourceVelocity().length();
//            if (speed > STICK_SPEED_THRESHOLD) {
//                BlockDestructionManager.addDamage(level, result.getBlockPos(), 0.3f + (float)Math.pow(speed, 1.5f), this, true, true);
//            }
//        }
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
     * Reflects velocity off the collision normal using the hammer's coefficient of restitution.
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

        if (entity instanceof LivingEntity mob) {
            Level level = this.level();
            if (level instanceof ServerLevel serverLevel) {
                DestinyDamageBuilder builder = DestinyDamageBuilder.create(DMDamageTypes.MELEE_ABILITY, mob)
                        .directSource(this)
                        .attacker(currentOwner != null ? currentOwner : this)
                        .element(ability.element())
                        .damage(ability.damage())
                        .invulnerabilityTicks(0)
                        .knockback(true);
                DamageSource damageSource = builder.buildDamageSource();
                builder.execute();

                if (hasMeltingPoint()) {
                    StatusEffectManager.applyMeltingPoint(mob);
                }

                EnchantmentHelper.doPostAttackEffectsWithItemSource(serverLevel, mob, damageSource, this.getWeaponItem());
                LivingEntity owner = this.getOwner() instanceof LivingEntity ? (LivingEntity) this.getOwner() : null;
                StatusEffectManager.applyScorch(mob, owner, ability.scorch());
            }

            this.doPostHurtEffects(mob);

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

    protected void applyHoming() {
        if (!this.hasHeatseeker()) return;

        LivingEntity target = findHomingTarget();
        if (target == null) return;

        Vec3 targetPos = target.position().add(0, target.getBbHeight() * 0.5, 0);
        Vec3 currentPos = this.position();
        Vec3 toTarget = targetPos.subtract(currentPos).normalize();

        Vec3 velocity = this.getDeltaMovement();
        double speed = velocity.length();
        Vec3 newVelocity = velocity.normalize().lerp(toTarget, getHomingStrength()).normalize().scale(speed);
        this.setDeltaMovement(newVelocity);

        float yRot = (float) (Mth.atan2(newVelocity.x, newVelocity.z) * (180 / Math.PI));
        float xRot = (float) (Mth.atan2(newVelocity.y, newVelocity.horizontalDistance()) * (180 / Math.PI));
        this.setYRot(lerpRotation(this.getYRot(), yRot));
        this.setXRot(lerpRotation(this.getXRot(), xRot));
    }

    protected @Nullable LivingEntity findHomingTarget() {
        Vec3 velocity = this.getDeltaMovement();
        if (velocity.lengthSqr() <= Constants.EPSILON) return null;

        AABB searchArea = this.getBoundingBox().inflate(12.0);
        List<LivingEntity> targets = this.level().getEntitiesOfClass(LivingEntity.class, searchArea, entity -> {
            if (entity == this.getOwner() || !entity.isAlive()) return false;
            if (this.collidedThisTick.contains(entity.getUUID())) return false;

            boolean isOwned = false;
            if (this.getOwner() instanceof Entity _owner) {
                if (entity instanceof TraceableEntity traceable && traceable.getOwner() == _owner) {
                    isOwned = true;
                }
                if (entity instanceof OwnableEntity ownable && ownable.getOwner() == _owner) {
                    isOwned = true;
                }
            }
            return !isOwned;
        });

        LivingEntity bestTarget = null;
        double bestScore = -1;
        for (LivingEntity target : targets) {
            Vec3 toTarget = target.position().add(0, target.getBbHeight() * 0.5, 0).subtract(this.position());
            double distance = toTarget.length();
            if (distance <= Constants.EPSILON) continue;

            Vec3 forwardNormal = velocity.normalize();
            Vec3 toTargetNormal = toTarget.normalize();
            double dotProduct = forwardNormal.dot(toTargetNormal);
            // 90 degree cone
            if (dotProduct < 0.707) continue;

            // check line of sight
            BlockHitResult raycast = this.level().clip(new ClipContext(
                    this.position(),
                    target.position().add(0, target.getBbHeight() * 0.5, 0),
                    ClipContext.Block.COLLIDER,
                    ClipContext.Fluid.NONE,
                    this
            ));
            if (raycast.getType() == HitResult.Type.BLOCK) continue;

            // weigh in favor of tighter angles so hammer doesnt prioritize targets that are out of the way
            double angleWeight = Math.pow(dotProduct, 3);
            double score = angleWeight / (1 + (distance * 0.1));
            if (score > bestScore) {
                bestScore = score;
                bestTarget = target;
            }
        }

        return bestTarget;
    }

    protected void tryCollectHammer() {
        if (this.level().isClientSide()) return;
        if (!(this.getOwner() instanceof Player player)) return;
        if (!canCollectHammer()) return;
        if (!(this.getBoundingBox().inflate(0.5f).intersects(player.getBoundingBox()))) return;

        float pitch = 0.2F / (this.random.nextFloat() * 0.2F + 0.9F);
        this.playSound(SoundEvents.ITEM_PICKUP, 0.3F, pitch);

        AbilityCooldownManager.reduceCooldownPercent(player, PlayerSubclassData.getRegisteredMelee(player), 0.5f);
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
    protected void defineSynchedData(SynchedEntityData.@NonNull Builder entityData) {
        super.defineSynchedData(entityData);
        entityData.define(HAS_MELTING_POINT, false);
        entityData.define(HAS_HEATSEEKER, false);
        entityData.define(HOMING_STRENGTH, 0.075f);
        entityData.define(BONUS_SCORCH, 20);
        entityData.define(HAS_ANVIL_DROP, false);
    }

    public boolean hasMeltingPoint() {
        return this.entityData.get(HAS_MELTING_POINT);
    }

    public void setHasMeltingPoint(boolean meltingPoint) {
        this.entityData.set(HAS_MELTING_POINT, meltingPoint);
    }

    public boolean hasHeatseeker() {
        return this.entityData.get(HAS_HEATSEEKER);
    }

    public void setHasHeatseeker(boolean heatseeker) {
        this.entityData.set(HAS_HEATSEEKER, heatseeker);
    }

    public float getHomingStrength() {
        return this.entityData.get(HOMING_STRENGTH);
    }

    public void setHomingStrength(float homingStrength) {
        this.entityData.set(HOMING_STRENGTH, homingStrength);
    }

    public int getBonusScorch() {
        return this.entityData.get(BONUS_SCORCH);
    }

    public void setBonusScorch(int bonusScorch) {
        this.entityData.set(BONUS_SCORCH, bonusScorch);
    }

    public boolean hasAnvilDrop() {
        return this.entityData.get(HAS_ANVIL_DROP);
    }

    public void setHasAnvilDrop(boolean anvilDrop) {
        this.entityData.set(HAS_ANVIL_DROP, anvilDrop);
    }

    @Override
    protected void readAdditionalSaveData(@NonNull ValueInput input) {
        super.readAdditionalSaveData(input);
        discard();
    }

    @Override
    public void registerControllers(AnimatableManager.@NonNull ControllerRegistrar controllers) {

    }

    @Override
    public @NotNull AnimatableInstanceCache getAnimatableInstanceCache() {
        return geoCache;
    }

    @Override
    public @NotNull Ability getDestinyAbility() {
        return ability;
    }
}
