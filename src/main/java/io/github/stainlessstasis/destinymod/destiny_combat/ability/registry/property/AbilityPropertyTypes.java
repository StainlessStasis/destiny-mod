package io.github.stainlessstasis.destinymod.destiny_combat.ability.registry.property;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;

import java.util.HashMap;
import java.util.Map;

public class AbilityPropertyTypes {
    private static final Map<String, MapCodec<? extends AbilityProperty>> BY_NAME = new HashMap<>();
    private static final Map<Class<? extends AbilityProperty>, String> BY_CLASS = new HashMap<>();

    public static final Codec<AbilityProperty> DISPATCH_CODEC = Codec.STRING.dispatch(
            property -> BY_CLASS.get(property.getClass()),
            BY_NAME::get
    );

    public static <T extends AbilityProperty> void register(String typeName, Class<T> clazz, MapCodec<T> codec) {
        if (BY_NAME.containsKey(typeName) || BY_CLASS.containsKey(clazz)) {
            throw new IllegalArgumentException("Duplicate ability property registration for: " + typeName);
        }
        BY_NAME.put(typeName, codec);
        BY_CLASS.put(clazz, typeName);
    }

    static {
        register("heatseeker", HeatseekerProperty.class, HeatseekerProperty.CODEC);
    }
}
