package io.github.stainlessstasis.destinymod.mixin;

import io.github.stainlessstasis.destinymod.destiny_classes.DestinyElement;
import io.github.stainlessstasis.destinymod.mixin_api.DestinyModDamageSource;
import net.minecraft.world.damagesource.DamageSource;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(DamageSource.class)
public abstract class DamageSourceMixin implements DestinyModDamageSource {
    @Unique private DestinyElement destinymod$element = DestinyElement.NONE;
    @Unique private boolean destinymod$bypassKnockback = false;

    @Override
    public DestinyElement destinymod$getElement() {
        return this.destinymod$element;
    }
    @Override
    public void destinymod$setElement(DestinyElement element) {
        this.destinymod$element = element;
    }
    @Override
    public boolean destinymod$isBypassingKnockback() { return this.destinymod$bypassKnockback; }
    @Override
    public void destinymod$setBypassKnockback(boolean bypass) { this.destinymod$bypassKnockback = bypass; }


}
