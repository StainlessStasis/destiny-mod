package io.github.stainlessstasis.destinymod.registry.property;

public interface AbilityProperty {
    default String type() {
        return AbilityProperties.getPropertyId(this.getClass());
    }
}
