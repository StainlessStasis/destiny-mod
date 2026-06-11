package io.github.stainlessstasis.destinymod.destiny_combat.status_effect.buff;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.stainlessstasis.destinymod.data.DestinyModAttachments;
import io.github.stainlessstasis.destinymod.destiny_combat.registry.StatusEffects;
import io.github.stainlessstasis.destinymod.destiny_combat.registry.property.AbilityProperties;
import io.github.stainlessstasis.destinymod.destiny_combat.registry.property.status_effect.MeltingPointProperty;
import io.github.stainlessstasis.destinymod.destiny_combat.registry.property.status_effect.SolInvictusProperty;
import io.github.stainlessstasis.destinymod.destiny_combat.status_effect.AbstractStatusEffect;
import io.github.stainlessstasis.destinymod.destiny_combat.status_effect.IStatusEffect;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.attachment.AttachmentType;

import java.util.function.Supplier;

public class SolInvictus extends AbstractStatusEffect {
    private int remainingTicks = AbilityProperties.SOL_INVICTUS.get().durationTicks();

    public static final MapCodec<SolInvictus> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            Codec.INT.fieldOf("remainingTicks").forGetter(SolInvictus::getRemainingTicks)
    ).apply(instance, SolInvictus::new));

    public SolInvictus() {}

    private SolInvictus(int ticks) {
        remainingTicks = ticks;
    }

    public static SolInvictusProperty getProperty(Level level) {
        return StatusEffects.SOL_INVICTUS.get(level).getProperty(SolInvictusProperty.class).orElse(AbilityProperties.SOL_INVICTUS.get());
    }

    @Override
    public void tick(LivingEntity entity) {
        super.tick(entity);
        remainingTicks--;
    }

    public int getRemainingTicks() {
        return remainingTicks;
    }

    public void setRemainingTicks(int ticks) {
        remainingTicks = ticks;
    }

    @Override
    public boolean isActive() {
        return remainingTicks > 0;
    }

    @Override
    public Supplier<AttachmentType<Boolean>> getClientStateSyncAttachment() {
        return DestinyModAttachments.IS_SOL_INVICTUS_ACTIVE;
    }

    @Override
    public Supplier<? extends AttachmentType<? extends IStatusEffect>> getAttachment() {
        return DestinyModAttachments.SOL_INVICTUS;
    }
}
