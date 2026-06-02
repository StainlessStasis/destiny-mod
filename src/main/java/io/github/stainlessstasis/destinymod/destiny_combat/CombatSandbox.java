package io.github.stainlessstasis.destinymod.destiny_combat;

import io.github.stainlessstasis.destinymod.destiny_classes.Subclasses;
import io.github.stainlessstasis.destinymod.destiny_classes.player_equipped.PlayerSubclassData;
import io.github.stainlessstasis.destinymod.destiny_combat.damage.DMDamageTypes;
import io.github.stainlessstasis.destinymod.entity.DestinyModEntities;
import io.github.stainlessstasis.destinymod.entity.SunspotEntity;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
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
        if (randomActivationChance <= 0.3f && subclass == Subclasses.SUNBREAKER && source.is(DMDamageTypes.Tags.IS_ABILITY) && !source.is(DMDamageTypes.SUNSPOT)) {
            SunspotEntity sunspotEntity = new SunspotEntity(DestinyModEntities.SUNSPOT.get(), player.level(), victim.position(), player);
            player.level().addFreshEntity(sunspotEntity);
        }
    }
}
