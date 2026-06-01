package io.github.stainlessstasis.destinymod.compat.LDL;

import dev.lambdaurora.lambdynlights.api.behavior.DynamicLightBehavior;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Range;

// From: https://lambdaurora.dev/projects/lambdynamiclights/docs/v4/java.html#add-custom-dynamic-light-sources-(dynamic-light-behavior)
public class FadeOutDynamicLightBehavior implements DynamicLightBehavior {
    private final double x;
    private final double y;
    private final double z;
    private final BoundingBox box;
    private int remainingTicks;
    private final int maxTicks;
    private int lastLuminance = 15;
    private int luminance = 15;

    public FadeOutDynamicLightBehavior(Vec3 pos, int ticks, int range) {
        this.remainingTicks = ticks;
        this.maxTicks = ticks;

        this.x = pos.x;
        this.y = pos.y;
        this.z = pos.z;

        BlockPos blockPos = new BlockPos((int) x, (int) y, (int) z);
        this.box = new BoundingBox(
                blockPos.getX() - range, blockPos.getY() - range, blockPos.getZ() - range,
                blockPos.getX() + range, blockPos.getY() + range, blockPos.getZ() + range
        );
    }

    @Override
    public @Range(from = 0, to = 15) double lightAtPos(BlockPos pos, double falloffRatio) {
        double dx = pos.getX() - this.x + 0.5;
        double dy = pos.getY() - this.y + 0.5;
        double dz = pos.getZ() - this.z + 0.5;

        double distanceSquared = dx * dx + dy * dy + dz * dz;
        return Math.max(
                this.luminance - Math.sqrt(distanceSquared) * falloffRatio,
                0.0
        );
    }

    @Override
    public @NotNull BoundingBox getBoundingBox() {
        return this.box;
    }

    @Override
    public boolean hasChanged() {
        if (this.luminance != this.lastLuminance) {
            this.lastLuminance = this.luminance;
            return true;
        } else {
            return false;
        }
    }

    public void tick() {
        this.luminance = (int) ((float) remainingTicks / maxTicks * 15.f);
        this.remainingTicks--;
    }

    @Override
    public boolean isRemoved() {
        return this.remainingTicks <= 0;
    }
}
