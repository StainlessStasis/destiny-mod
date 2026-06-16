package io.github.stainlessstasis.destinymod.api.block_display_fx.client;

import io.github.stainlessstasis.destinymod.api.block_display_fx.VfxEntity;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;

public class VfxEntityRenderer extends EntityRenderer<VfxEntity, VfxEntityRenderState> {
    public VfxEntityRenderer(EntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    public VfxEntityRenderState createRenderState() {
        return null;
    }
}
