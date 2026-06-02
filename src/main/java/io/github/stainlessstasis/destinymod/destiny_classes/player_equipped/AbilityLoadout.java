package io.github.stainlessstasis.destinymod.destiny_classes.player_equipped;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.stainlessstasis.destinymod.data.DestinyModRegistries;
import io.github.stainlessstasis.destinymod.destiny_classes.ability.Abilities;
import io.github.stainlessstasis.destinymod.destiny_classes.ability.Ability;
import io.github.stainlessstasis.destinymod.destiny_classes.ability.AbilityType;
import io.github.stainlessstasis.destinymod.destiny_classes.ability.Aspect;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.player.Player;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public record AbilityLoadout(ResourceKey<Ability> ability, Set<Aspect> aspects) {
    public static final AbilityLoadout NONE = new AbilityLoadout(Abilities.NONE, Set.of());

    public static final Codec<AbilityLoadout> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            ResourceKey.codec(DestinyModRegistries.ABILITY_REGISTRY_KEY).fieldOf("ability").forGetter(AbilityLoadout::ability),
            Codec.list(Aspect.CODEC).fieldOf("aspects").forGetter(loadout -> new ArrayList<>(loadout.aspects()))
    ).apply(instance, (ability, list) -> new AbilityLoadout(ability, new HashSet<>(list))));

    public static final StreamCodec<ByteBuf, AbilityLoadout> STREAM_CODEC = StreamCodec.composite(
            ResourceKey.streamCodec(DestinyModRegistries.ABILITY_REGISTRY_KEY),
            AbilityLoadout::ability,
            Aspect.STREAM_CODEC.apply(ByteBufCodecs.collection(HashSet::new)),
            AbilityLoadout::aspects,
            AbilityLoadout::new
    );
}
