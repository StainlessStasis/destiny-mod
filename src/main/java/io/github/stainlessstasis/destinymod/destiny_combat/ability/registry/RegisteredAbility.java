package io.github.stainlessstasis.destinymod.destiny_combat.ability.registry;

import com.mojang.serialization.Codec;
import io.github.stainlessstasis.destinymod.data.DestinyModRegistries;
import io.github.stainlessstasis.destinymod.destiny_combat.ability.Ability;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;

public record RegisteredAbility(ResourceKey<Ability> resourceKey) {
    public Ability get(RegistryAccess access) {
        return access.lookupOrThrow(DestinyModRegistries.ABILITY_REGISTRY_KEY).getValueOrThrow(resourceKey);
    }

    public Ability get(Level level) {
        return get(level.registryAccess());
    }

    public Ability get(Entity entity) {
        return get(entity.level().registryAccess());
    }

    public String getName() {
        return resourceKey.identifier().getPath();
    }

    public static Codec<RegisteredAbility> CODEC = ResourceKey.codec(DestinyModRegistries.ABILITY_REGISTRY_KEY)
            .xmap(RegisteredAbility::new, RegisteredAbility::resourceKey);
    public static StreamCodec<ByteBuf, RegisteredAbility> STREAM_CODEC = ResourceKey.streamCodec(DestinyModRegistries.ABILITY_REGISTRY_KEY)
            .map(RegisteredAbility::new, RegisteredAbility::resourceKey);
}
