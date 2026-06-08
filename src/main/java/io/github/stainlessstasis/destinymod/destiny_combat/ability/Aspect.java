package io.github.stainlessstasis.destinymod.destiny_combat.ability;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.stainlessstasis.destinymod.destiny_combat.ability.registry.property.AbilityProperty;
import io.github.stainlessstasis.destinymod.destiny_combat.ability.registry.property.AbilityPropertyTypes;
import net.minecraft.resources.Identifier;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public record Aspect(Identifier subclassID, AbilityType abilityType, int cooldownTicks, AbilityProperty... properties) {
    public static final Codec<Aspect> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                    Identifier.CODEC.fieldOf("subclassID").forGetter(Aspect::subclassID),
                    AbilityType.CODEC.fieldOf("abilityType").forGetter(Aspect::abilityType),
                    Codec.INT.optionalFieldOf("cooldownTicks", -1).forGetter(Aspect::cooldownTicks),
                    Codec.list(AbilityPropertyTypes.DISPATCH_CODEC)
                            .optionalFieldOf("properties", List.of())
                            .xmap(list -> list.toArray(new AbilityProperty[0]), List::of)
                            .forGetter(Aspect::properties)
            ).apply(instance, Aspect::new)
    );

    public <T extends AbilityProperty> Optional<T> getProperty(Class<T> propertyClass) {
        return Arrays.stream(this.properties)
                .filter(property -> propertyClass.isAssignableFrom(property.getClass()))
                .map(propertyClass::cast)
                .findFirst();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Aspect(Identifier id, AbilityType type, int ticks, AbilityProperty[] props))) return false;
        return cooldownTicks == ticks &&
                subclassID.equals(id) &&
                abilityType == type &&
                Arrays.equals(properties, props);
    }

    @Override
    public int hashCode() {
        int hash = Objects.hash(subclassID, abilityType, cooldownTicks);
        hash = 31 * hash + Arrays.hashCode(properties);
        return hash;
    }

    @Override
    public String toString() {
        return "Aspect[" +
                "subclassID=" + subclassID + ", " +
                "abilityType=" + abilityType + ", " +
                "cooldownTicks=" + cooldownTicks + ", " +
                "properties=" + Arrays.toString(properties) +
                ']';
    }
}

