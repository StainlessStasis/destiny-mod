package io.github.stainlessstasis.destinymod.data;

import com.mojang.serialization.Codec;
import io.github.stainlessstasis.destinymod.DestinyMod;
import io.github.stainlessstasis.destinymod.destiny_combat.ability.cooldown.AbilityCooldowns;
import io.github.stainlessstasis.destinymod.registry.datapack.RegisteredAspect;
import io.github.stainlessstasis.destinymod.destiny_combat.status_effect.buff.SolInvictus;
import io.github.stainlessstasis.destinymod.destiny_combat.status_effect.debuff.MeltingPoint;
import io.github.stainlessstasis.destinymod.destiny_combat.status_effect.debuff.Scorch;
import io.github.stainlessstasis.destinymod.destiny_classes.player_equipped.PlayerSubclassData;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.world.entity.Entity;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Supplier;

public class DestinyModAttachments {
    private static final DeferredRegister<AttachmentType<?>> ATTACHMENTS = DeferredRegister.create(NeoForgeRegistries.ATTACHMENT_TYPES, DestinyMod.MODID);

    public static final Supplier<AttachmentType<AbilityCooldowns>> ABILITY_COOLDOWNS = ATTACHMENTS.register(
            "ability_cooldowns",
            () -> AttachmentType.builder(AbilityCooldowns::new)
                    .serialize(AbilityCooldowns.CODEC)
                    .copyOnDeath()
                    .sync(AbilityCooldowns.STREAM_CODEC)
                    .build()
    );

    public static final Supplier<AttachmentType<PlayerSubclassData>> PLAYER_SUBCLASS_DATA = ATTACHMENTS.register(
            "player_subclass_data",
            () -> AttachmentType.builder(PlayerSubclassData::new)
                    .serialize(PlayerSubclassData.CODEC)
                    .copyOnDeath()
                    .sync(PlayerSubclassData.STREAM_CODEC)
                    .build()
    );

    public static final Supplier<AttachmentType<Set<RegisteredAspect>>> PLAYER_UNLOCKED_ASPECTS = ATTACHMENTS.register(
            "player_unlocked_aspects",
            () -> AttachmentType.builder(() -> (Set<RegisteredAspect>) new HashSet<RegisteredAspect>())
                    .serialize(Codec.list(RegisteredAspect.CODEC)
                            .<Set<RegisteredAspect>>xmap(
                                    HashSet::new,
                                    ArrayList::new
                            )
                            .fieldOf("aspects_to_equip")
                    )
                    .copyOnDeath()
                    .sync(ByteBufCodecs.collection(HashSet::new, RegisteredAspect.STREAM_CODEC))
                    .build()
    );

    /**
     * NOT synced to clients. Use IS_SCORCH_ACTIVE
     */
    public static final Supplier<AttachmentType<Scorch>> SCORCH = ATTACHMENTS.register(
            "scorch",
            () -> AttachmentType.builder(Scorch::new)
                    .serialize(Scorch.CODEC)
                    .build()
    );
    public static final Supplier<AttachmentType<Boolean>> IS_SCORCH_ACTIVE = ATTACHMENTS.register(
            "is_scorch_active",
            () -> AttachmentType.builder(() -> false)
                    .serialize(Codec.BOOL.fieldOf("is_scorch_active"))
                    .sync(ByteBufCodecs.BOOL)
                    .build()
    );

    /**
     * NOT synced to clients. Use IS_SOL_INVICTUS_ACTIVE
     */
    public static final Supplier<AttachmentType<SolInvictus>> SOL_INVICTUS = ATTACHMENTS.register(
            "sol_invictus",
            () -> AttachmentType.builder(SolInvictus::new)
                    .serialize(SolInvictus.CODEC)
                    .build()
    );
    public static final Supplier<AttachmentType<Boolean>> IS_SOL_INVICTUS_ACTIVE = ATTACHMENTS.register(
            "is_sol_invictus_active",
            () -> AttachmentType.builder(() -> false)
                    .serialize(Codec.BOOL.fieldOf("is_sol_invictus_active"))
                    .sync(ByteBufCodecs.BOOL)
                    .build()
    );

    /**
     * NOT synced to clients. Use IS_MELTING_POINT_ACTIVE
     */
    public static final Supplier<AttachmentType<MeltingPoint>> MELTING_POINT = ATTACHMENTS.register(
            "melting_point",
            () -> AttachmentType.builder(MeltingPoint::new)
                    .serialize(MeltingPoint.CODEC)
                    .build()
    );
    public static final Supplier<AttachmentType<Boolean>> IS_MELTING_POINT_ACTIVE = ATTACHMENTS.register(
            "is_melting_point_active",
            () -> AttachmentType.builder(() -> false)
                    .serialize(Codec.BOOL.fieldOf("is_melting_point_active"))
                    .sync(ByteBufCodecs.BOOL)
                    .build()
    );


//    public static final Supplier<AttachmentType<DestinyElement>> ELEMENT = ATTACHMENTS.register(
//            "element",
//            () -> AttachmentType.builder(() -> DestinyElement.NONE)
//                    .serialize(DestinyElement.MAP_CODEC)
//                    .copyOnDeath()
//                    .sync(DestinyElement.STREAM_CODEC)
//                    .build()
//    );

    public static void register(IEventBus bus) {
        ATTACHMENTS.register(bus);
    }

    public static void clearAll(Entity entity) {
        for (var attachment : ATTACHMENTS.getEntries()) {
            entity.removeData(attachment.get());
        }
    }
}
