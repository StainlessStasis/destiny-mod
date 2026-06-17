package io.github.stainlessstasis.destinymod.api.block_display_fx.channel;

import net.minecraft.world.level.block.state.BlockState;

import java.util.List;

public class BlockStateChannel {
    private final List<Keyframe<BlockState>> keyframes;

    public BlockStateChannel(List<Keyframe<BlockState>> keyframes) {
        this.keyframes = List.copyOf(keyframes);
    }

    public BlockState evaluate(float t) {
        BlockState result = keyframes.getFirst().value();
        for (Keyframe<BlockState> keyframe : keyframes) {
            if (t >= keyframe.time()) {
                result = keyframe.value();
            } else {
                break;
            }
        }
        return result;
    }
}
