package io.github.stainlessstasis.destinymod.destiny_combat.registry;

import com.mojang.serialization.Codec;
import io.github.stainlessstasis.destinymod.data.DestinyModRegistries;
import io.github.stainlessstasis.destinymod.destiny_combat.ability.Ability;
import io.github.stainlessstasis.destinymod.destiny_combat.status_effect.StatusEffect;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;

public record RegisteredStatusEffect(ResourceKey<StatusEffect> resourceKey) {
    public StatusEffect get(RegistryAccess access) {
        return access.lookupOrThrow(DestinyModRegistries.STATUS_EFFECT_REGISTRY_KEY).getValueOrThrow(resourceKey);
    }

    public StatusEffect get(Level level) {
        return get(level.registryAccess());
    }

    public StatusEffect get(Entity entity) {
        return get(entity.level().registryAccess());
    }

    public String getName() {
        return resourceKey.identifier().getPath();
    }

    public static Codec<RegisteredStatusEffect> CODEC = ResourceKey.codec(DestinyModRegistries.STATUS_EFFECT_REGISTRY_KEY)
            .xmap(RegisteredStatusEffect::new, RegisteredStatusEffect::resourceKey);
    public static StreamCodec<ByteBuf, RegisteredStatusEffect> STREAM_CODEC = ResourceKey.streamCodec(DestinyModRegistries.STATUS_EFFECT_REGISTRY_KEY)
            .map(RegisteredStatusEffect::new, RegisteredStatusEffect::resourceKey);
}

