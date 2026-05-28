package io.github.stainlessstasis.destinymod.destiny_classes;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.network.codec.StreamCodec;

public record Subclass(Component title, DestinyClass destinyClass, DestinyElement destinyElement) {
    public static final Codec<Subclass> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            ComponentSerialization.CODEC.fieldOf("title").forGetter(Subclass::title),
            DestinyClass.CODEC.fieldOf("class").forGetter(Subclass::destinyClass),
            DestinyElement.CODEC.fieldOf("element").forGetter(Subclass::destinyElement)
    ).apply(instance, Subclass::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, Subclass> STREAM_CODEC = StreamCodec.composite(
            ComponentSerialization.STREAM_CODEC, Subclass::title,
            DestinyClass.STREAM_CODEC, Subclass::destinyClass,
            DestinyElement.STREAM_CODEC, Subclass::destinyElement,
            Subclass::new
    );
}
