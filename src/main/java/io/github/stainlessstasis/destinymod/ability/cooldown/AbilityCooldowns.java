package io.github.stainlessstasis.destinymod.ability.cooldown;

import io.github.stainlessstasis.destinymod.ability.Ability;
import com.google.common.collect.Maps;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.Mth;

public class AbilityCooldowns {
    private final Map<Ability, CooldownInstance> cooldowns = Maps.newHashMap();
    private int tickCount;

    private static final Codec<Map<Ability, CooldownInstance>> MAP_CODEC = Codec.unboundedMap(Ability.CODEC, CooldownInstance.CODEC);

    public static final MapCodec<AbilityCooldowns> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            MAP_CODEC.fieldOf("cooldowns").forGetter(abilityCooldowns -> abilityCooldowns.cooldowns),
            Codec.INT.fieldOf("ticks").forGetter(abilityCooldowns -> abilityCooldowns.tickCount)
    ).apply(instance, AbilityCooldowns::new));

    public static final StreamCodec<ByteBuf, AbilityCooldowns> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.map(
                    HashMap::new,
                    ByteBufCodecs.fromCodec(Ability.CODEC),
                    CooldownInstance.STREAM_CODEC
            ),
            abilityCooldowns -> abilityCooldowns.cooldowns,
            ByteBufCodecs.INT, abilityCooldowns -> abilityCooldowns.tickCount,
            AbilityCooldowns::new
    );

    private AbilityCooldowns(Map<Ability, CooldownInstance> cooldownInstances, int tickCount) {
        this.tickCount = tickCount;
        this.cooldowns.putAll(cooldownInstances);
    }

    public AbilityCooldowns() {}

    public boolean isEmpty() {
        return cooldowns.isEmpty();
    }

    public boolean isOnCooldown(Ability ability) {
        return this.getCooldownPercent(ability, 0.0F) > 0.0F;
    }

    public float getCooldownPercent(Ability ability, float partialTicks) {
        CooldownInstance cooldown = this.cooldowns.get(ability);
        if (cooldown != null) {
            float f = (float)(cooldown.endTime - cooldown.startTime);
            float f1 = (float)cooldown.endTime - ((float)this.tickCount + partialTicks);
            return Mth.clamp(f1 / f, 0.0F, 1.0F);
        } else {
            return 0.0F;
        }
    }

    public void tick() {
        ++this.tickCount;
        if (!this.cooldowns.isEmpty()) {
            Iterator<Map.Entry<Ability, CooldownInstance>> iterator = this.cooldowns.entrySet().iterator();

            while(iterator.hasNext()) {
                Map.Entry<Ability, CooldownInstance> entry = iterator.next();
                if ((entry.getValue()).endTime <= this.tickCount) {
                    iterator.remove();
                    this.onCooldownEnded(entry.getKey());
                }
            }
        }

    }

    void addCooldown(Ability ability, int ticks) {
        this.cooldowns.put(ability, new CooldownInstance(this.tickCount, this.tickCount + ticks));
        this.onCooldownStarted(ability, ticks);
    }

    void removeCooldown(Ability ability) {
        this.cooldowns.remove(ability);
        this.onCooldownEnded(ability);
    }

    void reduceCooldownPercent(Ability ability, float reductionAmount) {
        CooldownInstance cooldown = this.cooldowns.get(ability);
        if (cooldown == null) {
            return;
        }

        int totalDuration = cooldown.getTotalDuration();
        int currentRemainingTicks = cooldown.endTime - this.tickCount;
        int ticksToSubtract = Math.round(totalDuration * reductionAmount);
        int newRemainingTicks = currentRemainingTicks - ticksToSubtract;

        if (newRemainingTicks <= 0) {
            removeCooldown(ability);
            return;
        }

        int newStartTime = cooldown.startTime - ticksToSubtract;
        int newEndTime = cooldown.endTime - ticksToSubtract;
        this.cooldowns.put(ability, new CooldownInstance(newStartTime, newEndTime));
    }

    protected void onCooldownStarted(Ability ability, int ticks) {
    }

    protected void onCooldownEnded(Ability ability) {
    }

    record CooldownInstance(int startTime, int endTime) {
        public static final Codec<CooldownInstance> CODEC =
                RecordCodecBuilder.create(instance -> instance.group(
                                Codec.INT.fieldOf("startTime").forGetter(CooldownInstance::startTime),
                                Codec.INT.fieldOf("endTime").forGetter(CooldownInstance::endTime)
                        ).apply(instance, CooldownInstance::new)
                );

        public static final StreamCodec<ByteBuf, CooldownInstance> STREAM_CODEC = StreamCodec.composite(
                ByteBufCodecs.INT, CooldownInstance::startTime,
                ByteBufCodecs.INT, CooldownInstance::endTime,
                CooldownInstance::new
        );

        public int getTotalDuration() {
            return endTime - startTime;
        }
    }
}

