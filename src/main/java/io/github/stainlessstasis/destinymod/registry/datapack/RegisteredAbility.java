package io.github.stainlessstasis.destinymod.registry.datapack;

import com.mojang.serialization.Codec;
import io.github.stainlessstasis.destinymod.destiny_combat.ability.Ability;
import io.github.stainlessstasis.destinymod.registry.DestinyRegistries;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.Holder;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;

import java.util.Optional;

public record RegisteredAbility(ResourceKey<Ability> resourceKey) {
    public Ability get(RegistryAccess access) {
        return access.lookupOrThrow(DestinyRegistries.ABILITY_REGISTRY_KEY).getValueOrThrow(resourceKey);
    }
    public Ability get(Level level) {
        return get(level.registryAccess());
    }
    public Ability get(Entity entity) {
        return get(entity.level().registryAccess());
    }

    public Optional<Ability> getOptional(RegistryAccess access) {
        return access.lookupOrThrow(DestinyRegistries.ABILITY_REGISTRY_KEY)
                .get(resourceKey)
                .map(Holder.Reference::value);
    }
    public Optional<Ability> getOptional(Level level) {
        return getOptional(level.registryAccess());
    }
    public Optional<Ability> getOptional(Entity entity) {
        return getOptional(entity.level().registryAccess());
    }

    public Identifier getID() {
        return resourceKey.identifier();
    }
    public String getName() {
        return resourceKey.identifier().getPath();
    }

    public static Codec<RegisteredAbility> CODEC = ResourceKey.codec(DestinyRegistries.ABILITY_REGISTRY_KEY)
            .xmap(RegisteredAbility::new, RegisteredAbility::resourceKey);
    public static StreamCodec<ByteBuf, RegisteredAbility> STREAM_CODEC = ResourceKey.streamCodec(DestinyRegistries.ABILITY_REGISTRY_KEY)
            .map(RegisteredAbility::new, RegisteredAbility::resourceKey);
}
