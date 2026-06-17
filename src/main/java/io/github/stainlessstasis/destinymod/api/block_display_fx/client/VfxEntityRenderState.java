package io.github.stainlessstasis.destinymod.api.block_display_fx.client;

import net.minecraft.client.renderer.block.BlockModelRenderState;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import org.joml.Quaternionf;
import org.joml.Vector3f;

public class VfxEntityRenderState extends EntityRenderState {
    public final BlockModelRenderState blockModel = new BlockModelRenderState();
    public BlockState blockState = Blocks.AIR.defaultBlockState();
    public final Vector3f translation = new Vector3f();
    public final Vector3f scale = new Vector3f(1f);
    public final Quaternionf rotation = new Quaternionf();
    public final Vector3f overlayColor = new Vector3f(1f);
    public final float[] overlayIntensity = new float[]{0f};
    public int brightnessOverride = -1;
}
