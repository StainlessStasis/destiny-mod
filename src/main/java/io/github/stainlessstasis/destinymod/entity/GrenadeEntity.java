package io.github.stainlessstasis.destinymod.entity;

import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.ThrowableProjectile;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jspecify.annotations.NonNull;

public class GrenadeEntity extends ThrowableProjectile {
    protected GrenadeEntity(EntityType<? extends ThrowableProjectile> type, Level level) {
        super(type, level);
    }

    public GrenadeEntity(Level level, LivingEntity owner, ItemStack weaponItem) {
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

    @Override
    protected void defineSynchedData(SynchedEntityData.@NonNull Builder builder) {}
}
