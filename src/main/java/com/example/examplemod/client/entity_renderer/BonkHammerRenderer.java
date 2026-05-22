package com.example.examplemod.client.entity_renderer;

import com.example.examplemod.BonkHammerEntity;
import com.example.examplemod.entity.DestinyModEntities;
import com.geckolib.renderer.GeoEntityRenderer;
import com.geckolib.renderer.base.GeoRenderState;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.world.entity.EntityType;

public class BonkHammerRenderer<R extends EntityRenderState & GeoRenderState> extends GeoEntityRenderer<BonkHammerEntity, R> {
    public BonkHammerRenderer(EntityRendererProvider.Context context, EntityType<BonkHammerEntity> entityType) {
        super(context, entityType);
    }

    public BonkHammerRenderer(EntityRendererProvider.Context context) {
        this(context, DestinyModEntities.HAMMER_OF_SOL.get());
    }
}
