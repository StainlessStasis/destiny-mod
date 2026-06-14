package io.github.stainlessstasis.destinymod.client.network;

import io.github.stainlessstasis.destinymod.entity.DestinyModEntities;
import io.github.stainlessstasis.destinymod.entity.ThermiteGrenadeEntity;
import io.github.stainlessstasis.destinymod.network.clientbound.ThermiteGrenadeSpawnPacket;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public class ClientPacketHandlers {
    public static void handleThermiteGrenadeSpawn(final ThermiteGrenadeSpawnPacket packet, final IPayloadContext context) {
        if (!(context.player().level() instanceof ClientLevel level)) return;

        ThermiteGrenadeEntity grenade = ThermiteGrenadeEntity.createDefault(DestinyModEntities.THERMITE_GRENADE.get(), level);
        double x = packet.pos().x();
        double y = packet.pos().y();
        double z = packet.pos().z();

        grenade.syncPacketPositionCodec(x, y, z);
        grenade.snapTo(x, y, z, packet.yRot(), 0);
        grenade.setId(packet.entityId());
        grenade.setUUID(packet.uuid());
        grenade.setDeltaMovement(Vec3.ZERO);
        grenade.applyProperties(packet.properties());

        level.addEntity(grenade);
    }
}
