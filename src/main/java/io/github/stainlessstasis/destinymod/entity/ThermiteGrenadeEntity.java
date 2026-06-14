package io.github.stainlessstasis.destinymod.entity;

import io.github.stainlessstasis.destinymod.registry.datapack.Abilities;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

public class ThermiteGrenadeEntity extends AbstractAbilityEntity {
    private ThermiteGrenadeEntity(EntityType<? extends AbstractAbilityEntity> type, Level level) {
        super(type, level, Abilities.THERMITE_GRENADE.get(level));
    }

    public static ThermiteGrenadeEntity createDefault(EntityType<? extends AbstractAbilityEntity> entityType, Level level) {
        return new ThermiteGrenadeEntity(entityType, level);
    }

    public ThermiteGrenadeEntity(EntityType<? extends AbstractAbilityEntity> type, Level level, Vec3 pos, @Nullable LivingEntity owner) {
        this(type, level);
        setPos(pos);
        setOwner(owner);
    }

    @Override
    public void tick() {
        super.tick();
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.@NonNull Builder builder) {}
}
