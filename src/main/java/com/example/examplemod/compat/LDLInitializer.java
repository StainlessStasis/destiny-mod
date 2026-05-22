package com.example.examplemod.compat;

import com.example.examplemod.DestinyMod;
import com.example.examplemod.entity.DestinyModEntities;
import dev.lambdaurora.lambdynlights.api.DynamicLightsContext;
import dev.lambdaurora.lambdynlights.api.DynamicLightsInitializer;
import dev.lambdaurora.lambdynlights.api.entity.luminance.EntityLuminance;
import net.minecraft.resources.Identifier;

public class LDLInitializer implements DynamicLightsInitializer {
    public static final EntityLuminance.Type CONSTANT = EntityLuminance.Type.registerSimple(
            Identifier.fromNamespaceAndPath(DestinyMod.MODID, "custom"),
            ConstantEntityLuminance.INSTANCE
    );

    @Override
    public void onInitializeDynamicLights(DynamicLightsContext dynamicLightsContext) {
        dynamicLightsContext.entityLightSourceManager().onRegisterEvent().register(context -> {
            context.register(DestinyModEntities.HAMMER_OF_SOL.get(), 8);
        });
    }
}
