package io.github.stainlessstasis.destinymod.entity;

import io.github.stainlessstasis.destinymod.destiny_combat.ability.collision.CollisionContext;
import io.github.stainlessstasis.destinymod.destiny_combat.ability.collision.DestinyProjectile;
import io.github.stainlessstasis.destinymod.destiny_combat.ability.grenade.GrenadeBehavior;
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
import org.jspecify.annotations.NonNull;

import java.util.Set;
import java.util.UUID;

public class GrenadeEntity extends ThrowableProjectile implements DestinyProjectile {
    private static final EntityDataAccessor<String> GRENADE_BEHAVIOR_ID = SynchedEntityData.defineId(GrenadeEntity.class, EntityDataSerializers.STRING);

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

    public Identifier getBehaviorID() {
        return Identifier.parse(this.entityData.get(GRENADE_BEHAVIOR_ID));
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

        this.checkLeftOwner();
        this.baseTick();
        this.leftOwnerChecked = false;
    }

    protected void thing() {
        // create new grenade entity here
        discard();
    }

    @Override
    public Set<UUID> getCollidedThisTick() {
        return Set.of();
    }

    @Override
    public void handleBlockCollision(CollisionContext context, BlockHitResult result) {
        thing();
    }

    @Override
    public void handleEntityCollision(CollisionContext context, EntityHitResult result) {
        thing();
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.@NonNull Builder builder) {
        builder.define(GRENADE_BEHAVIOR_ID, "");
    }
}
