package io.github.stainlessstasis.destinymod.entity;

import io.github.stainlessstasis.destinymod.destiny_combat.ability.Ability;
import io.github.stainlessstasis.destinymod.destiny_combat.ability.DestinyAbility;
import io.github.stainlessstasis.destinymod.destiny_combat.ability.collision.BouncingProjectile;
import io.github.stainlessstasis.destinymod.destiny_combat.ability.collision.CollisionContext;
import io.github.stainlessstasis.destinymod.destiny_combat.ability.grenade.GrenadeBehavior;
import io.github.stainlessstasis.destinymod.destiny_combat.ability.grenade.GrenadeBehaviors;
import io.github.stainlessstasis.destinymod.registry.datapack.RegisteredAbility;
import io.github.stainlessstasis.destinymod.registry.property.AbilityProperties;
import io.github.stainlessstasis.destinymod.registry.property.ability.GrenadePhysicsProperty;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.ThrowableProjectile;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.neoforged.neoforge.registries.DeferredHolder;
import org.jetbrains.annotations.Nullable;
import org.jspecify.annotations.NonNull;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;

public class GrenadeEntity extends ThrowableProjectile implements BouncingProjectile, DestinyAbility {
    private static final EntityDataAccessor<String> GRENADE_BEHAVIOR_ID = SynchedEntityData.defineId(GrenadeEntity.class, EntityDataSerializers.STRING);
    private static final EntityDataAccessor<Float> BOUNCINESS = SynchedEntityData.defineId(GrenadeEntity.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Float> FRICTION = SynchedEntityData.defineId(GrenadeEntity.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Float> GRAVITY = SynchedEntityData.defineId(GrenadeEntity.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Float> SETTLE_SPEED_THRESHOLD = SynchedEntityData.defineId(GrenadeEntity.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Boolean> DETONATE_ON_BLOCK = SynchedEntityData.defineId(GrenadeEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> DETONATE_ON_ENTITY = SynchedEntityData.defineId(GrenadeEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> DETONATE_ON_SETTLE = SynchedEntityData.defineId(GrenadeEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Integer> TICKS_BEFORE_FORCE_DETONATE = SynchedEntityData.defineId(GrenadeEntity.class, EntityDataSerializers.INT);

    protected Ability ability;

    protected GrenadeEntity(EntityType<? extends ThrowableProjectile> type, Level level) {
        super(type, level);
    }

    public GrenadeEntity(Level level, LivingEntity owner, ItemStack __) {
        super(DestinyModEntities.GENERIC_GRENADE.get(), owner.getX(), owner.getEyeY()-0.1, owner.getZ(), level);
        setOwner(owner);
    }

    public static GrenadeEntity createDefault(EntityType<? extends ThrowableProjectile> entityType, Level level) {
        return new GrenadeEntity(entityType, level);
    }

    public void setBehavior(DeferredHolder<GrenadeBehavior, GrenadeBehavior> behavior) {
        this.entityData.set(GRENADE_BEHAVIOR_ID, behavior.getId().toString());
    }

    public Optional<GrenadeBehavior> getBehavior() {
        return GrenadeBehaviors.getOptional(getBehaviorID());
    }

    public Identifier getBehaviorID() {
        return Identifier.parse(this.entityData.get(GRENADE_BEHAVIOR_ID));
    }

    public void setupFromAbility(RegisteredAbility ability) {
        if (level().isClientSide()) return;
        this.ability = ability.get(this);
        this.ability.getProperty(GrenadePhysicsProperty.class).ifPresent(props -> {
            this.entityData.set(BOUNCINESS, props.bounciness());
            this.entityData.set(FRICTION, props.friction());
            this.entityData.set(GRAVITY, props.gravity());
            this.entityData.set(SETTLE_SPEED_THRESHOLD, props.settleSpeedThreshold());
            this.entityData.set(DETONATE_ON_BLOCK, props.detonateOnBlock());
            this.entityData.set(DETONATE_ON_ENTITY, props.detonateOnEntity());
            this.entityData.set(DETONATE_ON_SETTLE, props.detonateOnSettle());
            this.entityData.set(TICKS_BEFORE_FORCE_DETONATE, props.ticksBeforeForceDetonate());
        });
    }

    @Override
    public void tick() {
        this.handleFirstTickBubbleColumn();
        this.applyGravity();
        this.applyInertia();

        moveAndCollide();

        this.updateRotation();
        this.applyEffectsFromBlocks();
        if (!this.hasBeenShot) {
            this.gameEvent(GameEvent.PROJECTILE_SHOOT, this.getOwner());
            this.hasBeenShot = true;
        }

        int forceDetonateTicks = this.entityData.get(TICKS_BEFORE_FORCE_DETONATE);
        if (forceDetonateTicks >= 0 && this.tickCount >= forceDetonateTicks) {
            detonate(null);
            return;
        }

        // leave this last
        this.checkLeftOwner();
        this.baseTick();
        this.leftOwnerChecked = false;
    }

    protected void detonate(@Nullable CollisionContext context) {
        getBehavior().ifPresent(behavior -> behavior.detonate(this, context));
        discard();
    }

    @Override
    public Set<UUID> getCollidedThisTick() {
        return Set.of();
    }

    @Override
    public void handleBlockCollision(CollisionContext context, BlockHitResult result) {
        if (this.entityData.get(DETONATE_ON_BLOCK)) {
            detonate(context);
        }
    }

    @Override
    public void handleEntityCollision(CollisionContext context, EntityHitResult result) {
        if (this.entityData.get(DETONATE_ON_ENTITY)) {
            detonate(context);
        }
    }

    @Override
    public void onSettle(CollisionContext context) {
        if (this.entityData.get(DETONATE_ON_SETTLE)) {
            detonate(context);
        }
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.@NonNull Builder builder) {
        builder.define(GRENADE_BEHAVIOR_ID, "");
        builder.define(BOUNCINESS, 0.0f);
        builder.define(FRICTION, 0.0f);
        builder.define(GRAVITY, 0.0f);
        builder.define(SETTLE_SPEED_THRESHOLD, 0.0f);
        builder.define(DETONATE_ON_BLOCK, false);
        builder.define(DETONATE_ON_ENTITY, false);
        builder.define(DETONATE_ON_SETTLE, false);
        builder.define(TICKS_BEFORE_FORCE_DETONATE, -1);
    }

    @Override
    public float getRestitution() {
        return this.entityData.get(BOUNCINESS);
    }

    @Override
    public float getFriction() {
        return this.entityData.get(FRICTION);
    }

    @Override
    protected double getDefaultGravity() {
        return this.entityData.get(GRAVITY);
    }

    @Override
    public float getSettleSpeedThreshold() {
        return this.entityData.get(SETTLE_SPEED_THRESHOLD);
    }

    @Override
    public @Nullable Ability getDestinyAbility() {
        return ability;
    }
}
