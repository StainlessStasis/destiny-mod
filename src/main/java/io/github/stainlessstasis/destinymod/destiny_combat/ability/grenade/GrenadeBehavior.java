package io.github.stainlessstasis.destinymod.destiny_combat.ability.grenade;

import io.github.stainlessstasis.destinymod.destiny_combat.ability.collision.CollisionContext;
import io.github.stainlessstasis.destinymod.entity.GrenadeEntity;

@FunctionalInterface
public interface GrenadeBehavior {
    void detonate(GrenadeEntity entity, CollisionContext context);
}
