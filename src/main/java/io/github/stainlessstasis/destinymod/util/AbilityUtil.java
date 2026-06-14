package io.github.stainlessstasis.destinymod.util;

import io.github.stainlessstasis.destinymod.registry.datapack.RegisteredAbility;
import io.github.stainlessstasis.destinymod.registry.datapack.RegisteredAspect;

public class AbilityUtil {
    public static String getAbilityName(RegisteredAbility ability) {
        return ability.resourceKey().identifier().getPath();
    }

    public static String getAspectName(RegisteredAspect aspect) {
        return aspect.resourceKey().identifier().getPath();
    }
}
