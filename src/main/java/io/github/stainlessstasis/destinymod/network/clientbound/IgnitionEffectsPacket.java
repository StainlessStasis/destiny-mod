package io.github.stainlessstasis.destinymod.network.clientbound;

import io.github.stainlessstasis.destinymod.client.particle.ClientParticleEffects;
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

public record IgnitionEffectsPacket(Vector3fc center) implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<@NotNull IgnitionEffectsPacket> TYPE = new CustomPacketPayload.Type<>(Identifier.fromNamespaceAndPath(DestinyMod.MODID, "ignition_effects_packet"));

    public static final StreamCodec<ByteBuf, IgnitionEffectsPacket> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.VECTOR3F, IgnitionEffectsPacket::center,
            IgnitionEffectsPacket::new
    );

    public static class Handler {
        public static void handle(final IgnitionEffectsPacket packet, final IPayloadContext context) {
            context.enqueueWork(() -> {
                ClientParticleEffects.ignition(new Vec3(packet.center()));
            });
        }
    }

    @Override
    public @NotNull Type<? extends @NotNull CustomPacketPayload> type() {
        return TYPE;
    }
}

