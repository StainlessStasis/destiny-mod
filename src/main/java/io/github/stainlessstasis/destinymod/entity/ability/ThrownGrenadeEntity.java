package io.github.stainlessstasis.destinymod.entity.ability;

import com.geckolib.animatable.GeoEntity;
import com.geckolib.animatable.instance.AnimatableInstanceCache;
import com.geckolib.animatable.manager.AnimatableManager;
import com.geckolib.util.GeckoLibUtil;
import io.github.stainlessstasis.destinymod.destiny_combat.ability.Ability;
import io.github.stainlessstasis.destinymod.destiny_combat.ability.DestinyAbility;
import io.github.stainlessstasis.destinymod.entity.projectile.BouncingProjectile;
import io.github.stainlessstasis.destinymod.entity.collision.CollisionContext;
import io.github.stainlessstasis.destinymod.destiny_combat.ability.grenade.GrenadeBehavior;
import io.github.stainlessstasis.destinymod.destiny_combat.ability.grenade.GrenadeBehaviors;
import io.github.stainlessstasis.destinymod.entity.DestinyModEntities;
import io.github.stainlessstasis.destinymod.registry.datapack.Abilities;
import io.github.stainlessstasis.destinymod.registry.datapack.RegisteredAbility;
import io.github.stainlessstasis.destinymod.registry.property.ability.GrenadePhysicsProperty;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
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

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

public class ThrownGrenadeEntity extends ThrowableProjectile implements GeoEntity, BouncingProjectile, DestinyAbility {
    private static final EntityDataAccessor<String> GRENADE_BEHAVIOR_ID = SynchedEntityData.defineId(ThrownGrenadeEntity.class, EntityDataSerializers.STRING);
    private static final EntityDataAccessor<String> ABILITY_ID = SynchedEntityData.defineId(ThrownGrenadeEntity.class, EntityDataSerializers.STRING);

    private final AnimatableInstanceCache geoCache = GeckoLibUtil.createInstanceCache(this);
    private final Set<UUID> collidedThisTick = new HashSet<>();
    protected Ability ability;

    protected ThrownGrenadeEntity(EntityType<? extends ThrowableProjectile> type, Level level) {
        super(type, level);
    }

    public ThrownGrenadeEntity(Level level, LivingEntity owner, ItemStack __) {
        super(DestinyModEntities.THROWN_GRENADE.get(), owner.getX(), owner.getEyeY()-0.1, owner.getZ(), level);
        setOwner(owner);
    }

    public static ThrownGrenadeEntity createDefault(EntityType<? extends ThrowableProjectile> entityType, Level level) {
        return new ThrownGrenadeEntity(entityType, level);
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
        this.entityData.set(ABILITY_ID, ability.getID().toString());
    }

    public Optional<GrenadePhysicsProperty> getPhysics() {
        Ability ability = getDestinyAbility();
        return ability != null ? ability.getProperty(GrenadePhysicsProperty.class) : Optional.empty();
    }

    @Override
    public void tick() {
        if (this.firstTick && level() instanceof ServerLevel level) {
            level.playSound(null, this.getX(), this.getY(), this.getZ(), SoundEvents.PLAYER_ATTACK_SWEEP, SoundSource.AMBIENT, 0.7f, 1.4f);
        }

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

        int forceDetonateTicks = getForceDetonateTicks();
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
        return collidedThisTick;
    }

    @Override
    public void handleBlockCollision(CollisionContext context, BlockHitResult result) {
        if (getDetonateOnBlock()) {
            detonate(context);
        }
    }

    @Override
    public void handleEntityCollision(CollisionContext context, EntityHitResult result) {
        collidedThisTick.add(result.getEntity().getUUID());
        if (getDetonateOnEntity()) {
            detonate(context);
        }
    }

    @Override
    public void onSettle(CollisionContext context) {
        if (getDetonateOnSettle()) {
            detonate(context);
        }
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.@NonNull Builder builder) {
        builder.define(GRENADE_BEHAVIOR_ID, "");
        builder.define(ABILITY_ID, "");
    }

    @Override
    public float getRestitution() {
        return getPhysics().map(GrenadePhysicsProperty::bounciness).orElse(0.5f);
    }

    @Override
    public float getFriction() {
        return getPhysics().map(GrenadePhysicsProperty::friction).orElse(0.5f);
    }

    public boolean getDetonateOnSettle() {
        return getPhysics().map(GrenadePhysicsProperty::detonateOnSettle).orElse(false);
    }

    public boolean getDetonateOnBlock() {
        return getPhysics().map(GrenadePhysicsProperty::detonateOnBlock).orElse(false);
    }

    public boolean getDetonateOnEntity() {
        return getPhysics().map(GrenadePhysicsProperty::detonateOnEntity).orElse(false);
    }

    public int getForceDetonateTicks() {
        return getPhysics().map(GrenadePhysicsProperty::ticksBeforeForceDetonate).orElse(-1);
    }

    @Override
    protected double getDefaultGravity() {
        return getPhysics().map(prop -> (double) prop.gravity()).orElse(super.getDefaultGravity());
    }

    @Override
    public float getSettleSpeedThreshold() {
        return getPhysics().map(GrenadePhysicsProperty::settleSpeedThreshold).orElse(0.5f);
    }

    @Override
    public @Nullable Ability getDestinyAbility() {
        if (this.ability == null) {
            String idString = this.entityData.get(ABILITY_ID);
            if (!idString.isEmpty()) {
                Identifier id = Identifier.parse(idString);
                this.ability = Abilities.get(id).get(level());
            }
        }
        return this.ability;
    }

    @Override
    public void registerControllers(AnimatableManager.@NonNull ControllerRegistrar controllers) {}

    @Override
    public @NonNull AnimatableInstanceCache getAnimatableInstanceCache() {
        return geoCache;
    }
}
