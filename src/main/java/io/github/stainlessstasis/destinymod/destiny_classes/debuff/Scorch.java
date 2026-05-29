package io.github.stainlessstasis.destinymod.destiny_classes.debuff;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.entity.LivingEntity;

public class Scorch implements Debuff {
    public static final int IGNITION_THRESHOLD = 100;
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
//        if (this.stacks >= IGNITION_THRESHOLD) {
            // TODO: ignitions here
//        }

        // TODO: DoT
        System.out.println("SCORCH TICKING | STACKS REMAINING: "+this.stacks);

        if (this.decayDelay > 0) {
            System.out.println("SCORCH IS STILL ON DECAY DELAY");
            this.decayDelay--;
        } else {
            this.stacks = Math.max(0, this.stacks-1);
        }
    }

    @Override
    public void clear() {
        this.stacks = 0;
        this.decayDelay = 0;
    }
}
