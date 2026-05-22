package com.example.examplemod.client.input;

import com.example.examplemod.network.KeyPressedPacket;
import net.neoforged.neoforge.client.network.ClientPacketDistributor;
import org.jetbrains.annotations.ApiStatus;

public class InputHandler {
    @ApiStatus.Internal
    public static void handleInput() {
        while (DestinyModKeybinds.MELEE.get().consumeClick()) {
            ClientPacketDistributor.sendToServer(new KeyPressedPacket(KeyPressedPacket.Action.MELEE));
        }
    }
}
