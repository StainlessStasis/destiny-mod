package io.github.stainlessstasis.destinymod.item_skin;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.Identifier;

public record ItemSkinComponent(Identifier skinID) {
    public static final Codec<ItemSkinComponent> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                    Identifier.CODEC.fieldOf("skin_id").forGetter(ItemSkinComponent::skinID)
            ).apply(instance, ItemSkinComponent::new)
    );
    public static final StreamCodec<ByteBuf, ItemSkinComponent> STREAM_CODEC = StreamCodec.composite(
            Identifier.STREAM_CODEC, ItemSkinComponent::skinID,
            ItemSkinComponent::new
    );
}
