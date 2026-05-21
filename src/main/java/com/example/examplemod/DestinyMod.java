package com.example.examplemod;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.*;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import org.slf4j.Logger;

import com.mojang.logging.LogUtils;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.common.NeoForge;

@Mod(DestinyMod.MODID)
public class DestinyMod {
    public static final String MODID = "examplemod";
    public static final Logger LOGGER = LogUtils.getLogger();

    public DestinyMod(IEventBus modEventBus, ModContainer modContainer) {
        modEventBus.addListener(this::commonSetup);
        NeoForge.EVENT_BUS.register(this);
        modContainer.registerConfig(ModConfig.Type.COMMON, Config.SPEC);
    }

    private void commonSetup(FMLCommonSetupEvent event) {}

    @SubscribeEvent
    public void onRightClick(PlayerInteractEvent.RightClickEmpty event) {
        System.out.println("RIGHT CLICK EMPTY");
        Player player = event.getEntity();
        player.swing(InteractionHand.MAIN_HAND);
        System.out.println("SIDE: "+event.getSide());

        if (player.level() instanceof ServerLevel serverLevel) {
            BonkHammerEntity hammer = Projectile.spawnProjectileFromRotation(
                    BonkHammerEntity::new, serverLevel, ItemStack.EMPTY, player, 0f, 3f, 0f
            );
        }
    }
}
