package com.example.examplemod.client;

import com.example.examplemod.DestinyMod;
import com.example.examplemod.client.entity_renderer.BonkHammerRenderer;
import com.example.examplemod.client.tooltip.HeaderTooltipComponent;
import com.example.examplemod.tooltip.HeaderComponent;
import com.example.examplemod.tooltip.SeparatorComponent;
import com.example.examplemod.client.tooltip.SeparatorTooltipComponent;
import com.example.examplemod.entity.DestinyModEntities;
import com.example.examplemod.tooltip.TooltipWidthContext;
import com.mojang.datafixers.util.Either;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.resources.Identifier;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.event.RegisterClientTooltipComponentFactoriesEvent;
import net.neoforged.neoforge.client.event.RenderTooltipEvent;
import net.neoforged.neoforge.client.gui.ConfigurationScreen;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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

    @SubscribeEvent
    public static void registerTooltipFactories(RegisterClientTooltipComponentFactoriesEvent event) {
        event.register(SeparatorComponent.class, component -> new SeparatorTooltipComponent(component.widthContext(), component.height(), component.color()));
        event.register(HeaderComponent.class, component -> new HeaderTooltipComponent(component.title(), component.subtitle(), component.widthContext(), component.color()));
    }

    @SubscribeEvent
    public static void onGatherTooltipComponents(RenderTooltipEvent.GatherComponents event) {
        var elements = event.getTooltipElements();
        List<Either<FormattedText, TooltipComponent>> newElements = new ArrayList<>();
        var mc = Minecraft.getInstance();

        TooltipWidthContext widthContext = new TooltipWidthContext();
        var header = new HeaderComponent("MELTING POINT", "Sunbreaker Aspect", widthContext, 0xAA9D310F);
        var bar = new SeparatorComponent(widthContext, 5, 0xFFAAFFFF);
        newElements.addFirst(Either.right(header));
        newElements.add(Either.right(bar));

        Set<String> tooltipStrings = new HashSet<>();
        tooltipStrings.add(header.title());
        tooltipStrings.add(header.subtitle());
        int maxWidth = 0;
        for (String string : tooltipStrings) {
            int width = mc.font.width(string);
            if (width > maxWidth) maxWidth = width;
        }
        maxWidth += 16;
        widthContext.setWidth(maxWidth);

        elements.clear();
        elements.addAll(newElements);
    }

    @SubscribeEvent
    public static void onRenderTooltipTexture(RenderTooltipEvent.Texture event) {
        event.setTexture(Identifier.fromNamespaceAndPath(DestinyMod.MODID, "clear"));
    }
}
