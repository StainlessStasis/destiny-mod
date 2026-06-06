package io.github.stainlessstasis.destinymod.network.serverbound;

import io.github.stainlessstasis.destinymod.destiny_classes.Subclass;
import io.github.stainlessstasis.destinymod.destiny_classes.Subclasses;
import io.github.stainlessstasis.destinymod.destiny_classes.player_equipped.PlayerSubclassData;
import io.github.stainlessstasis.destinymod.destiny_combat.ability.Aspect;
import io.github.stainlessstasis.destinymod.destiny_combat.ability.registry.RegisteredAspect;
import io.github.stainlessstasis.destinymod.DestinyMod;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public record EquipAspectsPacket(List<RegisteredAspect> aspectsToEquip) implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<@NotNull EquipAspectsPacket> TYPE = new CustomPacketPayload.Type<>(Identifier.fromNamespaceAndPath(DestinyMod.MODID, "equip_aspects_packet"));

    public static final StreamCodec<ByteBuf, EquipAspectsPacket> STREAM_CODEC = StreamCodec.composite(
            RegisteredAspect.STREAM_CODEC.apply(ByteBufCodecs.list(16)), EquipAspectsPacket::aspectsToEquip,
            EquipAspectsPacket::new
    );

    public static class Handler {
        public static void handle(final EquipAspectsPacket packet, final IPayloadContext context) {
            context.enqueueWork(() -> {
                Player player = context.player();
                Subclass subclass = PlayerSubclassData.getEquippedSubclass(player);
                Identifier subclassID = Subclasses.getID(subclass);
                int maxEquippable = PlayerSubclassData.getMaxAspectsEquippable(player);
                List<RegisteredAspect> equipped = new ArrayList<>();

                for (RegisteredAspect registeredAspect : packet.aspectsToEquip()) {
                    if (equipped.size() >= maxEquippable) break;

                    Aspect aspect = registeredAspect.get(player);
                    boolean isCorrectSubclass = aspect.subclassID().equals(subclassID);
                    boolean isUnlocked = PlayerSubclassData.hasUnlockedAspect(player, registeredAspect);
                    if (isCorrectSubclass && isUnlocked) {
                        equipped.add(registeredAspect);
                    }
                }

                PlayerSubclassData.equipAllAspects(player, equipped);
            });
        }
    }

    @Override
    public @NotNull Type<? extends @NotNull CustomPacketPayload> type() {
        return TYPE;
    }
}

