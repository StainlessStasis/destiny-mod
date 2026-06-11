package io.github.stainlessstasis.destinymod.entity;

import io.github.stainlessstasis.destinymod.destiny_combat.ability.cooldown.AbilityCooldownManager;
import io.github.stainlessstasis.destinymod.destiny_combat.registry.Abilities;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class GrenadeEntity extends Projectile {
    public GrenadeEntity(EntityType<? extends Projectile> type, Level level) {
        super(type, level);
    }

    public GrenadeEntity(Level level, LivingEntity owner, ItemStack weaponItem) {
        super(DestinyModEntities.GENERIC_GRENADE.get(), level);
    }

    public static GrenadeEntity createDefault(EntityType<? extends Projectile> entityType, Level level) {
        return new GrenadeEntity(entityType, level);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {}
}
