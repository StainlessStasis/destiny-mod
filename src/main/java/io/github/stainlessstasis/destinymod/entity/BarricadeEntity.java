package io.github.stainlessstasis.destinymod.entity;

import io.github.stainlessstasis.destinymod.destiny_combat.ability.Ability;
import io.github.stainlessstasis.destinymod.registry.datapack.Abilities;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import org.jspecify.annotations.NonNull;

public class BarricadeEntity extends DestinyAbilityEntity {
    protected BarricadeEntity(EntityType<?> type, Level level, Ability ability) {
        super(type, level, ability);
    }

    public static BarricadeEntity createDefault(EntityType<? extends DestinyAbilityEntity> entityType, Level level) {
        return new BarricadeEntity(entityType, level, Abilities.BARRICADE.get(level));
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.@NonNull Builder builder) {}
}
