package io.github.stainlessstasis.destinymod.destiny_combat;

import com.google.gson.internal.GsonTypes;
import io.github.stainlessstasis.destinymod.data.DestinyModAttachments;
import io.github.stainlessstasis.destinymod.destiny_classes.Subclass;
import io.github.stainlessstasis.destinymod.destiny_classes.Subclasses;
import io.github.stainlessstasis.destinymod.destiny_classes.player_equipped.PlayerSubclassData;
import io.github.stainlessstasis.destinymod.destiny_combat.ability.cooldown.AbilityCooldownManager;
import io.github.stainlessstasis.destinymod.destiny_combat.status_effect.debuff.Scorch;
import io.github.stainlessstasis.destinymod.mixin_api.DestinyModDamageSource;
import io.github.stainlessstasis.destinymod.registry.datapack.Abilities;
import io.github.stainlessstasis.destinymod.registry.damage_type.DMDamageTypes;
import io.github.stainlessstasis.destinymod.destiny_combat.status_effect.StatusEffectManager;
import io.github.stainlessstasis.destinymod.destiny_combat.status_effect.debuff.MeltingPoint;
import io.github.stainlessstasis.destinymod.entity.DestinyModEntities;
import io.github.stainlessstasis.destinymod.entity.SunspotEntity;
import io.github.stainlessstasis.destinymod.registry.datapack.Aspects;
import io.github.stainlessstasis.destinymod.registry.datapack.RegisteredAbility;
import io.github.stainlessstasis.destinymod.registry.property.aspect.BlazingPyreProperty;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;

@EventBusSubscriber
public class CombatSandbox {
    @SubscribeEvent
    public static void onLivingDamage(LivingIncomingDamageEvent event) {
        DamageSource source = event.getSource();
        DestinyModDamageSource dmSource = (DestinyModDamageSource) source;
        LivingEntity victim = event.getEntity();
        float randomActivationChance = victim.getRandom().nextFloat();
        float damageMultiplier = 1f;

        if (! (source.getEntity() instanceof Player player)) return;
        final Subclass subclass = PlayerSubclassData.getEquippedSubclass(player);

        if (victim.getData(DestinyModAttachments.IS_MELTING_POINT_ACTIVE)) {
            var instance = StatusEffectManager.getInstance(victim, MeltingPoint.class);
            if (instance != null) {
                damageMultiplier += instance.getProperty(victim.level()).additionalDamagePercent();
            }
        }

        event.setAmount(event.getAmount() * damageMultiplier);
    }

    @SubscribeEvent
    public static void onLivingDeath(LivingDeathEvent event) {
        DamageSource source = event.getSource();
        DestinyModDamageSource dmSource = (DestinyModDamageSource) source;
        LivingEntity victim = event.getEntity();
        float randomActivationChance = victim.getRandom().nextFloat();

        if (! (source.getEntity() instanceof Player player)) return;
        final Subclass subclass = PlayerSubclassData.getEquippedSubclass(player);

        // SUNSPOTS
        boolean isScorchActive = StatusEffectManager.isActive(victim, Scorch.class);
        boolean isNonSunspotAbilityDamage = source.is(DMDamageTypes.Tags.IS_ABILITY) && !source.is(DMDamageTypes.SUNSPOT.resourceKey());
        boolean canSpawnSunspot = (isScorchActive || isNonSunspotAbilityDamage)
                && subclass == Subclasses.SUNBREAKER
                && randomActivationChance <= Abilities.SUNSPOT.get(player).activationChance();

        boolean canSpawnFromBlazingPyre = tryActivateBlazingPyre(player, source, dmSource);

        if (canSpawnSunspot || canSpawnFromBlazingPyre) {
            Vec3 spawnPos = findGroundPosition(victim);
            SunspotEntity sunspotEntity = new SunspotEntity(DestinyModEntities.SUNSPOT.get(), player.level(), spawnPos, player);
            player.level().addFreshEntity(sunspotEntity);
        }
    }

    private static boolean tryActivateBlazingPyre(Player player, DamageSource source, DestinyModDamageSource dmSource) {
        if (! PlayerSubclassData.isAspectEquipped(player, Aspects.BLAZING_PYRE)) return false;

        boolean isDirect = source.is(DMDamageTypes.THERMITE_GRENADE.resourceKey());
        boolean isAttributed = dmSource.destinymod$getAttributedDamageType() == DMDamageTypes.THERMITE_GRENADE;
        if (!isDirect && !isAttributed) return false;

        var propOptional = PlayerSubclassData.getEquippedProperty(player, BlazingPyreProperty.class);
        propOptional.ifPresent(prop -> {
            RegisteredAbility ability = PlayerSubclassData.getRegisteredGrenade(player);
            AbilityCooldownManager.reduceCooldownPercent(player, ability, prop.energyRefundPercent());
        });

        return propOptional.isPresent();
    }

    private static Vec3 findGroundPosition(LivingEntity victim) {
        Vec3 startPos = victim.position();
        Vec3 endPos = startPos.add(0, -5, 0);

        BlockHitResult hitResult = victim.level().clip(new ClipContext(
                startPos,
                endPos,
                ClipContext.Block.COLLIDER,
                ClipContext.Fluid.NONE,
                victim
        ));

        if (hitResult.getType() == HitResult.Type.BLOCK) {
            return hitResult.getLocation().add(0, 0.05f, 0);
        }
        return startPos;
    }
}
