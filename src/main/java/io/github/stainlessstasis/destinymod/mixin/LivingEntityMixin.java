package io.github.stainlessstasis.destinymod.mixin;

import io.github.stainlessstasis.destinymod.destiny_combat.damage.DestinyDamageBuilder;
import io.github.stainlessstasis.destinymod.destiny_combat.damage.DestinyDamageTarget;
import io.github.stainlessstasis.destinymod.destiny_combat.status_effect.StatusEffectManager;
import io.github.stainlessstasis.destinymod.registry.damage_type.RegisteredDamageType;
import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin implements DestinyDamageTarget {
    @Override
    public boolean applyDestinyDamage(DestinyDamageBuilder builder) {
        return builder.executeDamage();
    }

    @Override
    public void applyScorch(@Nullable LivingEntity attacker, RegisteredDamageType damageType, int amount) {
        StatusEffectManager.applyScorch((LivingEntity)(Object)this, attacker, damageType, amount);
    }

    @Override
    public void applyMeltingPoint() {
        StatusEffectManager.applyMeltingPoint((LivingEntity)(Object)this);
    }
}
