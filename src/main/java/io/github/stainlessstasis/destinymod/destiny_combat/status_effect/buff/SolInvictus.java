package io.github.stainlessstasis.destinymod.destiny_combat.status_effect.buff;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.stainlessstasis.destinymod.data.DestinyModAttachments;
import io.github.stainlessstasis.destinymod.destiny_combat.status_effect.AbstractStatusEffect;
import io.github.stainlessstasis.destinymod.destiny_combat.status_effect.StatusEffect;
import net.minecraft.world.entity.LivingEntity;
import net.neoforged.neoforge.attachment.AttachmentType;

import java.util.function.Supplier;

public class SolInvictus extends AbstractStatusEffect {
    public static final int DEFAULT_TICKS = 60;
    private int remainingTicks = DEFAULT_TICKS;

    public static final MapCodec<SolInvictus> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            Codec.INT.fieldOf("remainingTicks").forGetter(SolInvictus::getRemainingTicks)
    ).apply(instance, SolInvictus::new));

    public SolInvictus() {}

    private SolInvictus(int ticks) {
        remainingTicks = ticks;
    }

    @Override
    public void tick(LivingEntity entity) {
        System.out.println("TICKING");
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
        System.out.println("REMAINING TICKS: "+remainingTicks);
        return remainingTicks > 0;
    }

    @Override
    public Supplier<AttachmentType<Boolean>> getClientStateSyncAttachment() {
        return DestinyModAttachments.IS_SOL_INVICTUS_ACTIVE;
    }

    @Override
    public Supplier<? extends AttachmentType<? extends StatusEffect>> getAttachment() {
        return DestinyModAttachments.SOL_INVICTUS;
    }
}
