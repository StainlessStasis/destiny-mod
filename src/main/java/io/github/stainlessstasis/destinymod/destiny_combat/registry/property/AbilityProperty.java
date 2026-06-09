package io.github.stainlessstasis.destinymod.destiny_combat.registry.property;

public interface AbilityProperty {
    default String type() {
        return AbilityProperties.getPropertyId(this.getClass());
    }
}
