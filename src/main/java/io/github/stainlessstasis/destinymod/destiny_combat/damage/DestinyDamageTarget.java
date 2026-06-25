package io.github.stainlessstasis.destinymod.destiny_combat.damage;

import io.github.stainlessstasis.destinymod.registry.damage_type.RegisteredDamageType;
import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.Nullable;

public interface DestinyDamageTarget {
    boolean applyDestinyDamage(DestinyDamageBuilder builder);
    default void applyScorch(@Nullable LivingEntity attacker, RegisteredDamageType damageType, int amount) {}
    default void applyMeltingPoint() {}
}
