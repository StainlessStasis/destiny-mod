package com.example.examplemod.network;

import com.example.examplemod.ability.Ability;
import com.example.examplemod.entity.BonkHammerEntity;
import com.example.examplemod.DestinyMod;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jetbrains.annotations.NotNull;

public record AbilityCastPacket(Ability ability) implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<@NotNull AbilityCastPacket> TYPE = new CustomPacketPayload.Type<>(Identifier.fromNamespaceAndPath(DestinyMod.MODID, "ability_cast_packet"));

    public static final StreamCodec<ByteBuf, AbilityCastPacket> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.BYTE.map(
                    byte_ -> Ability.values()[byte_],
                    action -> (byte)action.ordinal()
            ),
            AbilityCastPacket::ability,
            AbilityCastPacket::new
    );

    public static class Handler {
        public static void handleServerbound(final AbilityCastPacket packet, final IPayloadContext context) {
            switch (packet.ability()) {
                case MELEE -> {
                    context.enqueueWork(() -> {
                        Player player = context.player();
                        player.swing(InteractionHand.MAIN_HAND);

                        if (player.level() instanceof ServerLevel serverLevel) {
                            BonkHammerEntity hammer = Projectile.spawnProjectileFromRotation(
                                    BonkHammerEntity::new, serverLevel, ItemStack.EMPTY, player, 0f, 1f, 0f
                            );
                            hammer.setBaseDamage(7f);
                        }
                    });
                }
            }
        }
    }

    @Override
    public @NotNull Type<? extends @NotNull CustomPacketPayload> type() {
        return TYPE;
    }
}
