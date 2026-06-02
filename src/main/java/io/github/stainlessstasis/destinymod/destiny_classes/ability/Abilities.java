package io.github.stainlessstasis.destinymod.destiny_classes.ability;

import com.mojang.serialization.Codec;
import io.github.stainlessstasis.destinymod.DestinyMod;
import io.github.stainlessstasis.destinymod.data.DestinyModRegistries;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;

public class Abilities {
    private static RegisteredAbility register(String name) {
        return new RegisteredAbility(ResourceKey.create(DestinyModRegistries.ABILITY_REGISTRY_KEY, DestinyMod.id(name)));
    }

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

        public static Codec<RegisteredAbility> CODEC = ResourceKey.codec(DestinyModRegistries.ABILITY_REGISTRY_KEY)
                .xmap(Abilities.RegisteredAbility::new, Abilities.RegisteredAbility::resourceKey);
        public static StreamCodec<ByteBuf, RegisteredAbility> STREAM_CODEC = ResourceKey.streamCodec(DestinyModRegistries.ABILITY_REGISTRY_KEY)
                .map(Abilities.RegisteredAbility::new, Abilities.RegisteredAbility::resourceKey);
    }

    public static final RegisteredAbility NONE = register("none");
    public static final RegisteredAbility THROWING_HAMMER = register("throwing_hammer");
    public static final RegisteredAbility SUNSPOT = register("sunspot");
}
