package io.github.stainlessstasis.destinymod.destiny_combat.status_effect.debuff;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.stainlessstasis.destinymod.data.DestinyModAttachments;
import io.github.stainlessstasis.destinymod.destiny_classes.DestinyElement;
import io.github.stainlessstasis.destinymod.destiny_combat.ability.solar.Ignition;
import io.github.stainlessstasis.destinymod.destiny_combat.damage.DestinyDamageBuilder;
import io.github.stainlessstasis.destinymod.destiny_combat.damage.DMDamageTypes;
import io.github.stainlessstasis.destinymod.destiny_combat.registry.StatusEffects;
import io.github.stainlessstasis.destinymod.destiny_combat.registry.property.status_effect.ScorchProperty;
import io.github.stainlessstasis.destinymod.destiny_combat.status_effect.OwnableStatusEffect;
import io.github.stainlessstasis.destinymod.destiny_combat.status_effect.IStatusEffect;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.attachment.AttachmentType;

import java.util.function.Supplier;

public class Scorch extends OwnableStatusEffect {
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

    public static ScorchProperty getDefaultProperty() {
        return new ScorchProperty(0.25f, 40, 100);
    }

    public ScorchProperty getProperty(Level level) {
        return StatusEffects.SCORCH.get(level).getProperty(ScorchProperty.class).orElse(getDefaultProperty());
    }

    public void addStacks(LivingEntity entity, int amount) {
        ScorchProperty property = getProperty(entity.level());
        this.stacks = Math.min(property.ignitionThreshold(), this.stacks + amount);
        this.decayDelay = property.decayDelayTicks();
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
        ScorchProperty property = getProperty(entity.level());

        if (this.stacks >= property.ignitionThreshold() && entity.level() instanceof ServerLevel level) {
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
        ScorchProperty property = getProperty(entity.level());

        if (entity.level() instanceof ServerLevel level) {
            LivingEntity owner = getOwner(level);
            float damageMultiplier = 1f + (this.stacks*2f/property.ignitionThreshold());
            float damage = property.damage() * damageMultiplier;
            DestinyDamageBuilder.create(DMDamageTypes.SCORCH, entity)
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

    @Override
    public Supplier<? extends AttachmentType<? extends IStatusEffect>> getAttachment() {
        return DestinyModAttachments.SCORCH;
    }
}
