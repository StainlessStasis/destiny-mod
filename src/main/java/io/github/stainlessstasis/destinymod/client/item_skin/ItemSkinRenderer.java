package io.github.stainlessstasis.destinymod.client.item_skin;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.special.SpecialModelRenderer;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3fc;
import org.jspecify.annotations.NonNull;

import java.util.function.Consumer;

public class ItemSkinRenderer implements SpecialModelRenderer<ItemSkinRenderArgument> {
    @Override
    public void submit(
            @Nullable ItemSkinRenderArgument arg,
            @NonNull PoseStack poseStack,
            @NonNull SubmitNodeCollector submitNodeCollector,
            int lightCoords,
            int overlayCoords,
            boolean hasFoil,
            int outlineColor
    ) {
        if (arg == null) return;
        var entry = ClientItemSkins.get(arg.skin().skinID());
        if (entry == null) return;

        ItemSkinDispatcher.renderFromSpecial(arg, entry, poseStack, submitNodeCollector, lightCoords);
    }

    @Override
    public void getExtents(@NonNull Consumer<Vector3fc> output) {}

    @Override
    public @Nullable ItemSkinRenderArgument extractArgument(@NonNull ItemStack stack) {
        return null;
    }
}
