package io.github.stainlessstasis.destinymod.network.clientbound;

import io.github.stainlessstasis.destinymod.client.effects.ClientAudioAndVFX;
import io.github.stainlessstasis.destinymod.DestinyMod;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3fc;

public record AnvilDropEffectsPacket(Vector3fc center, float size) implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<@NotNull AnvilDropEffectsPacket> TYPE = new CustomPacketPayload.Type<>(Identifier.fromNamespaceAndPath(DestinyMod.MODID, "anvil_drop_effects_packet"));

    public static final StreamCodec<ByteBuf, AnvilDropEffectsPacket> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.VECTOR3F, AnvilDropEffectsPacket::center,
            ByteBufCodecs.FLOAT, AnvilDropEffectsPacket::size,
            AnvilDropEffectsPacket::new
    );

    public static class Handler {
        public static void handle(final AnvilDropEffectsPacket packet, final IPayloadContext context) {
            context.enqueueWork(() -> {
                ClientAudioAndVFX.anvilDrop(context.player().level(), new Vec3(packet.center()), packet.size());
            });
        }
    }

    @Override
    public @NotNull Type<? extends @NotNull CustomPacketPayload> type() {
        return TYPE;
    }
}


