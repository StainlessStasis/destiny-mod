package io.github.stainlessstasis.destinymod.data;

import io.github.stainlessstasis.destinymod.DestinyMod;
import io.github.stainlessstasis.destinymod.destiny_classes.ability.Abilities;
import io.github.stainlessstasis.destinymod.destiny_classes.ability.Ability;
import io.github.stainlessstasis.destinymod.destiny_classes.ability.AbilityType;
import io.github.stainlessstasis.destinymod.destiny_classes.damage.DestinyModDamageTypes;
import net.minecraft.core.RegistrySetBuilder;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.damagesource.DamageEffects;
import net.minecraft.world.damagesource.DamageScaling;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.damagesource.DeathMessageType;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.data.event.GatherDataEvent;

@EventBusSubscriber
public class DestinyModDatagen {
    @SubscribeEvent
    public static void onGatherData(GatherDataEvent.Client event) {
        event.createDatapackRegistryObjects(new RegistrySetBuilder()
                // ABILITIES
                .add(DestinyModRegistries.ABILITY_REGISTRY_KEY, bootstrap -> {
                    bootstrap.register(Abilities.NONE, new Ability(
                            AbilityType.MELEE, 0, 0
                    ));
                    bootstrap.register(Abilities.THROWING_HAMMER, new Ability(
                            AbilityType.MELEE, 200, 3
                    ));
                })

                // DAMAGE TYPES AND DAMAGE TYPE TAGS
                .add(Registries.DAMAGE_TYPE, bootstrap -> {
                    bootstrap.register(DestinyModDamageTypes.MELEE_ABILITY, new DamageType(
                            DestinyMod.MODID+".melee_ability",
                            DamageScaling.NEVER,
                            0.1f,
                            DamageEffects.HURT,
                            DeathMessageType.DEFAULT
                    ));
                    bootstrap.register(DestinyModDamageTypes.GRENADE_ABILITY, new DamageType(
                            DestinyMod.MODID+".grenade",
                            DamageScaling.NEVER,
                            0.1f,
                            DamageEffects.HURT,
                            DeathMessageType.DEFAULT
                    ));
                    bootstrap.register(DestinyModDamageTypes.CLASS_ABILITY, new DamageType(
                            DestinyMod.MODID+".class_ability",
                            DamageScaling.NEVER,
                            0.1f,
                            DamageEffects.HURT,
                            DeathMessageType.DEFAULT
                    ));
                    bootstrap.register(DestinyModDamageTypes.SUPER, new DamageType(
                            DestinyMod.MODID+".super",
                            DamageScaling.NEVER,
                            0.1f,
                            DamageEffects.HURT,
                            DeathMessageType.DEFAULT
                    ));
                    bootstrap.register(DestinyModDamageTypes.SCORCH, new DamageType(
                            DestinyMod.MODID+".scorch",
                            DamageScaling.NEVER,
                            0.1f,
                            DamageEffects.HURT,
                            DeathMessageType.DEFAULT
                    ));
                    bootstrap.register(DestinyModDamageTypes.IGNITION, new DamageType(
                            DestinyMod.MODID+".ignition",
                            DamageScaling.NEVER,
                            0.1f,
                            DamageEffects.HURT,
                            DeathMessageType.DEFAULT
                    ));
                    bootstrap.register(DestinyModDamageTypes.SUNSPOT, new DamageType(
                            DestinyMod.MODID+".sunspot",
                            DamageScaling.NEVER,
                            0.1f,
                            DamageEffects.HURT,
                            DeathMessageType.DEFAULT
                    ));
                })
        );

        event.createProvider((output, lookupProvider) -> new DMDamageTypeTagProvider(output, lookupProvider, DestinyMod.MODID));
    }
}
