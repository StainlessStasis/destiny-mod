package io.github.stainlessstasis.destinymod.task;

import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.server.ServerAboutToStartEvent;
import net.neoforged.neoforge.event.server.ServerStartedEvent;
import net.neoforged.neoforge.event.tick.ServerTickEvent;

@EventBusSubscriber
public class ServerTaskScheduler extends TaskScheduler {
    public static final ServerTaskScheduler INSTANCE = new ServerTaskScheduler();

    private ServerTaskScheduler(){}

    private void cleanup() {
        taskQueue.clear();
        runningTasks.clear();
    }

    @SubscribeEvent
    public static void onServerTick(ServerTickEvent.Post event) {
        INSTANCE.tick();
    }

    @SubscribeEvent
    public static void onServerAboutToStart(ServerAboutToStartEvent event) {
        INSTANCE.cleanup();
    }

    @SubscribeEvent
    public static void onServerStopping(net.neoforged.neoforge.event.server.ServerStoppingEvent event) {
        INSTANCE.cleanup();
    }
}
