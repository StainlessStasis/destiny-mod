package io.github.stainlessstasis.destinymod.destiny_combat.ability.grenade;

import io.github.stainlessstasis.destinymod.destiny_combat.ability.collision.CollisionContext;
import io.github.stainlessstasis.destinymod.entity.ThrownGrenadeEntity;
import org.jetbrains.annotations.Nullable;

@FunctionalInterface
public interface GrenadeBehavior {
    void detonate(ThrownGrenadeEntity entity, @Nullable CollisionContext context);
}
