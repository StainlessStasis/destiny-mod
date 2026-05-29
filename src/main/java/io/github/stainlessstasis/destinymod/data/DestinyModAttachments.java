package io.github.stainlessstasis.destinymod.data;

import io.github.stainlessstasis.destinymod.DestinyMod;
import io.github.stainlessstasis.destinymod.destiny_classes.DestinyElement;
import io.github.stainlessstasis.destinymod.destiny_classes.ability.cooldown.AbilityCooldowns;
import io.github.stainlessstasis.destinymod.destiny_classes.debuff.Scorch;
import io.github.stainlessstasis.destinymod.destiny_classes.player_equipped.PlayerSubclassData;
import net.minecraft.world.entity.Entity;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

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

    public static final Supplier<AttachmentType<Scorch>> SCORCH = ATTACHMENTS.register(
            "scorch",
            () -> AttachmentType.builder(Scorch::new)
                    .serialize(Scorch.CODEC)
                    .copyOnDeath()
                    .sync(Scorch.STREAM_CODEC)
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
