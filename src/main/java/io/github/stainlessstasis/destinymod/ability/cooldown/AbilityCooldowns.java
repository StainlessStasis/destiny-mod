package io.github.stainlessstasis.destinymod.ability.cooldown;

import io.github.stainlessstasis.destinymod.ability.Ability;
import io.github.stainlessstasis.destinymod.ability.AbilityType;
import com.google.common.collect.Maps;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.function.Function;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.Mth;

public class AbilityCooldowns {
    private final Map<AbilityType, CooldownInstance> cooldowns = Maps.newHashMap();
    private int tickCount;

    private static final Codec<Map<AbilityType, CooldownInstance>> MAP_CODEC = Codec.unboundedMap(AbilityType.CODEC, CooldownInstance.CODEC);

    public static final MapCodec<AbilityCooldowns> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            MAP_CODEC.fieldOf("cooldowns").forGetter(abilityCooldowns -> abilityCooldowns.cooldowns),
            Codec.INT.fieldOf("ticks").forGetter(abilityCooldowns -> abilityCooldowns.tickCount)
    ).apply(instance, AbilityCooldowns::new));

    public static final StreamCodec<ByteBuf, AbilityCooldowns> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.map(
                    HashMap::new,
                    ByteBufCodecs.fromCodec(AbilityType.CODEC),
                    CooldownInstance.STREAM_CODEC
            ),
            abilityCooldowns -> abilityCooldowns.cooldowns,
            ByteBufCodecs.INT, abilityCooldowns -> abilityCooldowns.tickCount,
            AbilityCooldowns::new
    );

    private AbilityCooldowns(Map<AbilityType, CooldownInstance> cooldownInstances, int tickCount) {
        this.tickCount = tickCount;
        this.cooldowns.putAll(cooldownInstances);
    }

    public AbilityCooldowns() {}

    public boolean isEmpty() {
        return cooldowns.isEmpty();
    }

    public boolean isOnCooldown(AbilityType type) {
        return !hasCharges(type);
    }

    public boolean hasCharges(AbilityType type) {
        CooldownInstance cooldown = this.cooldowns.get(type);
        if (cooldown == null) return true;
        return cooldown.currentCharges > 0;
    }

    public float getCooldownPercent(AbilityType type, float partialTicks) {
        CooldownInstance cooldown = this.cooldowns.get(type);
        if (cooldown != null) {
            float f = (float)(cooldown.endTime - cooldown.startTime);
            float f1 = (float)cooldown.endTime - ((float)this.tickCount + partialTicks);
            return Mth.clamp(f1 / f, 0.0F, 1.0F);
        } else {
            return 0.0F;
        }
    }

    public void tick(Function<AbilityType, Ability> abilityLookup) {
        ++this.tickCount;
        if (this.cooldowns.isEmpty()) return;

        Iterator<Map.Entry<AbilityType, CooldownInstance>> iterator = this.cooldowns.entrySet().iterator();

        while (iterator.hasNext()) {
            Map.Entry<AbilityType, CooldownInstance> entry = iterator.next();
            AbilityType type = entry.getKey();
            CooldownInstance instance = entry.getValue();

            if (instance.endTime <= this.tickCount) {
                Ability ability = abilityLookup.apply(type);
                int nextCharges = instance.currentCharges() + 1;

                if (nextCharges >= ability.maxCharges()) {
                    iterator.remove();
                    this.onCooldownEnded(type);
                } else {
                    entry.setValue(new CooldownInstance(
                            nextCharges,
                            this.tickCount,
                            this.tickCount + ability.cooldownTicks()
                    ));
                }
            }
        }
    }

    public void consumeCharge(AbilityType type, int cooldownTicks, int maxCharges) {
        CooldownInstance instance = this.cooldowns.get(type);

        if (instance == null) {
            this.cooldowns.put(type, new CooldownInstance(maxCharges - 1, this.tickCount, this.tickCount + cooldownTicks));
            this.onCooldownStarted(type, cooldownTicks);
        } else if (instance.currentCharges() > 0) {
            this.cooldowns.put(type, new CooldownInstance(
                    instance.currentCharges() - 1,
                    instance.startTime(),
                    instance.endTime()
            ));
        }
    }

    void addCooldown(AbilityType type, int cooldownTicks, int maxCharges) {
        this.cooldowns.put(type, new CooldownInstance(maxCharges, this.tickCount, this.tickCount + cooldownTicks));
        this.onCooldownStarted(type, cooldownTicks);
    }

    void removeCooldown(AbilityType type) {
        this.cooldowns.remove(type);
        this.onCooldownEnded(type);
    }

    void reduceCooldownPercent(AbilityType type, float reductionAmount) {
        CooldownInstance cooldown = this.cooldowns.get(type);
        if (cooldown == null) {
            return;
        }

        int totalDuration = cooldown.getTotalDuration();
        int currentRemainingTicks = cooldown.endTime - this.tickCount;
        int ticksToSubtract = Math.round(totalDuration * reductionAmount);
        int newRemainingTicks = currentRemainingTicks - ticksToSubtract;

        if (newRemainingTicks <= 0) {
            removeCooldown(type);
            return;
        }

        int newStartTime = cooldown.startTime - ticksToSubtract;
        int newEndTime = cooldown.endTime - ticksToSubtract;
        this.cooldowns.put(type, new CooldownInstance(newStartTime, newEndTime));
    }

    protected void onCooldownStarted(AbilityType type, int ticks) {
    }

    protected void onCooldownEnded(AbilityType type) {
    }

    record CooldownInstance(int currentCharges, int startTime, int endTime) {
        public static final Codec<CooldownInstance> CODEC =
                RecordCodecBuilder.create(instance -> instance.group(
                                Codec.INT.fieldOf("currentCharges").forGetter(CooldownInstance::currentCharges),
                                Codec.INT.fieldOf("startTime").forGetter(CooldownInstance::startTime),
                                Codec.INT.fieldOf("endTime").forGetter(CooldownInstance::endTime)
                        ).apply(instance, CooldownInstance::new)
                );

        public static final StreamCodec<ByteBuf, CooldownInstance> STREAM_CODEC = StreamCodec.composite(
                ByteBufCodecs.INT, CooldownInstance::currentCharges,
                ByteBufCodecs.INT, CooldownInstance::startTime,
                ByteBufCodecs.INT, CooldownInstance::endTime,
                CooldownInstance::new
        );

        public int getTotalDuration() {
            return endTime - startTime;
        }
    }
}

