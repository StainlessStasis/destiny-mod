package io.github.stainlessstasis.destinymod.destiny_combat.ability.grenade;

import io.github.stainlessstasis.destinymod.entity.collision.CollisionContext;
import io.github.stainlessstasis.destinymod.entity.ability.ThrownGrenadeEntity;
import org.jetbrains.annotations.Nullable;

@FunctionalInterface
public interface GrenadeBehavior {
    void detonate(ThrownGrenadeEntity entity, @Nullable CollisionContext context);
}
