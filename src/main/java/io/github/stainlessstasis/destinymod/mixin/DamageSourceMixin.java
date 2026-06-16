package io.github.stainlessstasis.destinymod.mixin;

import io.github.stainlessstasis.destinymod.destiny_classes.DestinyElement;
import io.github.stainlessstasis.destinymod.mixin_api.DestinyModDamageSource;
import io.github.stainlessstasis.destinymod.registry.damage_type.DMDamageTypes;
import io.github.stainlessstasis.destinymod.registry.damage_type.RegisteredDamageType;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(DamageSource.class)
public abstract class DamageSourceMixin implements DestinyModDamageSource {
    @Unique private DestinyElement destinymod$element = DestinyElement.NONE;
    @Unique private boolean destinymod$overridesKnockback = false;
    @Unique private boolean destinymod$hasKnockback = true;
    @Unique private RegisteredDamageType destinymod$attributedDamageType = DMDamageTypes.NONE;

    @Override
    public DestinyElement destinymod$getElement() {return this.destinymod$element;}
    @Override
    public void destinymod$setElement(DestinyElement element) {this.destinymod$element = element;}
    @Override
    public boolean destinymod$hasKnockback() { return this.destinymod$hasKnockback; }
    @Override
    public void destinymod$setHasKnockback(boolean hasKnockback) {
        this.destinymod$hasKnockback = hasKnockback;
        this.destinymod$overridesKnockback = true;
    }
    @Override
    public RegisteredDamageType destinymod$getAttributedDamageType() {
        return this.destinymod$attributedDamageType;
    }
    @Override
    public void destinymod$setAttributedDamageType(RegisteredDamageType attributedDamageType) {
        this.destinymod$attributedDamageType = attributedDamageType;
    }

    @Inject(method = "is(Lnet/minecraft/tags/TagKey;)Z", at = @At("HEAD"), cancellable = true)
    private void checkKnockback(TagKey<DamageType> tag, CallbackInfoReturnable<Boolean> cir) {
        if (tag.equals(DamageTypeTags.NO_KNOCKBACK) && destinymod$overridesKnockback) {
            cir.setReturnValue(!this.destinymod$hasKnockback);
        }

    }
}
