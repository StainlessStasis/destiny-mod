package io.github.stainlessstasis.destinymod.entity.ability;

import io.github.stainlessstasis.destinymod.destiny_combat.ability.Ability;
import io.github.stainlessstasis.destinymod.registry.datapack.Abilities;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

public class BarricadeEntity extends DestinyAbilityEntity {
    public static final float WIDTH = 3.25f;
    public static final float HEIGHT = 2.1f;

    public static BarricadeEntity createDefault(EntityType<? extends DestinyAbilityEntity> entityType, Level level) {
        return new BarricadeEntity(entityType, level, Vec3.ZERO, null, Abilities.BARRICADE.get(level));
    }

    public BarricadeEntity(EntityType<?> type, Level level, Vec3 pos, @Nullable LivingEntity owner, Ability ability) {
        super(type, level, pos, owner, ability);
    }

    @Override
    public void tick() {
        super.tick();
    }

    @Override
    public @NonNull EntityDimensions getDimensions(@NonNull Pose pose) {
        return super.getDimensions(pose);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.@NonNull Builder builder) {}
}
