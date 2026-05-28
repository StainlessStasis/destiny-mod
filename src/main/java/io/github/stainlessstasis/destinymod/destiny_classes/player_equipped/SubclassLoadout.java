package io.github.stainlessstasis.destinymod.destiny_classes.player_equipped;

import com.mojang.serialization.Codec;
import io.github.stainlessstasis.destinymod.destiny_classes.ability.AbilityType;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

import java.util.HashMap;
import java.util.Map;

public class SubclassLoadout {
    public static final SubclassLoadout NONE;
    static {
        HashMap<AbilityType, AbilityLoadout> none = new HashMap<>();
        for (AbilityType type : AbilityType.values()) {
            none.put(type, AbilityLoadout.NONE);
        }
        NONE = new SubclassLoadout(none);
    }
    private final HashMap<AbilityType, AbilityLoadout> abilityLoadouts = new HashMap<>();

    public static final Codec<SubclassLoadout> CODEC = Codec.unboundedMap(AbilityType.CODEC, AbilityLoadout.CODEC)
            .xmap(SubclassLoadout::new, loadout -> loadout.abilityLoadouts);

    public static final StreamCodec<ByteBuf, SubclassLoadout> STREAM_CODEC =
            ByteBufCodecs.map(HashMap::new, ByteBufCodecs.fromCodec(AbilityType.CODEC), AbilityLoadout.STREAM_CODEC)
                    .map(SubclassLoadout::new, loadout -> loadout.abilityLoadouts);

    private SubclassLoadout(Map<AbilityType, AbilityLoadout> abilityLoadouts) {
        this.abilityLoadouts.putAll(abilityLoadouts);
    }

    public SubclassLoadout() {}

    public AbilityLoadout getAbility(AbilityType type) {
        return this.abilityLoadouts.computeIfAbsent(type, _ -> AbilityLoadout.NONE);
    }

    public void putAbilityLoadout(AbilityType type, AbilityLoadout loadout) {
        this.abilityLoadouts.put(type, loadout);
    }
}