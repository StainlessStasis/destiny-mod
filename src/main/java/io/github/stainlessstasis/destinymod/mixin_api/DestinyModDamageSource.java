package io.github.stainlessstasis.destinymod.mixin_api;

import io.github.stainlessstasis.destinymod.destiny_classes.DestinyElement;

public interface DestinyModDamageSource {
    DestinyElement destinymod$getElement();
    void destinymod$setElement(DestinyElement element);
    boolean destinymod$isBypassingKnockback();
    void destinymod$setBypassKnockback(boolean knockback);
}
