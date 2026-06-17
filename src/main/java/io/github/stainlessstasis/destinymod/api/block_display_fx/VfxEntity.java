package io.github.stainlessstasis.destinymod.api.block_display_fx;

import io.github.stainlessstasis.destinymod.api.block_display_fx.animation.VfxAnimation;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import org.jetbrains.annotations.Nullable;

@SuppressWarnings("NullableProblems")
public class VfxEntity extends Entity {
    private int brightnessOverride = -1;

    private @Nullable VfxAnimation currentAnimation;
    private long animationStartTick;
    private int animationDurationTicks;
    private float lastProgress = 1f;

    public VfxEntity(EntityType<? extends Entity> type, Level level) {
        super(type, level);
        this.noPhysics = true;
    }

    public static VfxEntity createDefault(EntityType<? extends Entity> type, Level level) {
        return new VfxEntity(type, level);
    }

    public void playAnimation(VfxAnimation animation) {
        this.currentAnimation = animation;
        this.animationStartTick = this.tickCount;
        this.animationDurationTicks = animation.durationTicks();
    }

    public float getAnimationProgress(float partialTick) {
        if (animationDurationTicks <= 0) return 1f;
        float ticksSince = (float)(this.tickCount - this.animationStartTick);
        float t = Math.clamp(
                Mth.inverseLerp(ticksSince + partialTick, 0f, animationDurationTicks),
                0f, 1f
        );
        this.lastProgress = t;
        return t;
    }

    @Override
    public void tick() {
        super.tick();
        if (currentAnimation != null && tickCount - animationStartTick >= animationDurationTicks) {
            discard();
        }
    }

    public @Nullable VfxAnimation getCurrentAnimation() { return currentAnimation; }
    public int getBrightnessOverride() { return brightnessOverride; }
    public void setBrightnessOverride(int brightness) { this.brightnessOverride = brightness; }

    @Override protected void defineSynchedData(SynchedEntityData.Builder builder) {}
    @Override public boolean hurtServer(ServerLevel level, DamageSource source, float v) { return false; }
    @Override protected void readAdditionalSaveData(ValueInput input) { discard(); }
    @Override protected void addAdditionalSaveData(ValueOutput output) {}
}
