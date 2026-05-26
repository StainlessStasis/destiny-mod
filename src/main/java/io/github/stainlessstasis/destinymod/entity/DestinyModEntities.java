package io.github.stainlessstasis.destinymod.entity;

import io.github.stainlessstasis.destinymod.DestinyMod;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.jetbrains.annotations.NotNull;

import java.util.function.Supplier;

public class DestinyModEntities {
    private static final DeferredRegister.Entities ENTITY_TYPES = DeferredRegister.createEntities(DestinyMod.MODID);
    public static final Supplier<EntityType<@NotNull BonkHammerEntity>> HAMMER_OF_SOL = ENTITY_TYPES.register(
            "hammer_of_sol",
            () -> EntityType.Builder.of(
                            BonkHammerEntity::createDefault,
                            MobCategory.MISC
                    )
                    .sized(0.5f, 0.5f)
                    .noSave()
                    .clientTrackingRange(8)
                    .updateInterval(3)
                    .build(ResourceKey.create(
                            Registries.ENTITY_TYPE,
                            Identifier.fromNamespaceAndPath(DestinyMod.MODID, "hammer_of_sol")
                    ))
    );

    public static void register(IEventBus eventBus) {
        ENTITY_TYPES.register(eventBus);
    }
}
