package io.github.stainlessstasis.destinymod.network.clientbound;

import io.github.stainlessstasis.destinymod.DestinyMod;
import io.github.stainlessstasis.destinymod.client.network.ClientPacketHandlers;
import io.github.stainlessstasis.destinymod.registry.property.ability.ThermiteGrenadeProperty;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.UUIDUtil;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3fc;
import org.jspecify.annotations.NonNull;

import java.util.UUID;

public record ThermiteGrenadeSpawnPacket(int entityId, UUID uuid, Vector3fc pos, float yRot, ThermiteGrenadeProperty properties) implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<@NotNull ThermiteGrenadeSpawnPacket> TYPE = new CustomPacketPayload.Type<>(DestinyMod.id("thermite_grenade_spawn_packet"));

    public static final StreamCodec<ByteBuf, ThermiteGrenadeSpawnPacket> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.VAR_INT, ThermiteGrenadeSpawnPacket::entityId,
            UUIDUtil.STREAM_CODEC, ThermiteGrenadeSpawnPacket::uuid,
            ByteBufCodecs.VECTOR3F, ThermiteGrenadeSpawnPacket::pos,
            ByteBufCodecs.FLOAT, ThermiteGrenadeSpawnPacket::yRot,
            ThermiteGrenadeProperty.STREAM_CODEC, ThermiteGrenadeSpawnPacket::properties,
            ThermiteGrenadeSpawnPacket::new
    );

    public static class Handler {
        public static void handle(final ThermiteGrenadeSpawnPacket packet, final IPayloadContext context) {
            context.enqueueWork(() -> ClientPacketHandlers.handleThermiteGrenadeSpawn(packet, context));
        }
    }

    @Override
    public @NonNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
