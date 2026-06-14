package io.github.stainlessstasis.destinymod.network.serverbound;

import io.github.stainlessstasis.destinymod.destiny_combat.ability.AbilityType;
import io.github.stainlessstasis.destinymod.destiny_combat.ability.cooldown.AbilityCooldownManager;
import io.github.stainlessstasis.destinymod.destiny_classes.player_equipped.PlayerSubclassData;
import io.github.stainlessstasis.destinymod.destiny_combat.ability.grenade.GrenadeBehaviors;
import io.github.stainlessstasis.destinymod.entity.BonkHammerEntity;
import io.github.stainlessstasis.destinymod.DestinyMod;
import io.github.stainlessstasis.destinymod.entity.ThrownGrenadeEntity;
import io.github.stainlessstasis.destinymod.registry.datapack.Abilities;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jetbrains.annotations.NotNull;

public record AbilityCastPacket(AbilityType slot) implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<@NotNull AbilityCastPacket> TYPE = new CustomPacketPayload.Type<>(Identifier.fromNamespaceAndPath(DestinyMod.MODID, "ability_cast_packet"));

    public static final StreamCodec<ByteBuf, AbilityCastPacket> STREAM_CODEC = StreamCodec.composite(
            AbilityType.STREAM_CODEC, AbilityCastPacket::slot,
            AbilityCastPacket::new
    );

    public static class Handler {
        public static void handle(final AbilityCastPacket packet, final IPayloadContext context) {
            // TODO: move this shit to its own class
            Player player = context.player();

            if (packet.slot == AbilityType.MELEE) {
                var ability = PlayerSubclassData.getRegisteredMelee(player);
                if (AbilityCooldownManager.isOnCooldown(player, ability)) {
                    return;
                }

                context.enqueueWork(() -> {
                    player.swing(InteractionHand.MAIN_HAND);

                    if (player.level() instanceof ServerLevel serverLevel) {
                        Projectile.spawnProjectileFromRotation(
                                BonkHammerEntity::new, serverLevel, ItemStack.EMPTY, player, 0f, 1f, 0f
                        );
                        AbilityCooldownManager.addCooldown(player, ability);
                    }
                });
            }

            if (packet.slot == AbilityType.GRENADE) {
                var ability = PlayerSubclassData.getRegisteredGrenade(player);
                if (AbilityCooldownManager.isOnCooldown(player, ability)) {
                    return;
                }

                context.enqueueWork(() -> {
                    player.swing(InteractionHand.MAIN_HAND);

                    if (player.level() instanceof ServerLevel serverLevel) {
                        ThrownGrenadeEntity grenade = Projectile.spawnProjectileFromRotation(
                                ThrownGrenadeEntity::new, serverLevel, new ItemStack(Items.FIRE_CHARGE), player, 0f, 1f, 0f
                        );
                        grenade.setBehavior(GrenadeBehaviors.THERMITE);
                        grenade.setupFromAbility(Abilities.THERMITE_GRENADE);
                        AbilityCooldownManager.addCooldown(player, ability);
                    }
                });
            }
        }
    }

    @Override
    public @NotNull Type<? extends @NotNull CustomPacketPayload> type() {
        return TYPE;
    }
}
