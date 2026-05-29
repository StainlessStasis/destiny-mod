package io.github.stainlessstasis.destinymod.data;

import io.github.stainlessstasis.destinymod.destiny_classes.damage.DestinyModDamageTypes;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.DamageTypeTagsProvider;

import java.util.concurrent.CompletableFuture;

public class DMDamageTypeTagProvider extends DamageTypeTagsProvider {
    public DMDamageTypeTagProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider, String modId) {
        super(output, lookupProvider, modId);
    }

    @Override
    protected void addTags(HolderLookup.Provider registries) {
        tag(DestinyModDamageTypes.Tags.IS_ABILITY)
                .add(DestinyModDamageTypes.MELEE_ABILITY)
                .add(DestinyModDamageTypes.GRENADE_ABILITY)
                .add(DestinyModDamageTypes.CLASS_ABILITY)
                .add(DestinyModDamageTypes.SUPER);
    }
}
