package io.github.stainlessstasis.destinymod.client.input;

import com.mojang.blaze3d.platform.InputConstants;
import io.github.stainlessstasis.destinymod.DestinyMod;
import io.github.stainlessstasis.destinymod.api.block_display_fx.client.AnimationTest;
import io.github.stainlessstasis.destinymod.destiny_classes.Subclasses;
import io.github.stainlessstasis.destinymod.destiny_combat.ability.AbilityType;
import io.github.stainlessstasis.destinymod.client.ui.SubclassScreen;
import io.github.stainlessstasis.destinymod.network.serverbound.AbilityCastPacket;
import net.minecraft.client.Minecraft;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.InputEvent;
import net.neoforged.neoforge.client.network.ClientPacketDistributor;
import org.jetbrains.annotations.ApiStatus;
import org.lwjgl.glfw.GLFW;

@EventBusSubscriber(modid = DestinyMod.MODID, value = Dist.CLIENT)
public class InputHandler {
    @SubscribeEvent
    public static void onMouseInput(InputEvent.MouseButton.Post event) {
        onAnyKeyInput(event.getButton(), event.getAction());
    }

    @SubscribeEvent
    public static void onKeyInput(InputEvent.Key event) {
        onAnyKeyInput(event.getKey(), event.getAction());
    }

    @ApiStatus.Internal
    public static void onAnyKeyInput(int key, int action) {
        if (action != GLFW.GLFW_PRESS) return;

        Minecraft mc = Minecraft.getInstance();
        Player player = mc.player;
        if (player == null) return; // player can be null while in the main menu and whatnot
        if (player.hasContainerOpen() || Minecraft.getInstance().screen != null) return;

        if (key == DestinyModKeybinds.MELEE.get().getKey().getValue()) {
            player.swing(InteractionHand.MAIN_HAND);
            ClientPacketDistributor.sendToServer(new AbilityCastPacket(AbilityType.MELEE));
        }

        if (key == DestinyModKeybinds.GRENADE.get().getKey().getValue()) {
            player.swing(InteractionHand.MAIN_HAND);
            ClientPacketDistributor.sendToServer(new AbilityCastPacket(AbilityType.GRENADE));
        }

        // TODO: remove this
        if (key == InputConstants.KEY_LALT) {
            AnimationTest.runKeyframeTest();
        }

        if (key == DestinyModKeybinds.SUBCLASS_SCREEN.get().getKey().getValue()) {
            mc.setScreen(new SubclassScreen(Subclasses.SUNBREAKER));
        }
    }
}
