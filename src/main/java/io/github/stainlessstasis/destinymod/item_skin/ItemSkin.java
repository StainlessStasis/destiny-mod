package io.github.stainlessstasis.destinymod.item_skin;

import com.geckolib.animatable.GeoItem;
import com.geckolib.animatable.instance.AnimatableInstanceCache;
import com.geckolib.animatable.manager.AnimatableManager;
import com.geckolib.util.GeckoLibUtil;
import io.github.stainlessstasis.destinymod.DestinyMod;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Item;

import java.util.ArrayList;
import java.util.List;

public class ItemSkin extends Item implements GeoItem {
    public static final List<Identifier> SKIN_IDS = new ArrayList<>();
    public static final Identifier HAMMER_OF_SOL = register("hammer_of_sol");
    public static final Identifier CROWN_SPLITTER = register("crown_splitter");
    public static final Identifier CROWN_SPLITTER_BLOODY = register("crown_splitter_bloody");
    public static final Identifier HEARTSHADOW = register("heartshadow");

    private static Identifier register(String id_) {
        Identifier id = DestinyMod.id(id_);
        SKIN_IDS.add(id);
        return id;
    }

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
