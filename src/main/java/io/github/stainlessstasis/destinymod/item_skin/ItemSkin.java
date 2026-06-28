package io.github.stainlessstasis.destinymod.item_skin;

import com.geckolib.animatable.GeoItem;
import com.geckolib.animatable.instance.AnimatableInstanceCache;
import com.geckolib.animatable.manager.AnimatableManager;
import com.geckolib.util.GeckoLibUtil;
import io.github.stainlessstasis.destinymod.DestinyMod;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Item;

public class ItemSkin extends Item implements GeoItem {
    public static final Identifier HAMMER_OF_SOL = DestinyMod.id("hammer_of_sol");
    public static final Identifier CROWN_SPLITTER = DestinyMod.id("crown_splitter");
    public static final Identifier CROWN_SPLITTER_BLOODY = DestinyMod.id("crown_splitter_bloody");

    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    public ItemSkin(Item.Properties properties) {
        super(properties);
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {}

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return cache;
    }
}
