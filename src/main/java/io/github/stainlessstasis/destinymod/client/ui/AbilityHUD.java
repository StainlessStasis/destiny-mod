package io.github.stainlessstasis.destinymod.client.ui;

import io.github.stainlessstasis.destinymod.DestinyMod;
import io.github.stainlessstasis.destinymod.destiny_combat.ability.cooldown.AbilityCooldownManager;
import io.github.stainlessstasis.destinymod.destiny_classes.player_equipped.PlayerSubclassData;
import io.github.stainlessstasis.destinymod.registry.datapack.RegisteredAbility;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Player;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RenderGuiLayerEvent;

import java.util.ArrayList;
import java.util.List;

@EventBusSubscriber(modid = DestinyMod.MODID, value = Dist.CLIENT)
public class AbilityHUD {
    public static final int ABILITY_ICON_SIZE = 32;
    public static final int ABILITY_BORDER_SIZE = ABILITY_ICON_SIZE+2;
    public static final int HORIZONTAL_SPACING = 8;
    public static final Identifier ABILITY_BORDER = DestinyMod.id("textures/gui/sprites/destiny_ui/ability_border.png");
    public static final Identifier THROWING_HAMMER = DestinyMod.id("textures/gui/sprites/destiny_ui/throwing_hammer.png");
    public static final Identifier THROWING_HAMMER_CHARGED = DestinyMod.id("textures/gui/sprites/destiny_ui/throwing_hammer_charged.png");
    public static final Identifier THERMITE_GRENADE = DestinyMod.id("textures/gui/sprites/destiny_ui/throwing_hammer.png");
    public static final Identifier THERMITE_GRENADE_CHARGED = DestinyMod.id("textures/gui/sprites/destiny_ui/throwing_hammer_charged.png");

    @SubscribeEvent
    public static void onRenderGuiLayers(RenderGuiLayerEvent.Post event) {
        Minecraft mc = Minecraft.getInstance();
        Player player = mc.player;
        if (player == null || mc.options.hideGui || mc.screen != null) return;

        List<AbilityUIDisplay> activeAbilities = new ArrayList<>();

        var melee = PlayerSubclassData.getRegisteredMelee(player);
        if (melee != null) {
            activeAbilities.add(new AbilityUIDisplay(melee, THROWING_HAMMER, THROWING_HAMMER_CHARGED));
        }

        var grenade = PlayerSubclassData.getRegisteredGrenade(player);
        if (grenade != null) {
            activeAbilities.add(new AbilityUIDisplay(grenade, THERMITE_GRENADE, THERMITE_GRENADE_CHARGED));
        }

        if (activeAbilities.isEmpty()) return;

        GuiGraphicsExtractor graphics = event.getGuiGraphics();
        int renderX = ABILITY_ICON_SIZE/2;
        int renderY = mc.getWindow().getGuiScaledHeight() - (int) (ABILITY_ICON_SIZE * 1.5f);
        float partialTick = event.getPartialTick().getGameTimeDeltaTicks();

        for (AbilityUIDisplay display : activeAbilities) {
            renderAbilityBox(graphics, player, display, renderX, renderY, partialTick);
            renderX += ABILITY_ICON_SIZE + HORIZONTAL_SPACING;
        }

    }

    private static void renderAbilityBox(GuiGraphicsExtractor graphics, Player player, AbilityUIDisplay display, int renderX, int renderY, float partialTick) {
        RegisteredAbility ability = display.ability();

        renderBorder(graphics, renderX, renderY);
        renderBaseIcon(graphics, display.baseIcon(), renderX, renderY);

        int charges = AbilityCooldownManager.getCharges(player, ability);
        if (charges > 0) {
            renderChargedState(graphics, display.chargedIcon(), renderX, renderY, charges);
        }

        float cooldownPercent = AbilityCooldownManager.getCooldownPercent(player, ability, partialTick);
        if (cooldownPercent > 0f) {
            renderCooldown(graphics, renderX, renderY, cooldownPercent);
        }
    }

    private static void renderBorder(GuiGraphicsExtractor graphics, int x, int y) {
        int borderSize = ABILITY_BORDER_SIZE;
        graphics.blit(
                RenderPipelines.GUI_TEXTURED, ABILITY_BORDER,
                x-1, y-1, 0f, 0f,
                borderSize, borderSize,
                borderSize, borderSize,
                borderSize, borderSize
        );
    }

    private static void renderBaseIcon(GuiGraphicsExtractor graphics, Identifier icon, int x, int y) {
        int iconSize = ABILITY_ICON_SIZE;
        graphics.blit(
                RenderPipelines.GUI_TEXTURED, icon,
                x, y, 0f, 0f,
                iconSize, iconSize,
                iconSize, iconSize,
                iconSize, iconSize
        );
    }

    private static void renderChargedState(GuiGraphicsExtractor graphics, Identifier chargedIcon, int x, int y, int charges) {
        int iconSize = ABILITY_ICON_SIZE;
        graphics.blit(
                RenderPipelines.GUI_TEXTURED, chargedIcon,
                x, y, 0f, 0f,
                iconSize, iconSize,
                iconSize, iconSize,
                iconSize, iconSize
        );

        int chargeBarY = y + iconSize + 3;
        for (int i = 0; i < charges-1; i++) {
            graphics.fill(
                    x,
                    chargeBarY,
                    x + iconSize,
                    chargeBarY+2,
                    0xFFC35922
            );
            chargeBarY += 3;
        }
    }

    private static void renderCooldown(GuiGraphicsExtractor graphics, int x, int y, float cooldownPercent) {
        int iconSize = ABILITY_ICON_SIZE;
        int boxBottom = y + iconSize;
        int overlayHeight = boxBottom - (int) ((1 - cooldownPercent) * iconSize);

        graphics.fill(
                x,
                overlayHeight,
                x + iconSize,
                boxBottom,
                0x55555555
        );

        graphics.fill(
                x,
                overlayHeight,
                x + iconSize,
                overlayHeight - 1,
                0x99FFFFFF
        );
    }

    public record AbilityUIDisplay(RegisteredAbility ability, Identifier baseIcon, Identifier chargedIcon) {}
}
