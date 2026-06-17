package io.github.stainlessstasis.destinymod.api.block_display_fx.client;

import io.github.stainlessstasis.destinymod.api.block_display_fx.VfxEntity;
import io.github.stainlessstasis.destinymod.api.block_display_fx.channel.RotationDegreesChannel;
import io.github.stainlessstasis.destinymod.api.block_display_fx.channel.Vector3fChannel;
import io.github.stainlessstasis.destinymod.api.block_display_fx.channel.VfxAnimation;
import io.github.stainlessstasis.destinymod.api.block_display_fx.easing.Easing;
import io.github.stainlessstasis.destinymod.entity.DestinyModEntities;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3f;

public class AnimationTest {
    private static final int COUNT = 5000;

    public static void run() {
        LocalPlayer player = Minecraft.getInstance().player;
        if (player == null) return;
        ClientLevel level = Minecraft.getInstance().level;
        if (level == null) return;

        VfxEntity entity = new VfxEntity(DestinyModEntities.VFX_ENTITY.get(), level);
        Vec3 pos = player.getEyePosition().add(player.getLookAngle().normalize().scale(3f));
        entity.setPos(pos);
        entity.setBlockState(Blocks.MAGMA_BLOCK.defaultBlockState());
        level.addEntity(entity);

        VfxAnimation anim = new VfxAnimation(
                new Vector3fChannel(
                        new Vector3f(0, 0, 0),
                        new Vector3f(
                                (float)(Math.random() * 4 - 2),
                                (float)(Math.random() * 4),
                                (float)(Math.random() * 4 - 2)
                        ),
                        Easing.EASE_IN_QUAD
                ),
                new Vector3fChannel(
                        new Vector3f(0.5f, 0.5f, 0.5f),
                        new Vector3f(
                                (float)(Math.random() * 1.5f + 0.5f),
                                (float)(Math.random() * 1.5f + 0.5f),
                                (float)(Math.random() * 1.5f + 0.5f)
                        ),
                        Easing.EASE_IN_QUAD
                ),
                new RotationDegreesChannel(
                        new Vector3f(0, 0, 0),
                        new Vector3f((float) (Math.random()*30), 360 + (float) (Math.random()*360), (float) (Math.random()*30)),
                        Easing.EASE_IN_QUAD
                )
        );

        int duration = 60;
        entity.playAnimation(anim, duration);
    }

    public static void runPerformanceTest() {
        LocalPlayer player = Minecraft.getInstance().player;
        if (player == null) return;
        ClientLevel level = Minecraft.getInstance().level;
        if (level == null) return;

        double px = player.getX();
        double py = player.getY();
        double pz = player.getZ();
        float radius = 20f;

        BlockState[] blocks = {
                Blocks.CYAN_STAINED_GLASS.defaultBlockState(),
                Blocks.DIAMOND_BLOCK.defaultBlockState(),
                Blocks.FURNACE.defaultBlockState(),
                Blocks.OAK_LOG.defaultBlockState(),
                Blocks.STONE.defaultBlockState(),
        };

        for (int i = 0; i < COUNT; i++) {
            double theta = Math.random() * 2 * Math.PI;
            double phi = Math.acos(2 * Math.random() - 1);
            double r = radius * Math.cbrt(Math.random());
            double x = px + r * Math.sin(phi) * Math.cos(theta);
            double y = py + r * Math.cos(phi);
            double z = pz + r * Math.sin(phi) * Math.sin(theta);

            VfxEntity entity = new VfxEntity(DestinyModEntities.VFX_ENTITY.get(), level);
            entity.setPos(x, y, z);
            entity.setBlockState(blocks[i % blocks.length]);
            level.addEntity(entity);

            VfxAnimation anim = new VfxAnimation(
                    new Vector3fChannel(
                            new Vector3f(0, 0, 0),
                            new Vector3f(
                                    (float)(Math.random() * 4 - 2),
                                    (float)(Math.random() * 4),
                                    (float)(Math.random() * 4 - 2)
                            ),
                            Easing.EASE_OUT_QUAD
                    ),
                    new Vector3fChannel(
                            new Vector3f(0.5f, 0.5f, 0.5f),
                            new Vector3f(
                                    (float)(Math.random() * 1.5f + 0.5f),
                                    (float)(Math.random() * 1.5f + 0.5f),
                                    (float)(Math.random() * 1.5f + 0.5f)
                            ),
                            Easing.EASE_IN_QUAD
                    ),
                    new RotationDegreesChannel(
                            new Vector3f(0, 0, 0),
                            new Vector3f((float) (Math.random()*720), (float) (Math.random()*720), (float) (Math.random()*720)),
                            Easing.EASE_IN_QUAD
                    )
            );

            int duration = 1800 + (int)(Math.random() * 900);
            entity.playAnimation(anim, duration);
        }

        Minecraft.getInstance().player.sendSystemMessage(Component.literal("Spawned " + COUNT + " VFX entities"));
    }
}