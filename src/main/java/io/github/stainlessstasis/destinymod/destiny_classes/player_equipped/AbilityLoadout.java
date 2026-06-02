package io.github.stainlessstasis.destinymod.destiny_classes.player_equipped;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.stainlessstasis.destinymod.destiny_combat.ability.Abilities;
import io.github.stainlessstasis.destinymod.destiny_combat.ability.Aspect;
import io.github.stainlessstasis.destinymod.destiny_combat.ability.RegisteredAbility;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public record AbilityLoadout(RegisteredAbility ability, Set<Aspect> aspects) {
    public static final AbilityLoadout NONE = new AbilityLoadout(Abilities.NONE, Set.of());

    public static final Codec<AbilityLoadout> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            RegisteredAbility.CODEC.fieldOf("ability").forGetter(AbilityLoadout::ability),
            Codec.list(Aspect.CODEC).fieldOf("aspects").forGetter(loadout -> new ArrayList<>(loadout.aspects()))
    ).apply(instance, (ability, list) -> new AbilityLoadout(ability, new HashSet<>(list))));

    public static final StreamCodec<ByteBuf, AbilityLoadout> STREAM_CODEC = StreamCodec.composite(
            RegisteredAbility.STREAM_CODEC,
            AbilityLoadout::ability,
            Aspect.STREAM_CODEC.apply(ByteBufCodecs.collection(HashSet::new)),
            AbilityLoadout::aspects,
            AbilityLoadout::new
    );
}
