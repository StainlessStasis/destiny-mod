package io.github.stainlessstasis.destinymod.client;

import io.github.stainlessstasis.destinymod.DestinyMod;
import io.github.stainlessstasis.destinymod.client.tooltip.ActionHintTooltipComponent;
import io.github.stainlessstasis.destinymod.client.tooltip.DescriptionTooltipComponent;
import io.github.stainlessstasis.destinymod.client.tooltip.HeaderTooltipComponent;
import io.github.stainlessstasis.destinymod.client.tooltip.SeparatorTooltipComponent;
import io.github.stainlessstasis.destinymod.tooltip.component.ActionHintComponent;
import io.github.stainlessstasis.destinymod.tooltip.component.DescriptionComponent;
import io.github.stainlessstasis.destinymod.tooltip.component.HeaderComponent;
import io.github.stainlessstasis.destinymod.tooltip.component.SeparatorComponent;
import net.minecraft.resources.Identifier;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.event.RegisterClientTooltipComponentFactoriesEvent;
import net.neoforged.neoforge.client.event.RenderTooltipEvent;
import net.neoforged.neoforge.client.gui.ConfigurationScreen;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;

@Mod(value = DestinyMod.MODID, dist = Dist.CLIENT)
@EventBusSubscriber(modid = DestinyMod.MODID, value = Dist.CLIENT)
public class DestinyModClient {
    public DestinyModClient(ModContainer container) {
        container.registerExtensionPoint(IConfigScreenFactory.class, ConfigurationScreen::new);
    }

    @SubscribeEvent
    public static void registerTooltipFactories(RegisterClientTooltipComponentFactoriesEvent event) {
        event.register(SeparatorComponent.class, component -> new SeparatorTooltipComponent(component.widthContext(), component.height(), component.color()));
        event.register(HeaderComponent.class, component -> new HeaderTooltipComponent(component.title(), component.subtitle(), component.widthContext(), component.color()));
        event.register(DescriptionComponent.class, component -> new DescriptionTooltipComponent(component.description(), component.widthContext(), component.color()));
        event.register(ActionHintComponent.class, component -> new ActionHintTooltipComponent(component.hint(), component.widthContext()));
    }

    @SubscribeEvent
    public static void onGatherTooltipComponents(RenderTooltipEvent.GatherComponents event) {}

    @SubscribeEvent
    public static void onRenderTooltipTexture(RenderTooltipEvent.Texture event) {
        event.setTexture(Identifier.fromNamespaceAndPath(DestinyMod.MODID, "clear"));
    }
}
