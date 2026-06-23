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
                    .updateInterval(1)
                    .build(ResourceKey.create(Registries.ENTITY_TYPE, DestinyMod.id("hammer_of_sol")))

    );

    public static final Supplier<EntityType<@NotNull SunspotEntity>> SUNSPOT = ENTITY_TYPES.register(
            "sunspot",
            () -> EntityType.Builder.of(
                            SunspotEntity::createDefault,
                            MobCategory.MISC
                    )
                    .sized(3f, 2.5f)
                    .noSave()
                    .clientTrackingRange(8)
                    .build(ResourceKey.create(Registries.ENTITY_TYPE, DestinyMod.id("sunspot")))
    );

    public static final Supplier<EntityType<@NotNull ThrownGrenadeEntity>> THROWN_GRENADE = ENTITY_TYPES.register(
            "thrown_grenade",
            () -> EntityType.Builder.of(
                            ThrownGrenadeEntity::createDefault,
                            MobCategory.MISC
                    )
                    .sized(0.3f, 0.3f)
                    .noSave()
                    .clientTrackingRange(8)
                    .build(ResourceKey.create(Registries.ENTITY_TYPE, DestinyMod.id("thrown_grenade")))

    );

    public static final Supplier<EntityType<@NotNull ThermiteGrenadeEntity>> THERMITE_GRENADE = ENTITY_TYPES.register(
            "thermite_grenade",
            () -> EntityType.Builder.of(
                            ThermiteGrenadeEntity::createDefault,
                            MobCategory.MISC
                    )
                    .sized(0.5f, 0.5f)
                    .noSave()
                    .clientTrackingRange(8)
                    .build(ResourceKey.create(Registries.ENTITY_TYPE, DestinyMod.id("thermite_grenade")))

    );

    public static final Supplier<EntityType<@NotNull BarricadeEntity>> BARRICADE = ENTITY_TYPES.register(
            "barricade",
            () -> EntityType.Builder.of(
                            BarricadeEntity::createDefault,
                            MobCategory.MISC
                    )
                    .sized(3.25f, 2.1f)
                    .noSave()
                    .clientTrackingRange(8)
                    .build(ResourceKey.create(Registries.ENTITY_TYPE, DestinyMod.id("barricade")))

    );

    public static void register(IEventBus eventBus) {
        ENTITY_TYPES.register(eventBus);
    }
}
