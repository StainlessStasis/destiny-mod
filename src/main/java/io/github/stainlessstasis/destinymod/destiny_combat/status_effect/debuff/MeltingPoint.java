package io.github.stainlessstasis.destinymod.destiny_combat.status_effect.debuff;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.stainlessstasis.destinymod.data.DestinyModAttachments;
import io.github.stainlessstasis.destinymod.destiny_combat.status_effect.AbstractStatusEffect;
import io.github.stainlessstasis.destinymod.destiny_combat.status_effect.IStatusEffect;
import net.minecraft.world.entity.LivingEntity;
import net.neoforged.neoforge.attachment.AttachmentType;

import java.util.function.Supplier;

public class MeltingPoint extends AbstractStatusEffect {
    public static final int DEFAULT_TICKS = 160;
    private int remainingTicks = DEFAULT_TICKS;

    public static final MapCodec<MeltingPoint> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            Codec.INT.fieldOf("remainingTicks").forGetter(MeltingPoint::getRemainingTicks)
    ).apply(instance, MeltingPoint::new));

    public MeltingPoint() {}

    private MeltingPoint(int ticks) {
        remainingTicks = ticks;
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
        return DestinyModAttachments.IS_MELTING_POINT_ACTIVE;
    }

    @Override
    public Supplier<? extends AttachmentType<? extends IStatusEffect>> getAttachment() {
        return DestinyModAttachments.MELTING_POINT;
    }
}

