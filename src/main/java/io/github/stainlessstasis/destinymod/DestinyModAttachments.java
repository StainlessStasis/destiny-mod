package io.github.stainlessstasis.destinymod;

import io.github.stainlessstasis.destinymod.destiny_classes.ability.PlayerAbilities;
import io.github.stainlessstasis.destinymod.destiny_classes.ability.cooldown.AbilityCooldowns;
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

    public static final Supplier<AttachmentType<PlayerAbilities>> PLAYER_ABILITIES = ATTACHMENTS.register(
            "player_abilities",
            () -> AttachmentType.builder(PlayerAbilities::new)
                    .serialize(PlayerAbilities.CODEC)
                    .copyOnDeath()
                    .sync(PlayerAbilities.STREAM_CODEC)
                    .build()
    );

    public static void register(IEventBus bus) {
        ATTACHMENTS.register(bus);
    }

    public static void clearAll(Entity entity) {
        for (var attachment : ATTACHMENTS.getEntries()) {
            entity.removeData(attachment.get());
        }
    }
}
