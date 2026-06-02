package io.github.stainlessstasis.destinymod.data;

import io.github.stainlessstasis.destinymod.destiny_classes.damage.DMDamageTypes;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.DamageTypeTagsProvider;
import org.jspecify.annotations.NonNull;

import java.util.concurrent.CompletableFuture;

public class DMDamageTypeTagProvider extends DamageTypeTagsProvider {
    public DMDamageTypeTagProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider, String modId) {
        super(output, lookupProvider, modId);
    }

    @Override
    protected void addTags(HolderLookup.@NonNull Provider registries) {
        tag(DMDamageTypes.Tags.IS_SUBCLASS_ABILITY)
                .add(DMDamageTypes.MELEE_ABILITY)
                .add(DMDamageTypes.GRENADE_ABILITY)
                .add(DMDamageTypes.CLASS_ABILITY)
                .add(DMDamageTypes.SUPER);

        tag(DMDamageTypes.Tags.IS_ABILITY)
                .add(DMDamageTypes.MELEE_ABILITY)
                .add(DMDamageTypes.GRENADE_ABILITY)
                .add(DMDamageTypes.CLASS_ABILITY)
                .add(DMDamageTypes.SUPER)
                .add(DMDamageTypes.SCORCH)
                .add(DMDamageTypes.IGNITION)
                .add(DMDamageTypes.SUNSPOT);

        tag(DMDamageTypes.Tags.IS_DEBUFF)
                .add(DMDamageTypes.SCORCH);
    }
}
