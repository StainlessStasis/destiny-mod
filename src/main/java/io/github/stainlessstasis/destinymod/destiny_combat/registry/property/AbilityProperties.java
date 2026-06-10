package io.github.stainlessstasis.destinymod.destiny_combat.registry.property;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import io.github.stainlessstasis.destinymod.destiny_combat.registry.property.aspect.AnvilDropProperty;
import io.github.stainlessstasis.destinymod.destiny_combat.registry.property.aspect.HeatseekerProperty;
import io.github.stainlessstasis.destinymod.destiny_combat.registry.property.status_effect.MeltingPointProperty;
import io.github.stainlessstasis.destinymod.destiny_combat.registry.property.status_effect.ScorchProperty;
import io.github.stainlessstasis.destinymod.destiny_combat.registry.property.status_effect.SolInvictusProperty;

import java.util.HashMap;
import java.util.Map;

public class AbilityProperties {
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

    public static String getPropertyId(Class<? extends AbilityProperty> clazz) {
        return BY_CLASS.get(clazz);
    }

    static {
        register("heatseeker", HeatseekerProperty.class, HeatseekerProperty.CODEC);
        register("anvil_drop", AnvilDropProperty.class, AnvilDropProperty.CODEC);
        register("scorch", ScorchProperty.class, ScorchProperty.CODEC);
        register("melting_point", MeltingPointProperty.class, MeltingPointProperty.CODEC);
        register("sol_invictus", SolInvictusProperty.class, SolInvictusProperty.CODEC);
    }
}
