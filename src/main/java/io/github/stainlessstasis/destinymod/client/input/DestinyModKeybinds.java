package io.github.stainlessstasis.destinymod.client.input;

import io.github.stainlessstasis.destinymod.DestinyMod;
import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import net.minecraft.resources.Identifier;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import net.neoforged.neoforge.common.util.Lazy;
import org.lwjgl.glfw.GLFW;

@EventBusSubscriber
public class DestinyModKeybinds {
    public static final KeyMapping.Category KEY_CATEGORY = new KeyMapping.Category(Identifier.fromNamespaceAndPath(DestinyMod.MODID, "keybinds"));

    public static final Lazy<KeyMapping> MELEE = Lazy.of(() ->
            new KeyMapping(
                    "key."+DestinyMod.MODID+".melee",
                    InputConstants.Type.KEYSYM,
                    GLFW.GLFW_KEY_V,
                    KEY_CATEGORY
            ));

    @SubscribeEvent
    public static void registerKeybinds(RegisterKeyMappingsEvent event) {
        event.register(MELEE.get());
    }
}
