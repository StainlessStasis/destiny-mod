package io.github.stainlessstasis.destinymod.destiny_combat.ability.cooldown;

import io.github.stainlessstasis.destinymod.data.DestinyModAttachments;
import io.github.stainlessstasis.destinymod.destiny_combat.ability.Ability;
import com.google.common.collect.Maps;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.stainlessstasis.destinymod.destiny_combat.ability.RegisteredAbility;
import io.github.stainlessstasis.destinymod.destiny_combat.status_effect.StatusEffectManager;
import io.github.stainlessstasis.destinymod.destiny_combat.status_effect.buff.SolInvictus;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;
import org.jspecify.annotations.Nullable;

@EventBusSubscriber
public class AbilityCooldowns {
    private final Map<RegisteredAbility, CooldownInstance> cooldowns = Maps.newHashMap();
    private int tickCount;

    private static final Codec<Map<RegisteredAbility, CooldownInstance>> MAP_CODEC =
            Codec.unboundedMap(RegisteredAbility.CODEC, CooldownInstance.CODEC);

    public static final MapCodec<AbilityCooldowns> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            MAP_CODEC.fieldOf("cooldowns").forGetter(abilityCooldowns -> abilityCooldowns.cooldowns),
            Codec.INT.fieldOf("ticks").forGetter(abilityCooldowns -> abilityCooldowns.tickCount)
    ).apply(instance, AbilityCooldowns::new));

    public static final StreamCodec<ByteBuf, AbilityCooldowns> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.map(
                    HashMap::new,
                    RegisteredAbility.STREAM_CODEC,
                    CooldownInstance.STREAM_CODEC
            ),
            abilityCooldowns -> abilityCooldowns.cooldowns,
            ByteBufCodecs.INT, abilityCooldowns -> abilityCooldowns.tickCount,
            AbilityCooldowns::new
    );

    private AbilityCooldowns(Map<RegisteredAbility, CooldownInstance> cooldownInstances, int tickCount) {
        this.tickCount = tickCount;
        this.cooldowns.putAll(cooldownInstances);
    }

    public AbilityCooldowns() {}

    @SubscribeEvent
    public static void onPlayerTick(PlayerTickEvent.Post event) {
        Player player = event.getEntity();

        AbilityCooldowns abilityCooldowns = player.getData(DestinyModAttachments.ABILITY_COOLDOWNS);
        abilityCooldowns.tick(player);
    }

    public float getAbilityRegenSpeed(LivingEntity entity) {
        float regenSpeed = 1f;

        if (StatusEffectManager.isActive(entity, SolInvictus.class)) {
            regenSpeed += 9f;
        }

        return regenSpeed;
    }

    private void tick(@Nullable LivingEntity entity) {
        float regenSpeed = entity == null ? 1f : getAbilityRegenSpeed(entity);
        tick(regenSpeed);
    }

    private void tick(float regenSpeed) {
        this.tickCount++;
        if (this.cooldowns.isEmpty()) return;

        Iterator<Map.Entry<RegisteredAbility, CooldownInstance>> iterator = this.cooldowns.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<RegisteredAbility, CooldownInstance> entry = iterator.next();
            RegisteredAbility registeredAbility = entry.getKey();
            CooldownInstance cooldown = entry.getValue();

            float newProgress = cooldown.progress() + regenSpeed;
            if (newProgress >= cooldown.cooldownTicks()) {
                int newCharges = cooldown.currentCharges() + 1;
                if (newCharges >= cooldown.maxCharges()) {
                    iterator.remove();
                    onCooldownEnded(registeredAbility);
                } else {
                    float overflowProgress = newProgress - cooldown.cooldownTicks();
                    entry.setValue(new CooldownInstance(
                            newCharges,
                            cooldown.maxCharges(),
                            cooldown.cooldownTicks(),
                            overflowProgress
                    ));
                }
            } else {
                entry.setValue(new CooldownInstance(
                        cooldown.currentCharges(),
                        cooldown.maxCharges(),
                        cooldown.cooldownTicks(),
                        newProgress
                ));
            }
        }
    }


    public boolean isEmpty() {
        return cooldowns.isEmpty();
    }
    public boolean isOnCooldown(RegisteredAbility registeredAbility) {
        return !hasCharges(registeredAbility);
    }
    public boolean hasCharges(RegisteredAbility registeredAbility) {
        CooldownInstance cooldown = this.cooldowns.get(registeredAbility);
        return cooldown == null || cooldown.currentCharges() > 0;
    }

    public Set<RegisteredAbility> getAbilitiesOnCooldown() {
        return this.cooldowns.keySet();
    }

    public int getCharges(RegisteredAbility registeredAbility, RegistryAccess registryAccess) {
        CooldownInstance cooldown = this.cooldowns.get(registeredAbility);
        if (cooldown == null) {
            return registeredAbility.get(registryAccess).maxCharges();
        }
        return cooldown.currentCharges();
    }

    public float getCooldownPercent(RegisteredAbility registeredAbility, float partialTicks) {
        CooldownInstance cooldown = this.cooldowns.get(registeredAbility);
        if (cooldown != null) {
            float remaining = (float)cooldown.cooldownTicks() - (cooldown.progress() + partialTicks);
            return Math.clamp(remaining / (float)cooldown.cooldownTicks(), 0f, 1f);
        }
        return 0f;
    }

    void consumeCharge(RegisteredAbility registeredAbility, int cooldownTicks, int maxCharges) {
        CooldownInstance cooldown = this.cooldowns.get(registeredAbility);

        if (cooldown == null) {
            this.cooldowns.put(registeredAbility, new CooldownInstance(maxCharges - 1, maxCharges, cooldownTicks, 0f));
            this.onCooldownStarted(registeredAbility, cooldownTicks);
        } else if (cooldown.currentCharges() > 0) {
            this.cooldowns.put(registeredAbility, new CooldownInstance(
                    cooldown.currentCharges() - 1,
                    cooldown.maxCharges(),
                    cooldown.cooldownTicks(),
                    cooldown.progress()
            ));
        }
    }

    void addCooldown(RegisteredAbility registeredAbility, RegistryAccess registryAccess) {
        Ability ability = registeredAbility.get(registryAccess);
        CooldownInstance cooldown = this.cooldowns.get(registeredAbility);

        if (cooldown == null) {
            this.cooldowns.put(registeredAbility, new CooldownInstance(
                    ability.maxCharges() - 1,
                    ability.maxCharges(),
                    ability.cooldownTicks(),
                    0f
            ));
            this.onCooldownStarted(registeredAbility, ability.cooldownTicks());
        } else {
            int currentCharges = cooldown.currentCharges();
            int nextCharges = Math.max(0, currentCharges - 1);

            this.cooldowns.put(registeredAbility, new CooldownInstance(
                    nextCharges,
                    cooldown.maxCharges(),
                    cooldown.cooldownTicks(),
                    cooldown.progress()
            ));
            if (currentCharges == cooldown.maxCharges()) {
                this.onCooldownStarted(registeredAbility, ability.cooldownTicks());
            }
        }
    }

    void removeCooldown(RegisteredAbility registeredAbility) {
        this.cooldowns.remove(registeredAbility);
        this.onCooldownEnded(registeredAbility);
    }

    void reduceCooldownPercent(RegisteredAbility registeredAbility, float reductionAmount) {
        CooldownInstance cooldown = this.cooldowns.get(registeredAbility);
        if (cooldown == null) return;

        float progressToAdd = cooldown.cooldownTicks() * reductionAmount;
        float newProgress = cooldown.progress() + progressToAdd;

        if (newProgress < cooldown.cooldownTicks()) {
            this.cooldowns.put(registeredAbility, new CooldownInstance(
                    cooldown.currentCharges(),
                    cooldown.maxCharges(),
                    cooldown.cooldownTicks(),
                    newProgress
            ));
            return;
        }

        int nextCharges = cooldown.currentCharges() + 1;
        if (nextCharges >= cooldown.maxCharges()) {
            this.cooldowns.remove(registeredAbility);
            this.onCooldownEnded(registeredAbility);
        } else {
            this.cooldowns.put(registeredAbility, new CooldownInstance(
                    nextCharges,
                    cooldown.maxCharges(),
                    cooldown.cooldownTicks(),
                    newProgress - cooldown.cooldownTicks()
            ));
        }
    }

    protected void onCooldownStarted(RegisteredAbility registeredAbility, int ticks) {}

    protected void onCooldownEnded(RegisteredAbility registeredAbility) {}

    record CooldownInstance(int currentCharges, int maxCharges, int cooldownTicks, float progress) {
        public static final Codec<CooldownInstance> CODEC =
                RecordCodecBuilder.create(instance -> instance.group(
                            Codec.INT.fieldOf("currentCharges").forGetter(CooldownInstance::currentCharges),
                            Codec.INT.fieldOf("maxCharges").forGetter(CooldownInstance::maxCharges),
                            Codec.INT.fieldOf("cooldownTicks").forGetter(CooldownInstance::cooldownTicks),
                            Codec.FLOAT.fieldOf("progress").forGetter(CooldownInstance::progress)
                        ).apply(instance, CooldownInstance::new)
                );

        public static final StreamCodec<ByteBuf, CooldownInstance> STREAM_CODEC = StreamCodec.composite(
                ByteBufCodecs.INT, CooldownInstance::currentCharges,
                ByteBufCodecs.INT, CooldownInstance::maxCharges,
                ByteBufCodecs.INT, CooldownInstance::cooldownTicks,
                ByteBufCodecs.FLOAT, CooldownInstance::progress,
                CooldownInstance::new
        );
    }
}

