package io.github.stainlessstasis.destinymod.data;

import io.github.stainlessstasis.destinymod.DestinyMod;
import io.github.stainlessstasis.destinymod.destiny_classes.DestinyElement;
import io.github.stainlessstasis.destinymod.destiny_classes.Subclasses;
import io.github.stainlessstasis.destinymod.destiny_combat.ability.Aspect;
import io.github.stainlessstasis.destinymod.registry.DestinyRegistries;
import io.github.stainlessstasis.destinymod.registry.datapack.Abilities;
import io.github.stainlessstasis.destinymod.destiny_combat.ability.Ability;
import io.github.stainlessstasis.destinymod.destiny_combat.ability.AbilityType;
import io.github.stainlessstasis.destinymod.registry.datapack.Aspects;
import io.github.stainlessstasis.destinymod.registry.datapack.StatusEffects;
import io.github.stainlessstasis.destinymod.registry.property.AbilityProperties;
import io.github.stainlessstasis.destinymod.destiny_combat.damage.DMDamageTypes;
import io.github.stainlessstasis.destinymod.destiny_combat.status_effect.StatusEffect;
import io.github.stainlessstasis.destinymod.registry.property.ability.GrenadePhysicsProperty;
import io.github.stainlessstasis.destinymod.registry.property.ability.ThermiteGrenadeProperty;
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
                .add(DestinyRegistries.ABILITY_REGISTRY_KEY, bootstrap -> {
                    bootstrap.register(Abilities.NONE.resourceKey(), new Ability(
                            AbilityType.PASSIVE, DestinyElement.NONE, -1, -1, -1f, -1f, -1
                    ));
                    bootstrap.register(Abilities.THROWING_HAMMER.resourceKey(), new Ability(
                            AbilityType.MELEE, DestinyElement.SOLAR, 200, 3, -1f, 7f, 25
                    ));
                    bootstrap.register(Abilities.THERMITE_GRENADE.resourceKey(), new Ability(
                            AbilityType.GRENADE, DestinyElement.SOLAR, 100, 5, -1f, 7f, 25,
                            new GrenadePhysicsProperty(
                                    0.6f, 0.5f, 0.0325f, 0.05f,
                                    true, false, true, -1
                            ),
                            AbilityProperties.THERMITE_GRENADE.get()
                    ));
                    bootstrap.register(Abilities.SOL_INVICTUS.resourceKey(), new Ability(
                            AbilityType.PASSIVE, DestinyElement.NONE, -1, -1, -1, -1, -1
                    ));
                    bootstrap.register(Abilities.SUNSPOT.resourceKey(), new Ability(
                            AbilityType.PASSIVE, DestinyElement.SOLAR, -1, -1, 0.3f, 1f, 5
                    ));
                    bootstrap.register(Abilities.IGNITION.resourceKey(), new Ability(
                            AbilityType.PASSIVE, DestinyElement.SOLAR, -1, -1, -1f, 25f, -1, AbilityProperties.IGNITION.get()
                    ));
                })

                // ASPECTS
                .add(DestinyRegistries.ASPECT_REGISTRY_KEY, bootstrap -> {
                    bootstrap.register(Aspects.MELTING_POINT.resourceKey(), new Aspect(Subclasses.SUNBREAKER.getID(), AbilityType.MELEE, -1));
                    bootstrap.register(Aspects.HEATSEEKER.resourceKey(), new Aspect(Subclasses.SUNBREAKER.getID(), AbilityType.MELEE, -1, AbilityProperties.HEATSEEKER.get()));
                    bootstrap.register(Aspects.ANVIL_DROP.resourceKey(), new Aspect(Subclasses.SUNBREAKER.getID(), AbilityType.MELEE, 20, AbilityProperties.ANVIL_DROP.get()));
                    bootstrap.register(Aspects.REKINDLED_FLAMES.resourceKey(), new Aspect(Subclasses.SUNBREAKER.getID(), AbilityType.GRENADE, -1, AbilityProperties.REKINDLED_FLAMES.get()));
                })

                // STATUS EFFECTS
                .add(DestinyRegistries.STATUS_EFFECT_REGISTRY_KEY, bootstrap -> {
                    bootstrap.register(StatusEffects.SCORCH.resourceKey(), new StatusEffect(false, AbilityProperties.SCORCH.get()));
                    bootstrap.register(StatusEffects.MELTING_POINT.resourceKey(), new StatusEffect(false, AbilityProperties.MELTING_POINT.get()));
                    bootstrap.register(StatusEffects.SOL_INVICTUS.resourceKey(), new StatusEffect(true, AbilityProperties.SOL_INVICTUS.get()));
                })

                // DAMAGE TYPES AND DAMAGE TYPE TAGS
                .add(Registries.DAMAGE_TYPE, bootstrap -> {
                    bootstrap.register(DMDamageTypes.MELEE_ABILITY, new DamageType(
                            DestinyMod.MODID+".melee_ability",
                            DamageScaling.NEVER,
                            0.1f,
                            DamageEffects.HURT,
                            DeathMessageType.DEFAULT
                    ));
                    bootstrap.register(DMDamageTypes.GRENADE_ABILITY, new DamageType(
                            DestinyMod.MODID+".grenade",
                            DamageScaling.NEVER,
                            0.1f,
                            DamageEffects.HURT,
                            DeathMessageType.DEFAULT
                    ));
                    bootstrap.register(DMDamageTypes.CLASS_ABILITY, new DamageType(
                            DestinyMod.MODID+".class_ability",
                            DamageScaling.NEVER,
                            0.1f,
                            DamageEffects.HURT,
                            DeathMessageType.DEFAULT
                    ));
                    bootstrap.register(DMDamageTypes.SUPER, new DamageType(
                            DestinyMod.MODID+".super",
                            DamageScaling.NEVER,
                            0.1f,
                            DamageEffects.HURT,
                            DeathMessageType.DEFAULT
                    ));
                    bootstrap.register(DMDamageTypes.SCORCH, new DamageType(
                            DestinyMod.MODID+".scorch",
                            DamageScaling.NEVER,
                            0.1f,
                            DamageEffects.HURT,
                            DeathMessageType.DEFAULT
                    ));
                    bootstrap.register(DMDamageTypes.IGNITION, new DamageType(
                            DestinyMod.MODID+".ignition",
                            DamageScaling.NEVER,
                            0.1f,
                            DamageEffects.HURT,
                            DeathMessageType.DEFAULT
                    ));
                    bootstrap.register(DMDamageTypes.SUNSPOT, new DamageType(
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
