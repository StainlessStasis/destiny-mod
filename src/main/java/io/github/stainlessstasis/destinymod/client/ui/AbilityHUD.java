package io.github.stainlessstasis.destinymod.client.ui;

import io.github.stainlessstasis.destinymod.DestinyMod;
import io.github.stainlessstasis.destinymod.destiny_combat.ability.cooldown.AbilityCooldownManager;
import io.github.stainlessstasis.destinymod.destiny_classes.player_equipped.PlayerSubclassData;
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
    public static final int ABILITY_ICON_SIZE = 32;
    public static final int ABILITY_BORDER_SIZE = ABILITY_ICON_SIZE+2;
        public static final Identifier ABILITY_BORDER = DestinyMod.id("textures/gui/sprites/destiny_hud/ability_border.png");
    public static final Identifier THROWING_HAMMER = DestinyMod.id("textures/gui/sprites/destiny_hud/throwing_hammer.png");
    public static final Identifier THROWING_HAMMER_CHARGED = DestinyMod.id("textures/gui/sprites/destiny_hud/throwing_hammer_charged.png");

    @SubscribeEvent
    public static void onRenderGuiLayers(RenderGuiLayerEvent.Post event) {
        Minecraft mc = Minecraft.getInstance();
        Player player = mc.player;
        if (player == null || mc.options.hideGui || mc.screen != null) return;

        int renderX = ABILITY_ICON_SIZE/2;
        int renderY = mc.getWindow().getGuiScaledHeight() - (int)(ABILITY_ICON_SIZE * 1.5f);
        var ability = PlayerSubclassData.getRegisteredMelee(player);
        var graphics = event.getGuiGraphics();

        // border
        int borderSize = ABILITY_BORDER_SIZE;
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
                ABILITY_ICON_SIZE, ABILITY_ICON_SIZE,
                ABILITY_ICON_SIZE, ABILITY_ICON_SIZE,
                ABILITY_ICON_SIZE, ABILITY_ICON_SIZE
        );

        // charge background and amounts
        int charges = AbilityCooldownManager.getCharges(player, ability);
        if (charges > 0) {
            graphics.blit(
                    RenderPipelines.GUI_TEXTURED, THROWING_HAMMER_CHARGED,
                    renderX, renderY, 0f, 0f,
                    ABILITY_ICON_SIZE, ABILITY_ICON_SIZE,
                    ABILITY_ICON_SIZE, ABILITY_ICON_SIZE,
                    ABILITY_ICON_SIZE, ABILITY_ICON_SIZE
            );

            int chargeBarY = renderY + ABILITY_ICON_SIZE + 3;
            for (int i = 0; i < charges-1; i++) {
                graphics.fill(
                        renderX,
                        chargeBarY,
                        renderX + ABILITY_ICON_SIZE,
                        chargeBarY+2,
                        0xFFC35922
                );
                chargeBarY += 3;
            }
        }

        // cooldown
        float cooldownPercent = AbilityCooldownManager.getCooldownPercent(player, ability, event.getPartialTick().getGameTimeDeltaTicks());
        if (cooldownPercent > 0f) {
            int boxBottom = renderY + ABILITY_ICON_SIZE;
            int overlayHeight = boxBottom - (int)((1-cooldownPercent) * ABILITY_ICON_SIZE);

            // main fill
            graphics.fill(
                    renderX,
                    overlayHeight,
                    renderX + ABILITY_ICON_SIZE,
                    boxBottom,
                    0x55555555
            );

            // highlight bar
            graphics.fill(
                    renderX,
                    overlayHeight,
                    renderX + ABILITY_ICON_SIZE,
                    overlayHeight-1,
                    0x99FFFFFF
            );
        }
    }
}
