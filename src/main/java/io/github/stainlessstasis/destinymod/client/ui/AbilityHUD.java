package io.github.stainlessstasis.destinymod.client.ui;

import io.github.stainlessstasis.destinymod.DestinyMod;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Player;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RenderGuiLayerEvent;

@EventBusSubscriber(modid = DestinyMod.MODID, value = Dist.CLIENT)
public class AbilityHUD {
    public static final Identifier THROWING_HAMMER_CHARGED = Identifier.fromNamespaceAndPath(DestinyMod.MODID, "textures/gui/sprites/destiny_hud/throwing_hammer_charged.png");

    @SubscribeEvent
    public static void onRenderGuiLayers(RenderGuiLayerEvent.Post event) {
        Minecraft mc = Minecraft.getInstance();
        Player player = mc.player;
        if (player == null || mc.options.hideGui) return;

        int x = mc.getWindow().getGuiScaledWidth()/2;
        int y = mc.getWindow().getGuiScaledHeight()/2;
        int renderX = x-16;
        int renderY = y-16;

        event.getGuiGraphics().blit(
                RenderPipelines.GUI_TEXTURED,
                THROWING_HAMMER_CHARGED,
                renderX, renderY,
                0f, 0f,
                32, 32,
                32, 32,
                32, 32
        );
    }
}
