package io.github.stainlessstasis.destinymod.destiny_combat.damage;

import io.github.stainlessstasis.destinymod.destiny_classes.DestinyElement;
import io.github.stainlessstasis.destinymod.mixin_api.DestinyModDamageSource;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import org.jspecify.annotations.Nullable;

public class DestinyDamageBuilder {
    private final ResourceKey<DamageType> damageTypeKey;
    private final LivingEntity victim;

    private @Nullable Entity directEntity;
    private @Nullable Entity causingEntity;
    private DestinyElement element = DestinyElement.NONE;
    private boolean hasKnockback = true;
    private float damage = 1f;
    private int invulnerabilityTicks = -1;

    private DestinyDamageBuilder(ResourceKey<DamageType> damageTypeKey, LivingEntity victim) {
        this.damageTypeKey = damageTypeKey;
        this.victim = victim;
    }

    public static DestinyDamageBuilder create(ResourceKey<DamageType> damageTypeKey, LivingEntity victim) {
        return new DestinyDamageBuilder(damageTypeKey, victim);
    }

    /**
     * The entity directly causing the damage (e.g. a Throwing Hammer, or Arrow)
     */
    public DestinyDamageBuilder directSource(@Nullable Entity directEntity) {
        this.directEntity = directEntity;
        return this;
    }

    /**
     * The root cause entity of the damage (e.g. the player who threw the Throwing Hammer, or skeleton who shot the arrow)
     */
    public DestinyDamageBuilder attacker(@Nullable Entity causingEntity) {
        this.causingEntity = causingEntity;
        return this;
    }

    public DestinyDamageBuilder element(DestinyElement element) {
        this.element = element;
        return this;
    }

    public DestinyDamageBuilder knockback(boolean hasKnockback) {
        this.hasKnockback = hasKnockback;
        return this;
    }

    public DestinyDamageBuilder damage(float damage) {
        this.damage = damage;
        return this;
    }

    public DestinyDamageBuilder invulnerabilityTicks(int invulnerabilityTicks) {
        this.invulnerabilityTicks = invulnerabilityTicks;
        return this;
    }

    public DamageSource buildDamageSource() {
        var vanillaSources = victim.level().damageSources();
        DamageSource source = new DamageSource(
                vanillaSources.damageTypes.getOrThrow(damageTypeKey),
                directEntity,
                causingEntity
        );

        ((DestinyModDamageSource) source).destinymod$setElement(element);
        ((DestinyModDamageSource) source).destinymod$setHasKnockback(hasKnockback);

        return source;
    }

    /**
     * Damages the victim entity only if the victim's level is serverside. Does nothing on the client
     */
    public boolean execute() {
        if (!(victim.level() instanceof ServerLevel level)) {
            return false;
        }

        DamageSource source = this.buildDamageSource();
        boolean wasHurt = victim.hurtServer(level, source, this.damage);
        if (this.invulnerabilityTicks >= 0) {
            victim.invulnerableTime = invulnerabilityTicks;
        }

        return wasHurt;
    }
}
