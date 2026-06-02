package io.github.stainlessstasis.destinymod.destiny_classes.player_equipped;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.stainlessstasis.destinymod.data.DestinyModAttachments;
import io.github.stainlessstasis.destinymod.destiny_classes.Subclass;
import io.github.stainlessstasis.destinymod.destiny_classes.Subclasses;
import io.github.stainlessstasis.destinymod.destiny_combat.ability.Abilities;
import io.github.stainlessstasis.destinymod.destiny_combat.ability.Ability;
import io.github.stainlessstasis.destinymod.destiny_combat.ability.AbilityType;
import io.github.stainlessstasis.destinymod.destiny_combat.ability.Aspect;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.entity.player.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class PlayerSubclassData {
    private Subclass equippedSubclass = Subclasses.SUNBREAKER; // default value cus you cant really *not* have a subclass equipped
    private Map<Subclass, SubclassLoadout> subclassLoadouts = new HashMap<>();

    public static final MapCodec<PlayerSubclassData> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            Subclass.CODEC.fieldOf("equipped_subclass").forGetter(PlayerSubclassData::getEquippedSubclass),
            Codec.unboundedMap(Subclass.CODEC, SubclassLoadout.CODEC).fieldOf("loadouts").forGetter(data -> data.subclassLoadouts)
    ).apply(instance, PlayerSubclassData::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, PlayerSubclassData> STREAM_CODEC = StreamCodec.composite(
            Subclass.STREAM_CODEC, PlayerSubclassData::getEquippedSubclass,
            ByteBufCodecs.map(HashMap::new, Subclass.STREAM_CODEC, SubclassLoadout.STREAM_CODEC), data -> data.subclassLoadouts,
            PlayerSubclassData::new
    );

    public PlayerSubclassData(Subclass subclass, Map<Subclass, SubclassLoadout> subclassLoadouts) {
        this.equippedSubclass = subclass;
        this.subclassLoadouts = new HashMap<>(subclassLoadouts);
    }

    public PlayerSubclassData() {}

    public SubclassLoadout getSubclassLoadout(Subclass subclass) {
        return this.subclassLoadouts.computeIfAbsent(subclass, _ -> SubclassLoadout.NONE);
    }

    public SubclassLoadout getSubclassLoadout() {
        return getSubclassLoadout(getEquippedSubclass());
    }

    public void putSubclassLoadout(Subclass subclass, SubclassLoadout loadout) {
        this.subclassLoadouts.put(subclass, loadout);
    }

    void sync(Player player) {
        player.setData(DestinyModAttachments.PLAYER_SUBCLASS_DATA, this);
    }

    public static PlayerSubclassData getInstance(Player player) {
        return player.getData(DestinyModAttachments.PLAYER_SUBCLASS_DATA);
    }

    public static Subclass getEquippedSubclass(Player player) {
        return getInstance(player).getEquippedSubclass();
    }

    public Subclass getEquippedSubclass() {
        return equippedSubclass;
    }

    public static void setEquippedSubclass(Player player, Subclass equippedSubclass) {
        var instance = getInstance(player);
        instance.equippedSubclass = equippedSubclass;
        instance.sync(player);
    }

    public static SubclassLoadout getSubclassLoadout(Player player, Subclass subclass) {
        return getInstance(player).getSubclassLoadout(subclass);
    }

    public static SubclassLoadout getSubclassLoadout(Player player) {
        var instance = getInstance(player);
        return instance.getSubclassLoadout(instance.getEquippedSubclass());
    }

    public static void replaceAbility(Player player, AbilityType abilityType, Abilities.RegisteredAbility newAbility) {
        var instance = getInstance(player);
        instance.getSubclassLoadout().replaceAbility(abilityType, newAbility);
        instance.sync(player);
    }

    public static AbilityLoadout getAbilityLoadout(Player player, AbilityType abilityType) {
        return getSubclassLoadout(player).getAbilityLoadout(abilityType);
    }

    public static Ability getAbility(Player player, AbilityType abilityType) {
        return getSubclassLoadout(player).getAbility(player, abilityType);
    }

    public static Set<Aspect> getAspectsForAbility(Player player, AbilityType abilityType) {
        return getSubclassLoadout(player).getAspectsForAbility(abilityType);
    }

    public static Ability getMelee(Player player) {
        return getSubclassLoadout(player).getMelee(player);
    }

    public static Ability getGrenade(Player player) {
        return getSubclassLoadout(player).getGrenade(player);
    }

    public static Ability getClassAbility(Player player) {
        return getSubclassLoadout(player).getClassAbility(player);
    }

    public static Ability getSuper(Player player) {
        return getSubclassLoadout(player).getSuper(player);
    }
}
