package io.github.stainlessstasis.destinymod.compat.LDL;

import dev.lambdaurora.lambdynlights.api.behavior.DynamicLightBehaviorManager;
import io.github.stainlessstasis.destinymod.DestinyMod;
import io.github.stainlessstasis.destinymod.entity.DestinyModEntities;
import dev.lambdaurora.lambdynlights.api.DynamicLightsContext;
import dev.lambdaurora.lambdynlights.api.DynamicLightsInitializer;
import dev.lambdaurora.lambdynlights.api.entity.luminance.EntityLuminance;
import net.minecraft.resources.Identifier;

public class LDLCompat implements DynamicLightsInitializer {
    public static DynamicLightBehaviorManager BEHAVIOR_MANAGER;
    public static final EntityLuminance.Type CONSTANT = EntityLuminance.Type.registerSimple(
            Identifier.fromNamespaceAndPath(DestinyMod.MODID, "custom"),
            ConstantEntityLuminance.INSTANCE
    );

    @Override
    public void onInitializeDynamicLights(DynamicLightsContext dynamicLightsContext) {
        BEHAVIOR_MANAGER = dynamicLightsContext.dynamicLightBehaviorManager();
        dynamicLightsContext.entityLightSourceManager().onRegisterEvent().register(context -> {
            context.register(DestinyModEntities.HAMMER_OF_SOL.get(), 7);
            context.register(DestinyModEntities.SUNSPOT.get(), 11);
        });
    }
}
