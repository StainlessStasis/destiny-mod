package io.github.stainlessstasis.destinymod.destiny_combat.status_effect;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.stainlessstasis.destinymod.destiny_combat.registry.property.AbilityProperties;
import io.github.stainlessstasis.destinymod.destiny_combat.registry.property.AbilityProperty;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public record StatusEffect(boolean isBuff, AbilityProperty... properties) {
    public static final Codec<StatusEffect> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                    Codec.BOOL.fieldOf("isBuff").forGetter(StatusEffect::isBuff),
                    Codec.list(AbilityProperties.DISPATCH_CODEC)
                            .optionalFieldOf("properties", List.of())
                            .xmap(list -> list.toArray(new AbilityProperty[0]), List::of)
                            .forGetter(StatusEffect::properties)
            ).apply(instance, StatusEffect::new)
    );

    public <T extends AbilityProperty> Optional<T> getProperty(Class<T> propertyClass) {
        String targetTypeId = AbilityProperties.getPropertyId(propertyClass);
        if (targetTypeId == null) return Optional.empty();

        return Arrays.stream(this.properties)
                .filter(property -> property.type().equals(targetTypeId))
                .map(propertyClass::cast)
                .findFirst();
    }
}
