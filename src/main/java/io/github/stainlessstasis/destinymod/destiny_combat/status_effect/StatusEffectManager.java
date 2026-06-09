package io.github.stainlessstasis.destinymod.destiny_combat.status_effect;

import io.github.stainlessstasis.destinymod.data.DestinyModAttachments;
import io.github.stainlessstasis.destinymod.destiny_combat.status_effect.buff.SolInvictus;
import io.github.stainlessstasis.destinymod.destiny_combat.status_effect.debuff.MeltingPoint;
import io.github.stainlessstasis.destinymod.destiny_combat.status_effect.debuff.Scorch;
import net.minecraft.world.entity.LivingEntity;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.event.tick.EntityTickEvent;
import org.jetbrains.annotations.Nullable;

import java.util.function.Supplier;

@EventBusSubscriber
public class StatusEffectManager {
    private static <T extends IStatusEffect> T getAndRegisterEffect(LivingEntity entity, Class<T> effectClass, Supplier<? extends AttachmentType<T>> attachmentSupplier) {
        IStatusEffect.getOrCreateCache(effectClass);
        return entity.getData(attachmentSupplier.get());
    }

    public static boolean isActive(LivingEntity entity, Class<? extends IStatusEffect> statusEffectClass) {
        IStatusEffect.EffectAttachmentCache cache = IStatusEffect.getOrCreateCache(statusEffectClass);

        if (entity.level().isClientSide()) {
            return isActiveOnClient(entity, cache.clientAttachment());
        } else {
            return isActiveOnServer(entity, cache.serverAttachment());
        }
    }

    private static boolean isActiveOnServer(LivingEntity entity, Supplier<? extends AttachmentType<? extends IStatusEffect>> attachment) {
        if (!entity.hasData(attachment.get())) return false;
        return entity.getData(attachment.get()).isActive();
    }

    private static boolean isActiveOnClient(LivingEntity entity, Supplier<? extends AttachmentType<Boolean>> attachment) {
        if (!entity.hasData(attachment.get())) return false;
        return entity.getData(attachment.get());
    }

    public static @Nullable <T extends IStatusEffect> T getInstance(LivingEntity entity, Class<T> statusEffectClass) {
        if (entity.level().isClientSide()) return null;
        var attachment = IStatusEffect.getOrCreateCache(statusEffectClass).serverAttachment().get();
        if (!entity.hasData(attachment)) return null;
        return (T) entity.getData(attachment);
    }

    public static void applyScorch(LivingEntity target, @Nullable LivingEntity attacker, int stacks) {
        Scorch scorch = getAndRegisterEffect(target, Scorch.class, DestinyModAttachments.SCORCH);
        scorch.setOwner(attacker);
        scorch.addStacks(target, stacks);
    }

    public static void applySolInvictus(LivingEntity entity) {
        applySolInvictus(entity, SolInvictus.DEFAULT_TICKS);
    }

    public static void applySolInvictus(LivingEntity entity, int ticks) {
        SolInvictus sol = getAndRegisterEffect(entity, SolInvictus.class, DestinyModAttachments.SOL_INVICTUS);
        sol.setRemainingTicks(ticks);
    }

    public static void applyMeltingPoint(LivingEntity entity) {
        getAndRegisterEffect(entity, MeltingPoint.class, DestinyModAttachments.MELTING_POINT);
    }

    public static void applyMeltingPoint(LivingEntity entity, int ticks) {
        MeltingPoint mp = getAndRegisterEffect(entity, MeltingPoint.class, DestinyModAttachments.MELTING_POINT);
        mp.setRemainingTicks(ticks);
    }

    @SubscribeEvent
    public static void onEntityTick(EntityTickEvent.Post event) {
        if (!(event.getEntity() instanceof LivingEntity entity) || entity.level().isClientSide()) return;

        for (var effectSupplier : IStatusEffect.TICKABLE_EFFECTS) {
            if (entity.hasData(effectSupplier.get())) {
                IStatusEffect effect = entity.getData(effectSupplier.get());

                if (effect.isActive()) {
                    effect.tick(entity);
                } else {
                    effect.clear(entity);
                    entity.removeData(effectSupplier.get());
                }
            }
        }
    }
}
