package io.github.stainlessstasis.destinymod.destiny_combat.ability.registry;

import com.mojang.serialization.Codec;
import io.github.stainlessstasis.destinymod.data.DestinyModRegistries;
import io.github.stainlessstasis.destinymod.destiny_combat.ability.Aspect;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;

public record RegisteredAspect(ResourceKey<Aspect> resourceKey) {
    public Aspect get(RegistryAccess access) {
        return access.lookupOrThrow(DestinyModRegistries.ASPECT_REGISTRY_KEY).getValueOrThrow(resourceKey);
    }

    public Aspect get(Level level) {
        return get(level.registryAccess());
    }

    public Aspect get(Entity entity) {
        return get(entity.level().registryAccess());
    }

    public String getName() {
        return resourceKey.identifier().getPath();
    }

    public static Codec<RegisteredAspect> CODEC = ResourceKey.codec(DestinyModRegistries.ASPECT_REGISTRY_KEY)
            .xmap(RegisteredAspect::new, RegisteredAspect::resourceKey);
    public static StreamCodec<ByteBuf, RegisteredAspect> STREAM_CODEC = ResourceKey.streamCodec(DestinyModRegistries.ASPECT_REGISTRY_KEY)
            .map(RegisteredAspect::new, RegisteredAspect::resourceKey);
}

