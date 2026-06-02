package io.github.stainlessstasis.destinymod.destiny_combat;

import io.github.stainlessstasis.destinymod.destiny_classes.Subclasses;
import io.github.stainlessstasis.destinymod.destiny_classes.player_equipped.PlayerSubclassData;
import io.github.stainlessstasis.destinymod.destiny_combat.ability.Abilities;
import io.github.stainlessstasis.destinymod.destiny_combat.damage.DMDamageTypes;
import io.github.stainlessstasis.destinymod.entity.DestinyModEntities;
import io.github.stainlessstasis.destinymod.entity.SunspotEntity;
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
    }

    @SubscribeEvent
    public static void onLivingDeath(LivingDeathEvent event) {
        DamageSource source = event.getSource();
        LivingEntity victim = event.getEntity();
        if (source.getEntity() instanceof Player player) {
            handlePlayerKillEffects(player, victim, source);
        }
    }

    private static void handlePlayerKillEffects(Player player, LivingEntity victim, DamageSource source) {
        var subclass = PlayerSubclassData.getEquippedSubclass(player);
        final float randomActivationChance = player.getRandom().nextFloat();

        if (randomActivationChance <= Abilities.SUNSPOT.get(player).activationChance() && subclass == Subclasses.SUNBREAKER
                && source.is(DMDamageTypes.Tags.IS_ABILITY) && !source.is(DMDamageTypes.SUNSPOT)
        ) {
            Vec3 spawnPos = findGroundPosition(victim);
            SunspotEntity sunspotEntity = new SunspotEntity(DestinyModEntities.SUNSPOT.get(), player.level(), spawnPos, player);
            player.level().addFreshEntity(sunspotEntity);
        }
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
