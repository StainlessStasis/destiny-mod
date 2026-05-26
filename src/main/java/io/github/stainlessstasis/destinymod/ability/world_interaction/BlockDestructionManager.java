package io.github.stainlessstasis.destinymod.ability.world_interaction;

import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class BlockDestructionManager {
    private static final Map<GlobalPos, DestroyState> TRACKED_DESTRUCTIONS = new ConcurrentHashMap<>();

    public static void addDamage(ServerLevel level, BlockPos blockPos, float damage, @Nullable Entity breaker, boolean allowMulti, boolean enableDrops) {
        BlockState state = level.getBlockState(blockPos);
        float breakSpeed = state.getDestroySpeed(level, blockPos);
        if (breakSpeed < 0) return; // unbreakable blocks

        GlobalPos pos = new GlobalPos(level.dimension(), blockPos.immutable());
        long gameTime = level.getGameTime();

        TRACKED_DESTRUCTIONS.compute(pos, (p, current) -> {
            int breakerID;
            float newDamage;

            if (current == null) {
                breakerID = blockPos.hashCode() + (int)level.getGameTime();
                if (allowMulti && breaker != null) breakerID += breaker.getId();
                newDamage = damage;
            } else {
                breakerID = current.breakerID();
                newDamage = damage + current.damage();
            }

            return new DestroyState(newDamage, breakerID, gameTime, enableDrops);
        });

        var destroyState = TRACKED_DESTRUCTIONS.get(pos);
        if (destroyState != null) {
            updateBlock(level, blockPos, destroyState.breakerID(), breaker);
        }
    }

    private static void updateBlock(ServerLevel level, BlockPos blockPos, int breakerID, @Nullable Entity breaker) {
        GlobalPos pos = new GlobalPos(level.dimension(), blockPos);
        if (!TRACKED_DESTRUCTIONS.containsKey(pos)) return;
        var destroyState = TRACKED_DESTRUCTIONS.get(pos);
        if (destroyState == null) return;

        BlockState block = level.getBlockState(blockPos);
        float damage = destroyState.damage;
        float breakSpeed = block.getDestroySpeed(level, blockPos);
        if (damage > breakSpeed) {
            breakBlock(level, blockPos, breakerID, breaker, destroyState.enableDrops);
            return;
        }

        int destroyStage = (int) ((destroyState.damage() / breakSpeed) * 10);
        destroyStage = Math.min(9, Math.max(0, destroyStage)); // there are 10 visual stages from 0 to 9

        level.destroyBlockProgress(breakerID, blockPos, destroyStage);
    }

    private static void breakBlock(ServerLevel level, BlockPos blockPos, int breakerID, @Nullable Entity breaker, boolean enableDrops) {
        level.destroyBlockProgress(breakerID, blockPos, -1);
        TRACKED_DESTRUCTIONS.remove(new GlobalPos(level.dimension(), blockPos));
        level.destroyBlock(blockPos, enableDrops, breaker);
    }

    public static void removeInactive(MinecraftServer server, long currentTime) {
        if (currentTime % 100 != 0) return; // only run this shit once every 5 seconds

        TRACKED_DESTRUCTIONS.entrySet().removeIf(entry -> {
            if ((currentTime - entry.getValue().lastHitTime()) > 100) {
                GlobalPos pos = entry.getKey();
                ServerLevel level = server.getLevel(pos.dimension());

                if (level != null) {
                    level.destroyBlockProgress(entry.getValue().breakerID(), pos.blockPos(), -1);
                }

                return true;
            }
            return false;
        });
    }

    public static void cleanup() {
        TRACKED_DESTRUCTIONS.clear();
    }

    private record GlobalPos(ResourceKey<Level> dimension, BlockPos blockPos){}
    private record DestroyState(float damage, int breakerID, long lastHitTime, boolean enableDrops) { }
}

