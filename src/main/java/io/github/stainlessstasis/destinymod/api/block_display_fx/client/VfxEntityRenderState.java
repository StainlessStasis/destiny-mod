package io.github.stainlessstasis.destinymod.api.block_display_fx.client;

import net.minecraft.client.renderer.block.BlockModelRenderState;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import org.joml.Quaternionf;
import org.joml.Vector3f;

public class VfxEntityRenderState extends EntityRenderState {
    public final BlockModelRenderState blockModel = new BlockModelRenderState();
    public Vector3f translation = new Vector3f();
    public Vector3f scale = new Vector3f(1f);
    public Quaternionf rotation = new Quaternionf();
    public Vector3f overlayColor = new Vector3f(1f);
    public float[] overlayIntensity = new float[]{0f};
    public int brightnessOverride = -1;
}
