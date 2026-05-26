package io.github.stainlessstasis.destinymod.client.ui;

import io.github.stainlessstasis.destinymod.DestinyMod;
import io.github.stainlessstasis.destinymod.ability.PlayerAbilities;
import io.github.stainlessstasis.destinymod.ability.cooldown.AbilityCooldownManager;
import io.github.stainlessstasis.destinymod.ability.cooldown.AbilityCooldowns;
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
        int renderY = mc.getWindow().getGuiScaledHeight() - (int)(THROWING_HAMMER_SIZE * 1.5f);
        var ability = PlayerAbilities.getEquippedMelee(player);
        var graphics = event.getGuiGraphics();

        // border
        int borderSize = THROWING_HAMMER_SIZE+2;
        graphics.blit(
                RenderPipelines.GUI_TEXTURED, ABILITY_BORDER,
                renderX-1, renderY-1, 0f, 0f,
                borderSize, borderSize,
                borderSize, borderSize,
                borderSize, borderSize
        );

        // hammer
        graphics.blit(
                RenderPipelines.GUI_TEXTURED, THROWING_HAMMER,
                renderX, renderY, 0f, 0f,
                THROWING_HAMMER_SIZE, THROWING_HAMMER_SIZE,
                THROWING_HAMMER_SIZE, THROWING_HAMMER_SIZE,
                THROWING_HAMMER_SIZE, THROWING_HAMMER_SIZE
        );

        // charge background and amounts
        int charges = AbilityCooldownManager.getCharges(player, ability);
        if (charges > 0) {
            graphics.blit(
                    RenderPipelines.GUI_TEXTURED, THROWING_HAMMER_CHARGED,
                    renderX, renderY, 0f, 0f,
                    THROWING_HAMMER_SIZE, THROWING_HAMMER_SIZE,
                    THROWING_HAMMER_SIZE, THROWING_HAMMER_SIZE,
                    THROWING_HAMMER_SIZE, THROWING_HAMMER_SIZE
            );

            int chargeBarY = renderY + THROWING_HAMMER_SIZE + 3;
            for (int i = 0; i < charges-1; i++) {
                graphics.fill(
                        renderX,
                        chargeBarY,
                        renderX + THROWING_HAMMER_SIZE,
                        chargeBarY+2,
                        0xFFC35922
                );
                chargeBarY += 3;
            }
        }

        // cooldown
        float cooldownPercent = AbilityCooldownManager.getCooldownPercent(player, ability, event.getPartialTick().getGameTimeDeltaTicks());
        if (cooldownPercent > 0f) {
            int boxBottom = renderY + THROWING_HAMMER_SIZE;
            int overlayHeight = boxBottom - (int)((1-cooldownPercent) * THROWING_HAMMER_SIZE);

            // main fill
            graphics.fill(
                    renderX,
                    overlayHeight,
                    renderX + THROWING_HAMMER_SIZE,
                    boxBottom,
                    0x55555555
            );

            // highlight bar
            graphics.fill(
                    renderX,
                    overlayHeight,
                    renderX + THROWING_HAMMER_SIZE,
                    overlayHeight-1,
                    0x99FFFFFF
            );
        }
    }
}
