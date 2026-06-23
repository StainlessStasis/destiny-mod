package io.github.stainlessstasis.destinymod;

import io.github.stainlessstasis.destinymod.data.DestinyModAttachments;
import io.github.stainlessstasis.destinymod.destiny_classes.Subclasses;
import io.github.stainlessstasis.destinymod.destiny_combat.ability.grenade.GrenadeBehaviors;
import io.github.stainlessstasis.destinymod.network.clientbound.ThermiteGrenadeSpawnPacket;
import io.github.stainlessstasis.destinymod.registry.datapack.Abilities;
import io.github.stainlessstasis.destinymod.destiny_combat.ability.AbilityType;
import io.github.stainlessstasis.destinymod.destiny_classes.player_equipped.PlayerSubclassData;
import io.github.stainlessstasis.destinymod.registry.datapack.Aspects;
import io.github.stainlessstasis.destinymod.entity.DestinyModEntities;
import io.github.stainlessstasis.destinymod.network.clientbound.AnvilDropEffectsPacket;
import io.github.stainlessstasis.destinymod.network.clientbound.IgnitionEffectsPacket;
import io.github.stainlessstasis.destinymod.network.serverbound.AbilityCastPacket;
import io.github.stainlessstasis.destinymod.destiny_combat.ability.world_interaction.BlockDestructionManager;
import io.github.stainlessstasis.destinymod.network.serverbound.EquipAspectsPacket;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.server.ServerStoppingEvent;
import net.neoforged.neoforge.event.tick.ServerTickEvent;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;
import org.slf4j.Logger;

import com.mojang.logging.LogUtils;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.ModContainer;

@Mod(DestinyMod.MODID)
public class DestinyMod {
    public static final String MODID = "destinymod";
    public static final String NETWORK_VERSION = "1";
    public static final Logger LOGGER = LogUtils.getLogger();

    public DestinyMod(IEventBus modEventBus, ModContainer modContainer) {
        modContainer.registerConfig(ModConfig.Type.COMMON, Config.SPEC);
        DestinyModAttachments.register(modEventBus);
        DestinyModEntities.register(modEventBus);
        GrenadeBehaviors.registerRegistry(modEventBus);
    }

    public static Identifier id(String path) {
        return Identifier.fromNamespaceAndPath(MODID, path);
    }

    @EventBusSubscriber
    public static class ModBusEvents {
        @SubscribeEvent
        public static void registerPackets(RegisterPayloadHandlersEvent event) {
            final PayloadRegistrar registrar = event.registrar(NETWORK_VERSION);
            registrar.playToServer(
                    AbilityCastPacket.TYPE,
                    AbilityCastPacket.STREAM_CODEC,
                    AbilityCastPacket.Handler::handle
            );
            registrar.playToServer(
                    EquipAspectsPacket.TYPE,
                    EquipAspectsPacket.STREAM_CODEC,
                    EquipAspectsPacket.Handler::handle
            );
            registrar.playToClient(
                    IgnitionEffectsPacket.TYPE,
                    IgnitionEffectsPacket.STREAM_CODEC,
                    IgnitionEffectsPacket.Handler::handle
            );
            registrar.playToClient(
                    AnvilDropEffectsPacket.TYPE,
                    AnvilDropEffectsPacket.STREAM_CODEC,
                    AnvilDropEffectsPacket.Handler::handle
            );
            registrar.playToClient(
                    ThermiteGrenadeSpawnPacket.TYPE,
                    ThermiteGrenadeSpawnPacket.STREAM_CODEC,
                    ThermiteGrenadeSpawnPacket.Handler::handle
            );
        }
    }

    @EventBusSubscriber
    public static class GameBusEvents {
        @SubscribeEvent
        public static void onServerTick(ServerTickEvent.Post event) {
            BlockDestructionManager.removeInactive(event.getServer(), event.getServer().overworld().getGameTime());
        }

        @SubscribeEvent
        public static void onServerStopping(ServerStoppingEvent event) {
            BlockDestructionManager.cleanup();
        }

        @SubscribeEvent
        public static void onPlayerJoin(PlayerEvent.PlayerLoggedInEvent event) {
            Player player = event.getEntity();
            PlayerSubclassData.setEquippedSubclass(player, Subclasses.SUNBREAKER);
            PlayerSubclassData.replaceAbility(player, AbilityType.MELEE, Abilities.THROWING_HAMMER);
            PlayerSubclassData.replaceAbility(player, AbilityType.GRENADE, Abilities.THERMITE_GRENADE);
            PlayerSubclassData.replaceAbility(player, AbilityType.CLASS_ABILITY, Abilities.BARRICADE);
            player.setData(DestinyModAttachments.PLAYER_UNLOCKED_ASPECTS, Aspects.getAll());
            PlayerSubclassData.setMaxAspectsEquippable(player, 2);
        }
    }
}
