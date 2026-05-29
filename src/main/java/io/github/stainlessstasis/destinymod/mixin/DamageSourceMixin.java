package io.github.stainlessstasis.destinymod.mixin;

import io.github.stainlessstasis.destinymod.destiny_classes.DestinyElement;
import io.github.stainlessstasis.destinymod.mixin_api.DestinyModDamageSource;
import net.minecraft.world.damagesource.DamageSource;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(DamageSource.class)
public abstract class DamageSourceMixin implements DestinyModDamageSource {
    @Unique
    private DestinyElement destinymod$element = DestinyElement.NONE;

    @Override
    public DestinyElement destinymod$getElement() {
        return this.destinymod$element;
    }

    @Override
    public void destinymod$setElement(DestinyElement element) {
        this.destinymod$element = element;
    }
}
