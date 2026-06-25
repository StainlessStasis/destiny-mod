package io.github.stainlessstasis.destinymod;

import java.util.List;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.neoforged.neoforge.common.ModConfigSpec;

public class Config {
    private static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();

    public static final ModConfigSpec.BooleanValue ENABLE_SELF_DAMAGING_ABILITIES = BUILDER
            .comment("Makes abilities able to damage/target themselves. E.g. throwing hammer/thermite grenade damaging your own barricades")
            .define("selfDamagingAbilities", false);

    static final ModConfigSpec SPEC = BUILDER.build();
}
