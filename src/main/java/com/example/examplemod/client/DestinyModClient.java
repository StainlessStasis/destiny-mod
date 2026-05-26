package com.example.examplemod.client;

import com.example.examplemod.DestinyMod;
import com.example.examplemod.client.entity_renderer.BonkHammerRenderer;
import com.example.examplemod.client.tooltip.DescriptionTooltipComponent;
import com.example.examplemod.client.tooltip.HeaderTooltipComponent;
import com.example.examplemod.tooltip.*;
import com.example.examplemod.client.tooltip.SeparatorTooltipComponent;
import com.example.examplemod.entity.DestinyModEntities;
import com.mojang.datafixers.util.Either;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.locale.Language;
import net.minecraft.network.chat.Component;
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
        event.register(DescriptionComponent.class, component -> new DescriptionTooltipComponent(component.description(), component.widthContext(), component.color()));
    }

    @SubscribeEvent
    public static void onGatherTooltipComponents(RenderTooltipEvent.GatherComponents event) {
        var elements = event.getTooltipElements();
        List<Either<FormattedText, TooltipComponent>> newElements = new ArrayList<>();
        TooltipWidthContext widthContext = new TooltipWidthContext();

        String title = Language.getInstance().getOrDefault("tooltip.examplemod.melting_point.title");
        String subtitle = Language.getInstance().getOrDefault("tooltip.examplemod.melting_point.subtitle");
        var header = new HeaderComponent(title, subtitle, widthContext, 0xFA9D310F);
        newElements.add(Either.right(header));

        var bar = new SeparatorComponent(widthContext, 1, 0xFFF27149);
        newElements.add(Either.right(bar));

        Component desc = DescriptionComponentParser.parseTranslatable("tooltip.examplemod.melting_point.desc");
        var description = new DescriptionComponent(desc, widthContext, 0xEE222222);
        newElements.add(Either.right(description));

        elements.clear();
        elements.addAll(newElements);
    }

    @SubscribeEvent
    public static void onRenderTooltipTexture(RenderTooltipEvent.Texture event) {
        event.setTexture(Identifier.fromNamespaceAndPath(DestinyMod.MODID, "clear"));
    }
}
