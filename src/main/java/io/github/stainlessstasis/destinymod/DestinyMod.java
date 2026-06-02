package io.github.stainlessstasis.destinymod;

import io.github.stainlessstasis.destinymod.data.DestinyModAttachments;
import io.github.stainlessstasis.destinymod.destiny_classes.Subclasses;
import io.github.stainlessstasis.destinymod.destiny_combat.ability.Abilities;
import io.github.stainlessstasis.destinymod.destiny_combat.ability.AbilityType;
import io.github.stainlessstasis.destinymod.destiny_combat.ability.cooldown.AbilityCooldowns;
import io.github.stainlessstasis.destinymod.destiny_classes.player_equipped.PlayerSubclassData;
import io.github.stainlessstasis.destinymod.entity.DestinyModEntities;
import io.github.stainlessstasis.destinymod.network.clientbound.IgnitionEffectsPacket;
import io.github.stainlessstasis.destinymod.network.serverbound.AbilityCastPacket;
import io.github.stainlessstasis.destinymod.destiny_combat.ability.world_interaction.BlockDestructionManager;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;
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
            registrar.playToClient(
                    IgnitionEffectsPacket.TYPE,
                    IgnitionEffectsPacket.STREAM_CODEC,
                    IgnitionEffectsPacket.Handler::handle
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
            PlayerSubclassData.setEquippedSubclass(player, Subclasses.SUNBREAKER);
            PlayerSubclassData.replaceAbility(player, AbilityType.MELEE, Abilities.THROWING_HAMMER);
        }

        @SubscribeEvent
        public static void onLivingDamage(LivingIncomingDamageEvent event) {
//            var source = event.getSource();
//            System.out.println("SOURCE: "+source);
//            if (source instanceof DestinyModDamageSource destinySource) {
//                System.out.println("IS DESTINY SOURCE");
//                System.out.println("ELEMENT: "+destinySource.destinymod$getElement());
//            }
        }
    }
}
