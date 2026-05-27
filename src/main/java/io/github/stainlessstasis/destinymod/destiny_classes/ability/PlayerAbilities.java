package io.github.stainlessstasis.destinymod.destiny_classes.ability;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import io.github.stainlessstasis.destinymod.DestinyModAttachments;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.entity.player.Player;

import java.util.HashMap;
import java.util.Map;

public class PlayerAbilities {
    private final Map<AbilityType, Ability> equippedAbilities = new HashMap<>();

    public static final MapCodec<PlayerAbilities> CODEC = Codec.unboundedMap(AbilityType.CODEC, Ability.CODEC)
            .xmap(PlayerAbilities::new, playerAbilities -> playerAbilities.equippedAbilities)
            .fieldOf("equipped");

    public static final StreamCodec<ByteBuf, PlayerAbilities> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.map(HashMap::new, ByteBufCodecs.fromCodec(AbilityType.CODEC), Ability.STREAM_CODEC),
            playerAbilities -> playerAbilities.equippedAbilities,
            PlayerAbilities::new
    );

    private PlayerAbilities(Map<AbilityType, Ability> equippedAbilities) {
        this.equippedAbilities.putAll(equippedAbilities);
    }

    public PlayerAbilities() {}

    public void equip(Player player, AbilityType slot, Ability ability) {
        this.equippedAbilities.put(slot, ability);
        player.setData(DestinyModAttachments.PLAYER_ABILITIES, this);
    }

    public void unequip(Player player, AbilityType slot) {
        this.equippedAbilities.remove(slot);
        player.setData(DestinyModAttachments.PLAYER_ABILITIES, this);
    }

    public Ability getEquipped(AbilityType slot) {
        return this.equippedAbilities.getOrDefault(slot, Abilities.NONE);
    }

    public static PlayerAbilities get(Player player) {
        return player.getData(DestinyModAttachments.PLAYER_ABILITIES);
    }

    public static Ability getEquippedMelee(Player player) {
        return get(player).getEquipped(AbilityType.MELEE);
    }

}
