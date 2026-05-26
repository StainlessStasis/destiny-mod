package io.github.stainlessstasis.destinymod;

import io.github.stainlessstasis.destinymod.ability.Abilities;
import io.github.stainlessstasis.destinymod.ability.AbilityType;
import io.github.stainlessstasis.destinymod.ability.PlayerAbilities;
import io.github.stainlessstasis.destinymod.ability.cooldown.AbilityCooldowns;
import io.github.stainlessstasis.destinymod.entity.DestinyModEntities;
import io.github.stainlessstasis.destinymod.network.AbilityCastPacket;
import io.github.stainlessstasis.destinymod.ability.world_interaction.BlockDestructionManager;
import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.server.ServerStoppingEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;
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
    }

    @EventBusSubscriber
    public static class ModBusEvents {
        @SubscribeEvent
        public static void registerPackets(RegisterPayloadHandlersEvent event) {
            final PayloadRegistrar registrar = event.registrar(NETWORK_VERSION);
            registrar.playToServer(
                    AbilityCastPacket.TYPE,
                    AbilityCastPacket.STREAM_CODEC,
                    AbilityCastPacket.Handler::handleServerbound
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
        public static void onPlayerTick(PlayerTickEvent.Post event) {
            Player player = event.getEntity();

            AbilityCooldowns abilityCooldowns = player.getData(DestinyModAttachments.ABILITY_COOLDOWNS);
            abilityCooldowns.tick();
        }

        @SubscribeEvent
        public static void onPlayerJoin(PlayerEvent.PlayerLoggedInEvent event) {
            Player player = event.getEntity();
            PlayerAbilities.get(player).equip(player, AbilityType.MELEE, Abilities.THROWING_HAMMER);
        }
    }
}
