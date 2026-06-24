package io.github.stainlessstasis.destinymod.entity.ability;

import io.github.stainlessstasis.destinymod.destiny_combat.ability.Ability;
import io.github.stainlessstasis.destinymod.registry.datapack.Abilities;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

public class BarricadeEntity extends DestinyAbilityEntity {
    public static final float WIDTH = 3f;
    public static final float HALF_WIDTH = WIDTH/2f;
    public static final float HEIGHT = 2.2f;
    public static final float DEPTH = 0.25f;
    public static final float HALF_DEPTH = DEPTH/2f;

    public static BarricadeEntity createDefault(EntityType<? extends DestinyAbilityEntity> entityType, Level level) {
        return new BarricadeEntity(entityType, level, Vec3.ZERO, null, Abilities.BARRICADE.get(level));
    }

    public BarricadeEntity(EntityType<?> type, Level level, Vec3 pos, @Nullable LivingEntity owner, Ability ability) {
        super(type, level, pos, owner, ability);
        if (owner != null) {
            setYRot(owner.getYRot());
        }
        refreshDimensions();
        makeBoundingBox(position());
    }

    @Override
    public void tick() {
        super.tick();
    }

    @Override
    protected @NonNull AABB makeBoundingBox(@NonNull Vec3 position) {
        double x = this.getX();
        double y = this.getY();
        double z = this.getZ();
        return new AABB(
                x - HALF_WIDTH, y, z - HALF_DEPTH,
                x + HALF_WIDTH, y + HEIGHT, z + HALF_DEPTH
        );
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.@NonNull Builder builder) {}
}
