package io.github.stainlessstasis.destinymod.destiny_combat.ability;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.stainlessstasis.destinymod.destiny_classes.DestinyElement;
import io.github.stainlessstasis.destinymod.destiny_combat.ability.registry.property.AbilityProperty;
import io.github.stainlessstasis.destinymod.destiny_combat.ability.registry.property.AbilityProperties;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public record Ability(
        AbilityType abilityType, DestinyElement element, int cooldownTicks, int maxCharges, float activationChance, float damage, int scorch, AbilityProperty... properties
) {
    public static final Codec<Ability> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                    AbilityType.CODEC.fieldOf("abilityType").forGetter(Ability::abilityType),
                    DestinyElement.CODEC.fieldOf("element").forGetter(Ability::element),
                    Codec.INT.optionalFieldOf("cooldownTicks", -1).forGetter(Ability::cooldownTicks),
                    Codec.INT.optionalFieldOf("maxCharges", -1).forGetter(Ability::maxCharges),
                    Codec.FLOAT.optionalFieldOf("activationChance", -1f).forGetter(Ability::activationChance),
                    Codec.FLOAT.optionalFieldOf("damage", -1f).forGetter(Ability::damage),
                    Codec.INT.optionalFieldOf("scorch", -1).forGetter(Ability::scorch),
                    Codec.list(AbilityProperties.DISPATCH_CODEC)
                            .optionalFieldOf("properties", List.of())
                            .xmap(list -> list.toArray(new AbilityProperty[0]), List::of)
                            .forGetter(Ability::properties)
            ).apply(instance, Ability::new)
    );

    public <T extends AbilityProperty> Optional<T> getProperty(Class<T> propertyClass) {
        String targetTypeId = AbilityProperties.getPropertyId(propertyClass);
        if (targetTypeId == null) return Optional.empty();

        return Arrays.stream(this.properties)
                .filter(property -> property.type().equals(targetTypeId))
                .map(propertyClass::cast)
                .findFirst();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Ability(
                AbilityType type, DestinyElement element1, int ticks, int charges, float chance, float damage1,
                int scorch1, AbilityProperty[] props
        ))) return false;
        return this.cooldownTicks == ticks &&
                this.maxCharges == charges &&
                Float.compare(this.activationChance, chance) == 0 &&
                Float.compare(this.damage, damage1) == 0 &&
                this.scorch == scorch1 &&
                this.abilityType == type &&
                this.element == element1 &&
                Arrays.equals(this.properties, props);
    }

    @Override
    public int hashCode() {
        int hash = Objects.hash(abilityType, element, cooldownTicks, maxCharges, activationChance, damage, scorch);
        hash = 31 * hash + Arrays.hashCode(properties);
        return hash;
    }

    @Override
    public String toString() {
        return "Ability[" +
                "abilityType=" + abilityType +
                ", element=" + element +
                ", cooldownTicks=" + cooldownTicks +
                ", maxCharges=" + maxCharges +
                ", activationChance=" + activationChance +
                ", damage=" + damage +
                ", scorch=" + scorch +
                ", properties=" + Arrays.toString(properties) +
                ']';
    }
}
