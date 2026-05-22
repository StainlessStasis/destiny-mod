package com.example.examplemod.client;

import com.example.examplemod.DestinyMod;
import com.example.examplemod.client.entity_renderer.BonkHammerRenderer;
import com.example.examplemod.entity.DestinyModEntities;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.gui.ConfigurationScreen;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;

@Mod(value = DestinyMod.MODID, dist = Dist.CLIENT)
@EventBusSubscriber(modid = DestinyMod.MODID, value = Dist.CLIENT)
public class DestinyModClient {
    public DestinyModClient(ModContainer container) {
        container.registerExtensionPoint(IConfigScreenFactory.class, ConfigurationScreen::new);
    }

    @SubscribeEvent
    public static void registerEntityRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer(DestinyModEntities.HAMMER_OF_SOL.get(), BonkHammerRenderer::new);
    }
}
