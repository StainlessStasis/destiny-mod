package io.github.stainlessstasis.destinymod.api.block_display_fx.client;

import io.github.stainlessstasis.destinymod.api.block_display_fx.VfxEntity;
import io.github.stainlessstasis.destinymod.api.block_display_fx.channel.RotationChannel;
import io.github.stainlessstasis.destinymod.api.block_display_fx.channel.Vector3fChannel;
import io.github.stainlessstasis.destinymod.api.block_display_fx.channel.VfxAnimation;
import io.github.stainlessstasis.destinymod.api.block_display_fx.easing.Easing;
import io.github.stainlessstasis.destinymod.entity.DestinyModEntities;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.level.block.Blocks;
import org.joml.Quaternionf;
import org.joml.Vector3f;

public class AnimationTest {
    public static void run() {
        LocalPlayer player = Minecraft.getInstance().player;
        if (player == null) return;
        ClientLevel level = Minecraft.getInstance().level;
        if (level == null) return;

        VfxEntity entity = new VfxEntity(DestinyModEntities.VFX_ENTITY.get(), level);
        entity.setPos(player.getX(), player.getY() + 1, player.getZ());
        level.addEntity(entity);

        VfxAnimation anim = new VfxAnimation(
                new Vector3fChannel(
                        new Vector3f(0, 0, 0),
                        new Vector3f(0, 2, 0),
                        Easing.EASE_OUT_QUAD
                ),
                new Vector3fChannel(
                        new Vector3f(1, 1, 1),
                        new Vector3f(0.25f, 0.25f, 0.25f),
                        Easing.EASE_IN_QUAD
                ),
                new RotationChannel(
                        new Quaternionf(),
                        new Quaternionf().rotationY((float) Math.toRadians(270f)),
                        Easing.EASE_IN_QUAD
                )
        );

        entity.setBlockState(Blocks.DIAMOND_BLOCK.defaultBlockState());
        entity.playAnimation(anim, 20);
    }
}
