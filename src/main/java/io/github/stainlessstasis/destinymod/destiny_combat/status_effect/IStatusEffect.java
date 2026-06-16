package io.github.stainlessstasis.destinymod.destiny_combat.status_effect;

import net.minecraft.world.entity.EntityReference;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.attachment.AttachmentType;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Supplier;

public interface IStatusEffect {
    boolean isActive();
    void tick (LivingEntity entity);
    void clear(LivingEntity entity);

    Map<Class<? extends IStatusEffect>, EffectAttachmentCache> CACHE = new HashMap<>();
    List<Supplier<? extends AttachmentType<? extends IStatusEffect>>> TICKABLE_EFFECTS = new CopyOnWriteArrayList<>();
    Supplier<AttachmentType<Boolean>> getClientStateSyncAttachment();
    Supplier<? extends AttachmentType<? extends IStatusEffect>> getAttachment();

    static EffectAttachmentCache getOrCreateCache(Class<? extends IStatusEffect> clazz) {
        return CACHE.computeIfAbsent(clazz, key -> {
            try {
                IStatusEffect dummy = key.getDeclaredConstructor().newInstance();
                EffectAttachmentCache newCache = new EffectAttachmentCache(dummy.getAttachment(), dummy.getClientStateSyncAttachment());
                TICKABLE_EFFECTS.add(newCache.serverAttachment());
                return newCache;
            } catch (Exception e) {
                throw new RuntimeException("Failed to cache StatusEffect attachments for " + key.getName() + ". Does it have a public no-arg constructor?", e);
            }
        });
    }

    record EffectAttachmentCache(
            Supplier<? extends AttachmentType<? extends IStatusEffect>> serverAttachment,
            Supplier<AttachmentType<Boolean>> clientAttachment
    ) {}

    default @Nullable EntityReference<LivingEntity> getAttackerReference() {
        return null;
    }
    default Optional<LivingEntity> getOwnerOptional(Level level) {
        return Optional.ofNullable(getOwner(level));
    }
    default @Nullable LivingEntity getOwner(Level level) {
        EntityReference<LivingEntity> ref = this.getAttackerReference();
        return ref == null ? null : EntityReference.getLivingEntity(ref, level);
    }
}
