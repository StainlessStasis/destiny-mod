package io.github.stainlessstasis.destinymod.entity;

import io.github.stainlessstasis.destinymod.destiny_combat.ability.collision.CollisionContext;
import io.github.stainlessstasis.destinymod.destiny_combat.ability.collision.DestinyProjectile;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.ThrowableProjectile;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import org.jspecify.annotations.NonNull;

import java.util.Set;
import java.util.UUID;

public class GrenadeEntity extends ThrowableProjectile implements DestinyProjectile {
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

    @Override
    public void tick() {
        super.tick();
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
    protected void defineSynchedData(SynchedEntityData.@NonNull Builder builder) {}
}
