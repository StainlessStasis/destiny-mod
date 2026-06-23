package io.github.stainlessstasis.destinymod.entity;

import io.github.stainlessstasis.destinymod.destiny_combat.ability.Ability;
import io.github.stainlessstasis.destinymod.registry.datapack.Abilities;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

public class BarricadeEntity extends DestinyAbilityEntity {
    protected BarricadeEntity(EntityType<?> type, Level level, Ability ability) {
        super(type, level, ability);
    }

    public static BarricadeEntity createDefault(EntityType<? extends DestinyAbilityEntity> entityType, Level level) {
        return new BarricadeEntity(entityType, level, Abilities.BARRICADE.get(level));
    }

    public BarricadeEntity(EntityType<?> type, Level level, Vec3 pos, @Nullable LivingEntity owner, Ability ability) {
        super(type, level, pos, owner, ability);
    }

    @Override
    public void tick() {
        super.tick();
        System.out.println("TICK");
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.@NonNull Builder builder) {}
}
