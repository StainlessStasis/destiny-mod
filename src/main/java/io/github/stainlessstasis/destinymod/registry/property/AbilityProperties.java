package io.github.stainlessstasis.destinymod.registry.property;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import io.github.stainlessstasis.destinymod.registry.property.ability.GrenadePhysicsProperty;
import io.github.stainlessstasis.destinymod.registry.property.ability.IgnitionProperty;
import io.github.stainlessstasis.destinymod.registry.property.ability.ThermiteGrenadeProperty;
import io.github.stainlessstasis.destinymod.registry.property.aspect.AnvilDropProperty;
import io.github.stainlessstasis.destinymod.registry.property.aspect.HeatseekerProperty;
import io.github.stainlessstasis.destinymod.registry.property.status_effect.MeltingPointProperty;
import io.github.stainlessstasis.destinymod.registry.property.status_effect.ScorchProperty;
import io.github.stainlessstasis.destinymod.registry.property.status_effect.SolInvictusProperty;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

public class AbilityProperties {
    private static final Map<String, MapCodec<? extends AbilityProperty>> BY_NAME = new HashMap<>();
    private static final Map<Class<? extends AbilityProperty>, String> BY_CLASS = new HashMap<>();
    private static final Map<String, Supplier<? extends AbilityProperty>> DEFAULTS = new HashMap<>();

    public static final Codec<AbilityProperty> DISPATCH_CODEC = Codec.STRING.dispatch(
            property -> BY_CLASS.get(property.getClass()),
            BY_NAME::get
    );

    public static <T extends AbilityProperty> Supplier<T> register(String typeName, Class<T> clazz, MapCodec<T> codec, Supplier<T> defaultFactory) {
        if (BY_NAME.containsKey(typeName) || BY_CLASS.containsKey(clazz)) {
            throw new IllegalArgumentException("Duplicate ability property registration for: " + typeName);
        }
        BY_NAME.put(typeName, codec);
        BY_CLASS.put(clazz, typeName);
        DEFAULTS.put(typeName, defaultFactory);
        return defaultFactory;
    }

    public static String getPropertyId(Class<? extends AbilityProperty> clazz) {
        if (!BY_CLASS.containsKey(clazz)) return "";
        return BY_CLASS.get(clazz);
    }

    public static @Nullable <T extends AbilityProperty> T getDefault(String typeName) {
        Supplier<? extends AbilityProperty> factory = DEFAULTS.get(typeName);
        return factory != null ? (T) factory.get() : null;
    }

    // ABILITIES
    public static Supplier<IgnitionProperty> IGNITION = register("ignition", IgnitionProperty.class, IgnitionProperty.CODEC,
            () -> new IgnitionProperty(5f)
    );
    public static Supplier<GrenadePhysicsProperty> DEFAULT_GRENADE_PHYSICS = register("grenade_physics", GrenadePhysicsProperty.class, GrenadePhysicsProperty.CODEC,
            GrenadePhysicsProperty::getDefault
    );
    public static Supplier<ThermiteGrenadeProperty> THERMITE_GRENADE = register("thermite_grenade", ThermiteGrenadeProperty.class, ThermiteGrenadeProperty.CODEC,
            ThermiteGrenadeProperty::getDefault
    );

    // ASPECTS
    public static Supplier<HeatseekerProperty> HEATSEEKER = register("heatseeker", HeatseekerProperty.class, HeatseekerProperty.CODEC,
            () -> new HeatseekerProperty(0.1f, 12f, 90f, 35)
    );
    public static final Supplier<AnvilDropProperty> ANVIL_DROP = register("anvil_drop", AnvilDropProperty.class, AnvilDropProperty.CODEC,
            () ->  new AnvilDropProperty(2.5f, 0.7f, 1.65f,
                    1.5f, 5f, 0.5f, 2f)
    );

    // STATUS EFFECTS
    public static final Supplier<ScorchProperty> SCORCH = register("scorch", ScorchProperty.class, ScorchProperty.CODEC,
            () -> new ScorchProperty(0.25f, 40, 100)
    );
    public static final Supplier<MeltingPointProperty> MELTING_POINT = register("melting_point", MeltingPointProperty.class, MeltingPointProperty.CODEC,
            () -> new MeltingPointProperty(160, 0.2f, 0.25f)
    );
    public static final Supplier<SolInvictusProperty> SOL_INVICTUS = register(
            "sol_invictus", SolInvictusProperty.class, SolInvictusProperty.CODEC,
            () -> new SolInvictusProperty(60, 1f)
    );
}
