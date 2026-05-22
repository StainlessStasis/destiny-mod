package com.example.examplemod;

import com.example.examplemod.network.KeyPressedPacket;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
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
    public static final String MODID = "examplemod";
    public static final String NETWORK_VERSION = "1";
    public static final Logger LOGGER = LogUtils.getLogger();

    public DestinyMod(IEventBus modEventBus, ModContainer modContainer) {
        modContainer.registerConfig(ModConfig.Type.COMMON, Config.SPEC);
    }

    @EventBusSubscriber
    public static class ModBusEvents {
        @SubscribeEvent
        public static void registerPackets(RegisterPayloadHandlersEvent event) {
            final PayloadRegistrar registrar = event.registrar(NETWORK_VERSION);
            registrar.playToServer(
                    KeyPressedPacket.TYPE,
                    KeyPressedPacket.STREAM_CODEC,
                    KeyPressedPacket.Handler::handleServerbound
            );
        }
    }

    @EventBusSubscriber
    public static class GameBusEvents {

    }
}
