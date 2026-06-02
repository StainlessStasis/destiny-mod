package io.github.stainlessstasis.destinymod.destiny_classes.ability;

import io.github.stainlessstasis.destinymod.destiny_classes.DestinyElement;
import org.jetbrains.annotations.NotNull;

public interface DestinyAbility {
    public @NotNull DestinyElement getDestinyElement();
    public void setDestinyElement(DestinyElement destinyElement);
}
