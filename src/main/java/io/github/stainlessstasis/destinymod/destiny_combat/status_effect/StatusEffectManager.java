package io.github.stainlessstasis.destinymod.destiny_combat.status_effect;

import io.github.stainlessstasis.destinymod.data.DestinyModAttachments;
import io.github.stainlessstasis.destinymod.destiny_combat.status_effect.debuff.Scorch;
import net.minecraft.world.entity.LivingEntity;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.event.tick.EntityTickEvent;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.Supplier;

@EventBusSubscriber
public class StatusEffectManager {
    private static final List<Supplier<? extends AttachmentType<? extends StatusEffect>>> STATUS_EFFECTS = List.of(DestinyModAttachments.SCORCH);

    public static boolean isActiveOnServer(LivingEntity entity, Supplier<? extends AttachmentType<? extends StatusEffect>> attachment) {
        if (!entity.hasData(attachment.get())) return false;
        return entity.getData(attachment.get()).isActive();
    }

    public static void applyScorch(LivingEntity target, @Nullable LivingEntity attacker, int stacks) {
        Scorch scorch = target.getData(DestinyModAttachments.SCORCH);
        scorch.setOwner(attacker);
        scorch.addStacks(stacks);
    }

    @SubscribeEvent
    public static void onEntityTick(EntityTickEvent.Post event) {
        if (!(event.getEntity() instanceof LivingEntity entity) || entity.level().isClientSide()) return;

        for (var effectSupplier : STATUS_EFFECTS) {
            if (entity.hasData(effectSupplier.get())) {
                StatusEffect effect = entity.getData(effectSupplier.get());

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
