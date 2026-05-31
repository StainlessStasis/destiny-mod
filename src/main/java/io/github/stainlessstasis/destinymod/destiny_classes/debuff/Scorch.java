package io.github.stainlessstasis.destinymod.destiny_classes.debuff;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.stainlessstasis.destinymod.data.DestinyModAttachments;
import io.github.stainlessstasis.destinymod.destiny_classes.DestinyElement;
import io.github.stainlessstasis.destinymod.destiny_classes.ability.solar.Ignition;
import io.github.stainlessstasis.destinymod.destiny_classes.damage.DestinyDamageBuilder;
import io.github.stainlessstasis.destinymod.destiny_classes.damage.DestinyModDamageTypes;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.attachment.AttachmentType;

import java.util.function.Supplier;

public class Scorch extends OwnableDebuff {
    public static final int IGNITION_THRESHOLD = 100;
    /*** Damage at 0 stacks, scales up to 3x the amount*/
    public static final float DAMAGE = 0.25f;
    private int stacks = 0;
    private int decayDelay = 0;

    public Scorch() {}

    private Scorch(int stacks, int decayDelay) {
        this.stacks = stacks;
        this.decayDelay = decayDelay;
    }

    public void addStacks(int amount) {
        this.stacks = Math.min(IGNITION_THRESHOLD, this.stacks + amount);
        this.decayDelay = 40;
    }

    public int getStacks() { return this.stacks; }
    public int getDecayDelay() { return this.decayDelay; }

    public static final MapCodec<Scorch> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            Codec.INT.fieldOf("stacks").forGetter(Scorch::getStacks),
            Codec.INT.fieldOf("decay_delay").forGetter(Scorch::getDecayDelay)
    ).apply(instance, Scorch::new));

    public static final StreamCodec<ByteBuf, Scorch> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.VAR_INT, Scorch::getStacks,
            ByteBufCodecs.VAR_INT, Scorch::getDecayDelay,
            Scorch::new
    );

    @Override
    public boolean isActive() {
        return this.stacks > 0;
    }

    @Override
    public void tick(LivingEntity entity) {
        super.tick(entity);
        if (this.stacks >= IGNITION_THRESHOLD && entity.level() instanceof ServerLevel level) {
            LivingEntity owner = getOwner(level);
            Ignition.ignite(entity, owner, owner);
            this.clear(entity);
            return;
        }
        if (this.tickCount%10 == 0) {
            hurt(entity);
            entity.level().playSound(null, entity, SoundEvents.LAVA_EXTINGUISH, SoundSource.AMBIENT, 0.5f, 2f);
        }

        if (this.decayDelay > 0) {
            this.decayDelay--;
        } else {
            this.stacks = Math.max(0, this.stacks-1);
        }
    }

    public void hurt(LivingEntity entity) {
        if (entity.level() instanceof ServerLevel level) {
            LivingEntity owner = getOwner(level);
            float damageMultiplier = 1f + (this.stacks*2f/IGNITION_THRESHOLD);
            float damage = DAMAGE * damageMultiplier;
            DestinyDamageBuilder.create(DestinyModDamageTypes.SCORCH, entity)
                    .directSource(owner)
                    .attacker(owner)
                    .element(DestinyElement.SOLAR)
                    .damage(damage)
                    .invulnerabilityTicks(0)
                    .knockback(false)
                    .execute();
        }
    }

    @Override
    public void clear(LivingEntity entity) {
        super.clear(entity);
        this.stacks = 0;
        this.decayDelay = 0;
    }

    @Override
    public Supplier<AttachmentType<Boolean>> getClientStateSyncAttachment() {
        return DestinyModAttachments.IS_SCORCH_ACTIVE;
    }
}
