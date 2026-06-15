package io.github.stainlessstasis.destinymod.destiny_classes.player_equipped;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.stainlessstasis.destinymod.data.DestinyModAttachments;
import io.github.stainlessstasis.destinymod.destiny_classes.Subclass;
import io.github.stainlessstasis.destinymod.destiny_classes.Subclasses;
import io.github.stainlessstasis.destinymod.destiny_combat.ability.*;
import io.github.stainlessstasis.destinymod.registry.datapack.RegisteredAbility;
import io.github.stainlessstasis.destinymod.registry.datapack.RegisteredAspect;
import io.github.stainlessstasis.destinymod.registry.property.AbilityProperties;
import io.github.stainlessstasis.destinymod.registry.property.AbilityProperty;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.entity.player.Player;

import java.util.*;

public class PlayerSubclassData {
    private Subclass equippedSubclass = Subclasses.SUNBREAKER; // default value cus you cant really *not* have a subclassID equipped
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

    public static void replaceAbility(Player player, AbilityType abilityType, RegisteredAbility newAbility) {
        var instance = getInstance(player);
        instance.getSubclassLoadout().replaceAbility(abilityType, newAbility);
        instance.sync(player);
    }

    public static AbilityLoadout getAbilityLoadout(Player player, AbilityType abilityType) {
        return getSubclassLoadout(player).getAbilityLoadout(abilityType);
    }

    public static RegisteredAbility getRegisteredAbility(Player player, AbilityType abilityType) {
        return getSubclassLoadout(player).getRegisteredAbility(abilityType);
    }

    public static Ability getAbility(Player player, AbilityType abilityType) {
        return getSubclassLoadout(player).getAbility(player, abilityType);
    }

    public static List<RegisteredAspect> getRegisteredAspectsForAbility(Player player, AbilityType abilityType) {
        return getSubclassLoadout(player).getRegisteredAspectsForAbility(abilityType);
    }

    public static List<Aspect> getAspectsForAbility(Player player, AbilityType abilityType) {
        return getSubclassLoadout(player).getAspectsForAbility(player, abilityType);
    }

    public static List<RegisteredAspect> getAllEquippedRegisteredAspects(Player player) {
        List<RegisteredAspect> allAspects = new ArrayList<>();
        for (AbilityType abilityType : AbilityType.getCombatTypes()) {
            allAspects.addAll(getRegisteredAspectsForAbility(player, abilityType));
        }
        return allAspects;
    }

    public static List<Aspect> getAllEquippedAspects(Player player) {
        return getAllEquippedRegisteredAspects(player).stream()
                .map(registeredAspect -> registeredAspect.get(player))
                .toList();
    }

    public static int getMaxAspectsEquippable(Player player) {
        return getSubclassLoadout(player).getMaxAspectsEquippable();
    }

    public static void setMaxAspectsEquippable(Player player, int newMax) {
        var instance = getInstance(player);
        instance.getSubclassLoadout().setMaxAspectsEquippable(newMax);
        instance.sync(player);
    }

    public static void equipAllAspects(Player player, List<RegisteredAspect> aspects) {
        var instance = getInstance(player);
        instance.getSubclassLoadout().equipAllAspects(player, aspects);
        instance.sync(player);
    }

    public static boolean isAspectEquipped(Player player, RegisteredAspect aspect) {
        return getAllEquippedRegisteredAspects(player).contains(aspect);
    }

    public static boolean hasUnlockedAspect(Player player, RegisteredAspect aspect) {
        return player.getData(DestinyModAttachments.PLAYER_UNLOCKED_ASPECTS).contains(aspect);
    }

    public static boolean canEquipAspect(Player player, RegisteredAspect aspect) {
        if (!hasUnlockedAspect(player, aspect)) return false;
        return getAllEquippedRegisteredAspects(player).size() < getMaxAspectsEquippable(player);
    }

    public static <T extends AbilityProperty> Optional<T> getEquippedProperty(Player player, Class<T> propertyClass) {
        String targetTypeId = AbilityProperties.getPropertyId(propertyClass);
        if (targetTypeId == null) return Optional.empty();

        return getAllProperties(player).stream()
                .filter(prop -> prop.type().equals(targetTypeId))
                .map(propertyClass::cast)
                .findFirst();
    }

    public static List<AbilityProperty> getAllProperties(Player player) {
        List<AbilityProperty> activeProperties = new ArrayList<>();

        for (Aspect aspect : getAllEquippedAspects(player)) {
            activeProperties.addAll(Arrays.asList(aspect.properties()));
        }

        return activeProperties;
    }

    public static RegisteredAbility getRegisteredMelee(Player player) {
        return getSubclassLoadout(player).getRegisteredMelee();
    }
    public static RegisteredAbility getRegisteredGrenade(Player player) {
        return getSubclassLoadout(player).getRegisteredGrenade();
    }
    public static RegisteredAbility getRegisteredClassAbility(Player player) {
        return getSubclassLoadout(player).getRegisteredClassAbility();
    }
    public static RegisteredAbility getRegisteredSuper(Player player) {
        return getSubclassLoadout(player).getRegisteredSuper();
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
