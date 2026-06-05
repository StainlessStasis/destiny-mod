package io.github.stainlessstasis.destinymod.destiny_classes.player_equipped;

import com.mojang.serialization.Codec;
import io.github.stainlessstasis.destinymod.destiny_combat.ability.*;
import io.github.stainlessstasis.destinymod.destiny_combat.ability.registry.RegisteredAbility;
import io.github.stainlessstasis.destinymod.destiny_combat.ability.registry.RegisteredAspect;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.entity.player.Player;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class SubclassLoadout {
    public static final SubclassLoadout NONE;
    static {
        HashMap<AbilityType, AbilityLoadout> none = new HashMap<>();
        for (AbilityType type : AbilityType.values()) {
            none.put(type, AbilityLoadout.NONE);
        }
        NONE = new SubclassLoadout(none, 0);
    }

    private final HashMap<AbilityType, AbilityLoadout> abilityLoadouts = new HashMap<>();
    private int maxAspectsEquippable = 4;

    public static final Codec<SubclassLoadout> CODEC = com.mojang.serialization.codecs.RecordCodecBuilder.create(instance -> instance.group(
            Codec.unboundedMap(AbilityType.CODEC, AbilityLoadout.CODEC).fieldOf("abilityLoadouts").forGetter(loadout -> loadout.abilityLoadouts),
            Codec.INT.fieldOf("maxAspects").orElse(4).forGetter(loadout -> loadout.maxAspectsEquippable)
    ).apply(instance, SubclassLoadout::new));

    public static final StreamCodec<ByteBuf, SubclassLoadout> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.map(HashMap::new, ByteBufCodecs.fromCodec(AbilityType.CODEC), AbilityLoadout.STREAM_CODEC),
            loadout -> loadout.abilityLoadouts,
            ByteBufCodecs.VAR_INT,
            loadout -> loadout.maxAspectsEquippable,
            SubclassLoadout::new
    );

    private SubclassLoadout(Map<AbilityType, AbilityLoadout> abilityLoadouts, int maxAspectsEquippable) {
        this.abilityLoadouts.putAll(abilityLoadouts);
        this.maxAspectsEquippable = maxAspectsEquippable;
    }

    public SubclassLoadout() {}

    public AbilityLoadout getAbilityLoadout(AbilityType abilityType) {
        return this.abilityLoadouts.get(abilityType);
    }

    public int getMaxAspectsEquippable() {
        return this.maxAspectsEquippable;
    }

    public void setMaxAspectsEquippable(int newMax) {
        this.maxAspectsEquippable = newMax;
    }

    void putAbilityLoadout(AbilityType abilityType, AbilityLoadout loadout) {
        this.abilityLoadouts.put(abilityType, loadout);
    }

    public RegisteredAbility getRegisteredAbility(AbilityType abilityType) {
        return getAbilityLoadout(abilityType).ability();
    }

    public Ability getAbility(Player player, AbilityType abilityType) {
        return getRegisteredAbility(abilityType).get(player);
    }

    void replaceAbility(AbilityType abilityType, RegisteredAbility newAbility) {
        var currentLoadout = getAbilityLoadout(abilityType);
        AbilityLoadout newLoadout = new AbilityLoadout(newAbility, currentLoadout.aspects());
        putAbilityLoadout(abilityType, newLoadout);
    }

    public List<RegisteredAspect> getRegisteredAspectsForAbility(AbilityType abilityType) {
        return getAbilityLoadout(abilityType).aspects();
    }

    public List<Aspect> getAspectsForAbility(Player player, AbilityType abilityType) {
        return getRegisteredAspectsForAbility(abilityType).stream()
                .map(registeredAspect -> registeredAspect.get(player))
                .collect(Collectors.toList());
    }

    public RegisteredAbility getRegisteredMelee() {
        return getRegisteredAbility(AbilityType.MELEE);
    }
    public RegisteredAbility getRegisteredGrenade() {
        return getRegisteredAbility(AbilityType.GRENADE);
    }
    public RegisteredAbility getRegisteredClassAbility() {
        return getRegisteredAbility(AbilityType.CLASS_ABILITY);
    }
    public RegisteredAbility getRegisteredSuper() {
        return getRegisteredAbility(AbilityType.SUPER);
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