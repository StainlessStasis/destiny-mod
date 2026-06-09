package io.github.stainlessstasis.destinymod.client.entity_rendering;

import com.google.common.reflect.TypeToken;
import io.github.stainlessstasis.destinymod.DestinyMod;
import io.github.stainlessstasis.destinymod.client.entity_rendering.layer.MeltingPointRenderLayer;
import io.github.stainlessstasis.destinymod.client.entity_rendering.layer.ScorchRenderLayer;
import io.github.stainlessstasis.destinymod.client.entity_rendering.renderer.BonkHammerRenderer;
import io.github.stainlessstasis.destinymod.client.entity_rendering.renderer.DummyEntityRenderer;
import io.github.stainlessstasis.destinymod.data.DestinyModAttachments;
import io.github.stainlessstasis.destinymod.destiny_combat.status_effect.StatusEffectManager;
import io.github.stainlessstasis.destinymod.destiny_combat.status_effect.debuff.MeltingPoint;
import io.github.stainlessstasis.destinymod.destiny_combat.status_effect.debuff.Scorch;
import io.github.stainlessstasis.destinymod.entity.DestinyModEntities;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.util.context.ContextKey;
import net.minecraft.world.entity.LivingEntity;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.renderstate.RegisterRenderStateModifiersEvent;

@EventBusSubscriber(modid = DestinyMod.MODID, value = Dist.CLIENT)
public class EntityRendererEvents {
    public static final ContextKey<DebuffData> DEBUFF_CONTEXT_KEY = new ContextKey<>(DestinyMod.id("debuff_data"));
    public record DebuffData(boolean isScorched, boolean isMelting) {}

    @SubscribeEvent
    public static void registerRenderStateModifiers(RegisterRenderStateModifiersEvent event) {
        event.registerEntityModifier(
                new TypeToken<LivingEntityRenderer<LivingEntity, LivingEntityRenderState, ?>>() {},
                (entity, state) -> {
                    boolean isScorched = StatusEffectManager.isActive(entity, Scorch.class);
                    boolean isMelting = StatusEffectManager.isActive(entity, MeltingPoint.class);
                    state.setRenderData(DEBUFF_CONTEXT_KEY, new DebuffData(isScorched, isMelting));
                }
        );
    }

    @SubscribeEvent
    public static void registerEntityRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer(DestinyModEntities.HAMMER_OF_SOL.get(), BonkHammerRenderer::new);
        event.registerEntityRenderer(DestinyModEntities.SUNSPOT.get(), DummyEntityRenderer::new);
    }

    @SubscribeEvent
    public static void registerLayers(EntityRenderersEvent.AddLayers event) {
        for (var skinType : event.getSkins()) {
            LivingEntityRenderer<?, ?, ?> playerRenderer = event.getPlayerRenderer(skinType);
            if (playerRenderer != null) {
                addAllLayers(playerRenderer);
            }

            LivingEntityRenderer<?, ?, ?> mannequinRenderer = event.getMannequinRenderer(skinType);
            if (mannequinRenderer != null) {
                addAllLayers(mannequinRenderer);
            }
        }

        for (var entityType : event.getEntityTypes()) {
            if (event.getRenderer(entityType) instanceof LivingEntityRenderer<?, ?, ?> renderer) {
                addAllLayers(renderer);
            }
        }
    }

    private static void addAllLayers(LivingEntityRenderer<?, ?, ?> renderer) {
        addScorchLayer(renderer);
        addMeltingPointLayer(renderer);
    }

    @SuppressWarnings("unchecked")
    private static <T extends LivingEntity, S extends LivingEntityRenderState, M extends EntityModel<? super S>> void addScorchLayer(LivingEntityRenderer<?, ?, ?> renderer) {
        var castRenderer = (LivingEntityRenderer<T, S, M>) renderer;
        castRenderer.addLayer(new ScorchRenderLayer<>(castRenderer));
    }

    private static <T extends LivingEntity, S extends LivingEntityRenderState, M extends EntityModel<? super S>> void addMeltingPointLayer(LivingEntityRenderer<?, ?, ?> renderer) {
        var castRenderer = (LivingEntityRenderer<T, S, M>) renderer;
        castRenderer.addLayer(new MeltingPointRenderLayer<>(castRenderer));
    }
}
