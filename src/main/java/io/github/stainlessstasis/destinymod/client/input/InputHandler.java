package io.github.stainlessstasis.destinymod.client.input;

import io.github.stainlessstasis.destinymod.ability.Ability;
import io.github.stainlessstasis.destinymod.network.AbilityCastPacket;
import net.minecraft.client.Minecraft;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.InputEvent;
import net.neoforged.neoforge.client.network.ClientPacketDistributor;
import org.jetbrains.annotations.ApiStatus;
import org.lwjgl.glfw.GLFW;

@EventBusSubscriber
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

        Player player = Minecraft.getInstance().player;
        if (player == null) return; // player can be null while in the main menu and whatnot
        if (player.hasContainerOpen() || Minecraft.getInstance().screen != null) return;

        if (key == DestinyModKeybinds.MELEE.get().getKey().getValue()) {
            player.swing(InteractionHand.MAIN_HAND);
            ClientPacketDistributor.sendToServer(new AbilityCastPacket(Ability.MELEE));
        }
    }
}
