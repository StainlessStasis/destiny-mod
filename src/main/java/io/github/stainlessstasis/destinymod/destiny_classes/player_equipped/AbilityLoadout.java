package io.github.stainlessstasis.destinymod.destiny_classes.player_equipped;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.stainlessstasis.destinymod.destiny_combat.registry.Abilities;
import io.github.stainlessstasis.destinymod.destiny_combat.registry.RegisteredAbility;
import io.github.stainlessstasis.destinymod.destiny_combat.registry.RegisteredAspect;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

import java.util.ArrayList;
import java.util.List;

public record AbilityLoadout(RegisteredAbility ability, List<RegisteredAspect> aspects) {
    public static final AbilityLoadout NONE = new AbilityLoadout(Abilities.NONE, List.of());

    public static final Codec<AbilityLoadout> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            RegisteredAbility.CODEC.fieldOf("ability").forGetter(AbilityLoadout::ability),
            Codec.list(RegisteredAspect.CODEC).fieldOf("aspects").forGetter(loadout -> new ArrayList<>(loadout.aspects()))
    ).apply(instance, (ability, list) -> new AbilityLoadout(ability, new ArrayList<>(list))));

    public static final StreamCodec<ByteBuf, AbilityLoadout> STREAM_CODEC = StreamCodec.composite(
            RegisteredAbility.STREAM_CODEC,
            AbilityLoadout::ability,
            RegisteredAspect.STREAM_CODEC.apply(ByteBufCodecs.collection(ArrayList::new)),
            AbilityLoadout::aspects,
            AbilityLoadout::new
    );
}
