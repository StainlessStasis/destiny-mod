package io.github.stainlessstasis.destinymod.api.block_display_fx;

import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import org.joml.Quaternionf;
import org.joml.Vector3f;

@SuppressWarnings("NullableProblems")
public class VfxEntity extends Entity {
    private BlockState blockState = Blocks.AIR.defaultBlockState();
    private Vector3f translation = new Vector3f();
    private Vector3f scale = new Vector3f(1, 1, 1);
    private Quaternionf leftRotation = new Quaternionf();
    private Quaternionf rightRotation = new Quaternionf();
    private int color = 0xFFFFFFFF;
    private int brightnessOverride = -1;

    public VfxEntity(EntityType<? extends Entity> type, Level level) {
        super(type, level);
    }

    public static VfxEntity createDefault(EntityType<? extends Entity> type, Level level) {
        return new VfxEntity(type, level);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {}

    @Override
    public boolean hurtServer(ServerLevel serverLevel, DamageSource damageSource, float v) {return false;}

    @Override
    protected void readAdditionalSaveData(ValueInput valueInput) {
        discard();
    }

    @Override
    protected void addAdditionalSaveData(ValueOutput valueOutput) {}
}
