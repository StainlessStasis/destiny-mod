package io.github.stainlessstasis.destinymod.client.ui;

import io.github.stainlessstasis.destinymod.DestinyMod;
import io.github.stainlessstasis.destinymod.ability.PlayerAbilities;
import io.github.stainlessstasis.destinymod.ability.cooldown.AbilityCooldownManager;
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
    public static final Identifier ABILITY_BORDER = Identifier.fromNamespaceAndPath(DestinyMod.MODID, "textures/gui/sprites/destiny_hud/ability_border.png");
    public static final Identifier THROWING_HAMMER = Identifier.fromNamespaceAndPath(DestinyMod.MODID, "textures/gui/sprites/destiny_hud/throwing_hammer.png");
    public static final Identifier THROWING_HAMMER_CHARGED = Identifier.fromNamespaceAndPath(DestinyMod.MODID, "textures/gui/sprites/destiny_hud/throwing_hammer_charged.png");
    public static final int THROWING_HAMMER_SIZE = 32;

    @SubscribeEvent
    public static void onRenderGuiLayers(RenderGuiLayerEvent.Post event) {
        Minecraft mc = Minecraft.getInstance();
        Player player = mc.player;
        if (player == null || mc.options.hideGui) return;

        int renderX = THROWING_HAMMER_SIZE/2;
        int renderY = mc.getWindow().getGuiScaledHeight() - 4 - THROWING_HAMMER_SIZE;
        var ability = PlayerAbilities.getEquippedMelee(player);

        int borderSize = THROWING_HAMMER_SIZE+2;
        event.getGuiGraphics().blit(
                RenderPipelines.GUI_TEXTURED, ABILITY_BORDER,
                renderX-1, renderY-1, 0f, 0f,
                borderSize, borderSize,
                borderSize, borderSize,
                borderSize, borderSize
        );

        event.getGuiGraphics().blit(
                RenderPipelines.GUI_TEXTURED, THROWING_HAMMER,
                renderX, renderY, 0f, 0f,
                THROWING_HAMMER_SIZE, THROWING_HAMMER_SIZE,
                THROWING_HAMMER_SIZE, THROWING_HAMMER_SIZE,
                THROWING_HAMMER_SIZE, THROWING_HAMMER_SIZE
        );

        if (AbilityCooldownManager.hasCharges(player, ability)) {
            event.getGuiGraphics().blit(
                    RenderPipelines.GUI_TEXTURED, THROWING_HAMMER_CHARGED,
                    renderX, renderY, 0f, 0f,
                    THROWING_HAMMER_SIZE, THROWING_HAMMER_SIZE,
                    THROWING_HAMMER_SIZE, THROWING_HAMMER_SIZE,
                    THROWING_HAMMER_SIZE, THROWING_HAMMER_SIZE
            );
        }

        System.out.println(AbilityCooldownManager.getCooldownPercent(player, ability));
    }
}
