package io.github.stainlessstasis.destinymod.destiny_combat.status_effect;

import io.github.stainlessstasis.destinymod.registry.damage_type.DMDamageTypes;
import io.github.stainlessstasis.destinymod.registry.damage_type.RegisteredDamageType;
import net.minecraft.world.entity.EntityReference;
import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.Nullable;

public abstract class AttributableStatusEffect extends AbstractStatusEffect {
    @Nullable protected EntityReference<LivingEntity> attackerReference = null;
    protected RegisteredDamageType attributedDamageType = DMDamageTypes.NONE;

    protected AttributableStatusEffect() {}

    public void setAttacker(@Nullable LivingEntity attacker) {
        this.attackerReference = attacker == null ? null : EntityReference.of(attacker);
    }
    @Override @Nullable
    public EntityReference<LivingEntity> getAttackerReference() {
        return this.attackerReference;
    }

    public void setAttributedDamageType(RegisteredDamageType attributedDamageType) {
        this.attributedDamageType = attributedDamageType;
    }
    public RegisteredDamageType getAttributedDamageType() {
        return this.attributedDamageType;
    }

    @Override
    public void clear(LivingEntity entity) {
        super.clear(entity);
        this.attackerReference = null;
        this.attributedDamageType = DMDamageTypes.NONE;
    }
}
