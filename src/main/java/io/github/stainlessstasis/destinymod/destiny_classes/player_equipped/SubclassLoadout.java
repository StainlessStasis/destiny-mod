package io.github.stainlessstasis.destinymod.destiny_classes.player_equipped;

import com.mojang.serialization.Codec;
import io.github.stainlessstasis.destinymod.destiny_combat.ability.Abilities;
import io.github.stainlessstasis.destinymod.destiny_combat.ability.Ability;
import io.github.stainlessstasis.destinymod.destiny_combat.ability.AbilityType;
import io.github.stainlessstasis.destinymod.destiny_combat.ability.Aspect;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.entity.player.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

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

    public AbilityLoadout getAbilityLoadout(AbilityType abilityType) {
        return this.abilityLoadouts.get(abilityType);
    }

    void putAbilityLoadout(AbilityType abilityType, AbilityLoadout loadout) {
        this.abilityLoadouts.put(abilityType, loadout);
    }

    public Ability getAbility(Player player, AbilityType abilityType) {
        return getAbilityLoadout(abilityType).ability().get(player);
    }

    void replaceAbility(AbilityType abilityType, Abilities.RegisteredAbility newAbility) {
        var currentLoadout = getAbilityLoadout(abilityType);
        AbilityLoadout newLoadout = new AbilityLoadout(newAbility, currentLoadout.aspects());
        putAbilityLoadout(abilityType, newLoadout);
    }

    public Set<Aspect> getAspectsForAbility(AbilityType abilityType) {
        return getAbilityLoadout(abilityType).aspects();
    }

    public Ability getMelee(Player player) {
        return getAbility(player, AbilityType.MELEE);
    }

    public Ability getGrenade(Player player) {
        return getAbility(player, AbilityType.GRENADE);
    }

    public Ability getClassAbility(Player player) {
        return getAbility(player, AbilityType.CLASS_ABILITY);
    }

    public Ability getSuper(Player player) {
        return getAbility(player, AbilityType.SUPER);
    }
}