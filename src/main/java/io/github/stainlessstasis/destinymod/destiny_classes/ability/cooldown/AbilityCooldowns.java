package io.github.stainlessstasis.destinymod.destiny_classes.ability.cooldown;

import io.github.stainlessstasis.destinymod.destiny_classes.ability.Ability;
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
        return !hasCharges(ability);
    }

    public boolean hasCharges(Ability ability) {
        return getCharges(ability) > 0;
    }

    public int getCharges(Ability ability) {
        CooldownInstance cooldown = this.cooldowns.get(ability);
        if (cooldown == null) return ability.maxCharges();
        return cooldown.currentCharges();
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
        if (this.cooldowns.isEmpty()) return;

        Iterator<Map.Entry<Ability, CooldownInstance>> iterator = this.cooldowns.entrySet().iterator();

        while (iterator.hasNext()) {
            Map.Entry<Ability, CooldownInstance> entry = iterator.next();
            Ability ability = entry.getKey();
            CooldownInstance instance = entry.getValue();

            if (instance.endTime <= this.tickCount) {
                int nextCharges = instance.currentCharges() + 1;

                if (nextCharges >= instance.maxCharges()) {
                    iterator.remove();
                    this.onCooldownEnded(ability);
                } else {
                    entry.setValue(new CooldownInstance(
                            nextCharges,
                            instance.maxCharges(),
                            instance.cooldownTicks(),
                            this.tickCount,
                            this.tickCount + instance.cooldownTicks()
                    ));
                }
            }
        }
    }

    void consumeCharge(Ability ability, int cooldownTicks, int maxCharges) {
        CooldownInstance instance = this.cooldowns.get(ability);

        if (instance == null) {
            this.cooldowns.put(ability, new CooldownInstance(maxCharges - 1, maxCharges, cooldownTicks, this.tickCount, this.tickCount + cooldownTicks));
            this.onCooldownStarted(ability, cooldownTicks);
        } else if (instance.currentCharges() > 0) {
            this.cooldowns.put(ability, new CooldownInstance(
                    instance.currentCharges() - 1,
                    instance.maxCharges(),
                    instance.cooldownTicks(),
                    instance.startTime(),
                    instance.endTime()
            ));
        }
    }

    void addCooldown(Ability ability) {
        CooldownInstance instance = this.cooldowns.get(ability);

        if (instance == null) {
            this.cooldowns.put(ability, new CooldownInstance(
                    ability.maxCharges() - 1,
                    ability.maxCharges(),
                    ability.cooldownTicks(),
                    this.tickCount,
                    this.tickCount + ability.cooldownTicks()
            ));
            this.onCooldownStarted(ability, ability.cooldownTicks());
        } else {
            int currentCharges = instance.currentCharges();
            int nextCharges = Math.max(0, currentCharges - 1);

            this.cooldowns.put(ability, new CooldownInstance(
                    nextCharges,
                    instance.maxCharges(),
                    instance.cooldownTicks(),
                    instance.startTime(),
                    instance.endTime()
            ));
            if (currentCharges == instance.maxCharges()) {
                this.onCooldownStarted(ability, ability.cooldownTicks());
            }
        }
    }

    void removeCooldown(Ability ability) {
        this.cooldowns.remove(ability);
        this.onCooldownEnded(ability);
    }

    void reduceCooldownPercent(Ability ability, float reductionAmount) {
        CooldownInstance cooldown = this.cooldowns.get(ability);
        if (cooldown == null) return;

        int totalDuration = cooldown.getTotalDuration();
        int currentRemainingTicks = cooldown.endTime - this.tickCount;
        int ticksToSubtract = Math.round(totalDuration * reductionAmount);
        int newRemainingTicks = currentRemainingTicks - ticksToSubtract;

        if (newRemainingTicks > 0) {
            int newStartTime = cooldown.startTime - ticksToSubtract;
            int newEndTime = cooldown.endTime - ticksToSubtract;
            this.cooldowns.put(ability, new CooldownInstance(
                    cooldown.currentCharges(),
                    cooldown.maxCharges(),
                    cooldown.cooldownTicks(),
                    newStartTime,
                    newEndTime
            ));
            return;
        }

        int nextCharges = cooldown.currentCharges() + 1;
        if (nextCharges >= cooldown.maxCharges()) {
            this.cooldowns.remove(ability);
            this.onCooldownEnded(ability);
        } else {
            this.cooldowns.put(ability, new CooldownInstance(
                    nextCharges,
                    cooldown.maxCharges(),
                    cooldown.cooldownTicks(),
                    this.tickCount,
                    this.tickCount + cooldown.cooldownTicks()
            ));
        }
    }

    protected void onCooldownStarted(Ability ability, int ticks) {
    }

    protected void onCooldownEnded(Ability ability) {
    }

    record CooldownInstance(int currentCharges, int maxCharges, int cooldownTicks, int startTime, int endTime) {
        public static final Codec<CooldownInstance> CODEC =
                RecordCodecBuilder.create(instance -> instance.group(
                            Codec.INT.fieldOf("currentCharges").forGetter(CooldownInstance::currentCharges),
                            Codec.INT.fieldOf("maxCharges").forGetter(CooldownInstance::maxCharges),
                            // cooldownTicks doesn't technically need to exist right now, but getTotalDuration may change in the future
                            Codec.INT.fieldOf("cooldownTicks").forGetter(CooldownInstance::cooldownTicks),
                            Codec.INT.fieldOf("startTime").forGetter(CooldownInstance::startTime),
                            Codec.INT.fieldOf("endTime").forGetter(CooldownInstance::endTime)
                        ).apply(instance, CooldownInstance::new)
                );

        public static final StreamCodec<ByteBuf, CooldownInstance> STREAM_CODEC = StreamCodec.composite(
                ByteBufCodecs.INT, CooldownInstance::currentCharges,
                ByteBufCodecs.INT, CooldownInstance::maxCharges,
                ByteBufCodecs.INT, CooldownInstance::cooldownTicks,
                ByteBufCodecs.INT, CooldownInstance::startTime,
                ByteBufCodecs.INT, CooldownInstance::endTime,
                CooldownInstance::new
        );

        public int getTotalDuration() {
            return endTime - startTime;
        }
    }
}

