package io.github.stainlessstasis.destinymod.client.entity_renderer;

import com.google.common.reflect.TypeToken;
import com.mojang.blaze3d.vertex.PoseStack;
import io.github.stainlessstasis.destinymod.DestinyMod;
import io.github.stainlessstasis.destinymod.data.DestinyModAttachments;
import io.github.stainlessstasis.destinymod.destiny_classes.debuff.DebuffManager;
import io.github.stainlessstasis.destinymod.entity.DestinyModEntities;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.resources.Identifier;
import net.minecraft.util.context.ContextKey;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.golem.IronGolem;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.event.RenderLivingEvent;
import net.neoforged.neoforge.client.renderstate.RegisterRenderStateModifiersEvent;

@EventBusSubscriber(modid = DestinyMod.MODID, value = Dist.CLIENT)
public class DMEntityRenderers {
    public static final ContextKey<DebuffData> DEBUFF_CONTEXT_KEY = new ContextKey<>(DestinyMod.id("debuff_data"));
    public record DebuffData(boolean isScorched) {}

    @SubscribeEvent
    public static void registerRenderStateModifiers(RegisterRenderStateModifiersEvent event) {
        event.registerEntityModifier(
                new TypeToken<LivingEntityRenderer<LivingEntity, LivingEntityRenderState, ?>>() {},
                (entity, state) -> {
                    boolean isScorched = entity.getData(DestinyModAttachments.IS_SCORCH_ACTIVE);
                    state.setRenderData(DEBUFF_CONTEXT_KEY, new DebuffData(isScorched));
                }
        );
    }

    @SubscribeEvent
    public static void registerEntityRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer(DestinyModEntities.HAMMER_OF_SOL.get(), BonkHammerRenderer::new);
    }

    @SubscribeEvent
    public static void onRenderLivingPre(RenderLivingEvent.Pre<?, LivingEntityRenderState, EntityModel<LivingEntityRenderState>> event) {
        LivingEntityRenderState state = event.getRenderState();
        DebuffData debuffData = state.getRenderData(DEBUFF_CONTEXT_KEY);

        if (debuffData == null) return;
        if (!debuffData.isScorched()) return;

        PoseStack poseStack = event.getPoseStack();
        SubmitNodeCollector submitNodeCollector = event.getSubmitNodeCollector();
        LivingEntityRenderer<?, LivingEntityRenderState, EntityModel<LivingEntityRenderState>> renderer = event.getRenderer();
        EntityModel<LivingEntityRenderState> model = renderer.getModel();

        Identifier texture = DestinyMod.id("textures/entity/hammer_of_sol.png");
        RenderType renderType = RenderTypes.energySwirl(texture, 0f, 0f);

        submitNodeCollector.submitModel(
                model,
                state,
                poseStack,
                renderType,
                0xFFFFFF,
                LivingEntityRenderer.getOverlayCoords(state, 0f),
                0,
                null
        );
    }
}
