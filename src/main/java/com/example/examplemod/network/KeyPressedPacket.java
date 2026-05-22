package com.example.examplemod.network;

import com.example.examplemod.DestinyMod;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jetbrains.annotations.NotNull;

public record KeyPressedPacket(Action action) implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<@NotNull KeyPressedPacket> TYPE = new CustomPacketPayload.Type<>(Identifier.fromNamespaceAndPath(DestinyMod.MODID, "key_pressed_packet"));

    public static final StreamCodec<ByteBuf, KeyPressedPacket> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.BYTE.map(
                    byte_ -> Action.values()[byte_],
                    action -> (byte)action.ordinal()
            ),
            KeyPressedPacket::action,
            KeyPressedPacket::new
    );

    public static class Handler {
        public static void handleServerbound(final KeyPressedPacket packet, final IPayloadContext context) {
            switch (packet.action()) {
                case MELEE -> System.out.println("MELEE ATTACK");
            }
        }
    }

    public enum Action {
        MELEE
    }

    @Override
    public @NotNull Type<? extends @NotNull CustomPacketPayload> type() {
        return TYPE;
    }
}
