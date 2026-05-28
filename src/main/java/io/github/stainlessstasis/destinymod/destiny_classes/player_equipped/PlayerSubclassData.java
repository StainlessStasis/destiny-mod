package io.github.stainlessstasis.destinymod.destiny_classes.player_equipped;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import io.github.stainlessstasis.destinymod.destiny_classes.Subclass;
import io.github.stainlessstasis.destinymod.destiny_classes.ability.AbilityType;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

import java.util.HashMap;
import java.util.Map;

public class PlayerSubclassData {
    private final HashMap<Subclass, SubclassLoadout> subclassLoadouts = new HashMap<>();

    public static final MapCodec<PlayerSubclassData> CODEC = Codec.unboundedMap(Subclass.CODEC, SubclassLoadout.CODEC)
            .xmap(PlayerSubclassData::new, loadout -> loadout.subclassLoadouts)
            .fieldOf("subclass_data");

    public static final StreamCodec<RegistryFriendlyByteBuf, PlayerSubclassData> STREAM_CODEC =
            ByteBufCodecs.map(HashMap::new, Subclass.STREAM_CODEC, SubclassLoadout.STREAM_CODEC)
                    .map(PlayerSubclassData::new, data -> data.subclassLoadouts);

    private PlayerSubclassData(Map<Subclass, SubclassLoadout> subclassLoadouts) {
        this.subclassLoadouts.putAll(subclassLoadouts);
    }

    public PlayerSubclassData() {}

    public SubclassLoadout getSubclassLoadout(Subclass subclass) {
        return this.subclassLoadouts.computeIfAbsent(subclass, _ -> SubclassLoadout.NONE);
    }

    public void putSubclassLoadout(Subclass subclass, SubclassLoadout loadout) {
        this.subclassLoadouts.put(subclass, loadout);
    }
}
