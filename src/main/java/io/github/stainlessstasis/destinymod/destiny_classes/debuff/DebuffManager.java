package io.github.stainlessstasis.destinymod.destiny_classes.debuff;

import io.github.stainlessstasis.destinymod.data.DestinyModAttachments;
import net.minecraft.world.entity.LivingEntity;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.event.tick.EntityTickEvent;

import java.util.List;
import java.util.function.Supplier;

@EventBusSubscriber
public class DebuffManager {
    private static final List<Supplier<? extends AttachmentType<? extends Debuff>>> DEBUFFS = List.of(DestinyModAttachments.SCORCH);

    public static boolean isActive(LivingEntity entity, Supplier<? extends AttachmentType<? extends Debuff>> debuffAttachment) {
        if (!entity.hasData(debuffAttachment.get())) return false;
        return entity.getData(debuffAttachment.get()).isActive();
    }

    public static void applyScorch(LivingEntity target, int stacks) {
        Scorch scorch = target.getData(DestinyModAttachments.SCORCH);
        scorch.addStacks(stacks);
    }

    @SubscribeEvent
    public static void onEntityTick(EntityTickEvent.Post event) {
        if (!(event.getEntity() instanceof LivingEntity entity) || entity.level().isClientSide()) return;

        for (var debuffSupplier : DEBUFFS) {
            if (entity.hasData(debuffSupplier.get())) {
                Debuff debuff = entity.getData(debuffSupplier.get());

                if (debuff.isActive()) {
                    debuff.tick(entity);
                } else {
                    entity.removeData(debuffSupplier.get());
                }
            }
        }
    }
}
