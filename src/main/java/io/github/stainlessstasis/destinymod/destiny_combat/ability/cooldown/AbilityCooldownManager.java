package io.github.stainlessstasis.destinymod.destiny_combat.ability.cooldown;

import io.github.stainlessstasis.destinymod.data.DestinyModAttachments;
import io.github.stainlessstasis.destinymod.registry.datapack.RegisteredAbility;
import net.minecraft.world.entity.Entity;

public class AbilityCooldownManager {
    public static AbilityCooldowns getCooldowns(Entity entity) {
        return entity.getData(DestinyModAttachments.ABILITY_COOLDOWNS);
    }

    public static void addCooldown(Entity entity, RegisteredAbility registeredAbility) {
        var cooldowns = getCooldowns(entity);
        cooldowns.addCooldown(registeredAbility, entity.level().registryAccess());
        entity.setData(DestinyModAttachments.ABILITY_COOLDOWNS, cooldowns);
    }

    public static void removeCooldown(Entity entity, RegisteredAbility registeredAbility) {
        var cooldowns = getCooldowns(entity);
        cooldowns.removeCooldown(registeredAbility);
        entity.setData(DestinyModAttachments.ABILITY_COOLDOWNS, cooldowns);
    }

    public static boolean isOnCooldown(Entity entity, RegisteredAbility registeredAbility) {
        var cooldowns = getCooldowns(entity);
        return cooldowns.isOnCooldown(registeredAbility);
    }

    public static boolean hasCharges(Entity entity, RegisteredAbility registeredAbility) {
        var cooldowns = getCooldowns(entity);
        return cooldowns.hasCharges(registeredAbility);
    }

    public static int getCharges(Entity entity, RegisteredAbility registeredAbility) {
        var cooldowns = getCooldowns(entity);
        return cooldowns.getCharges(registeredAbility, entity.level().registryAccess());
    }

    public static float getCooldownPercent(Entity entity, RegisteredAbility registeredAbility) {
        return getCooldownPercent(entity, registeredAbility, 0);
    }

    public static float getCooldownPercent(Entity entity, RegisteredAbility registeredAbility, float partialTick) {
        var cooldowns = getCooldowns(entity);
        return cooldowns.getCooldownPercent(registeredAbility, partialTick);
    }

    public static void reduceCooldownPercent(Entity entity, RegisteredAbility registeredAbility, float reductionAmount) {
        var cooldowns = getCooldowns(entity);
        cooldowns.reduceCooldownPercent(registeredAbility, reductionAmount);
        entity.setData(DestinyModAttachments.ABILITY_COOLDOWNS, cooldowns);
    }
}
