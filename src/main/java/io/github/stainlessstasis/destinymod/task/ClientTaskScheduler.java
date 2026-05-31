package io.github.stainlessstasis.destinymod.task;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientPlayerNetworkEvent;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.RenderFrameEvent;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

@EventBusSubscriber(value = Dist.CLIENT)
public class ClientTaskScheduler extends TaskScheduler {
    public static final ClientTaskScheduler INSTANCE = new ClientTaskScheduler();
    protected final Queue<Task> frameTaskQueue = new ConcurrentLinkedQueue<>();
    protected final Map<UUID, Task> frameRunningTasks = new ConcurrentHashMap<>();
    private final List<Task> clientTickCache = new ArrayList<>();
    private final List<Task> frameTickCache = new ArrayList<>();

    private ClientTaskScheduler(){}

    private void cleanup() {
        taskQueue.clear();
        runningTasks.clear();
        frameTaskQueue.clear();
        frameRunningTasks.clear();
    }

    public UUID runTaskRepeatingEachFrame(int delayFrames, int repeatInterval, @NotNull CancellableRunnable task) {
        return runTaskRepeating(delayFrames, repeatInterval, task, frameTaskQueue, frameRunningTasks);
    }

    public boolean cancelFrameTask(UUID taskId) {
        return cancelTask(taskId, frameRunningTasks);
    }

    @Override
    protected void tick() {
        tick(taskQueue, runningTasks, clientTickCache);
    }

    protected void tickFrame() {
        tick(frameTaskQueue, frameRunningTasks, frameTickCache);
    }

    @SubscribeEvent
    public static void onClientTick(ClientTickEvent.Post event) {
        INSTANCE.tick();
    }

    @SubscribeEvent
    public static void onRenderFrame(RenderFrameEvent.Post event) {
        INSTANCE.tickFrame();
    }

    @SubscribeEvent
    public static void onLogin(ClientPlayerNetworkEvent.LoggingIn event) {
        INSTANCE.cleanup();
    }

    @SubscribeEvent
    public static void onLogout(ClientPlayerNetworkEvent.LoggingOut event) {
        INSTANCE.cleanup();
    }
}
