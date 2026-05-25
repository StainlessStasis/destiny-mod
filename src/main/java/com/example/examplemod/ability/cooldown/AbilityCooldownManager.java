package com.example.examplemod.ability.cooldown;

import com.example.examplemod.DestinyModAttachments;
import com.example.examplemod.ability.Ability;
import net.minecraft.world.entity.Entity;

public class AbilityCooldownManager {
    public static AbilityCooldowns getCooldowns(Entity entity) {
        return entity.getData(DestinyModAttachments.ABILITY_COOLDOWNS);
    }

    public static void addCooldown(Entity entity, Ability ability, int ticks) {
        var cooldowns = getCooldowns(entity);
        cooldowns.addCooldown(ability, ticks);
        entity.setData(DestinyModAttachments.ABILITY_COOLDOWNS, cooldowns);
    }

    public static void removeCooldown(Entity entity, Ability ability) {
        var cooldowns = getCooldowns(entity);
        cooldowns.removeCooldown(ability);
        entity.setData(DestinyModAttachments.ABILITY_COOLDOWNS, cooldowns);
    }

    public static boolean isOnCooldown(Entity entity, Ability ability) {
        var cooldowns = getCooldowns(entity);
        return cooldowns.isOnCooldown(ability);
    }

    public static float getCooldownPercent(Entity entity, Ability ability) {
        return getCooldownPercent(entity, ability, 0);
    }

    public static float getCooldownPercent(Entity entity, Ability ability, float partialTick) {
        var cooldowns = getCooldowns(entity);
        return cooldowns.getCooldownPercent(ability, partialTick);
    }

    public static void reduceCooldownPercent(Entity entity, Ability ability, float reductionAmount) {
        var cooldowns = getCooldowns(entity);
        cooldowns.reduceCooldownPercent(ability, reductionAmount);
        entity.setData(DestinyModAttachments.ABILITY_COOLDOWNS, cooldowns);
    }
}
