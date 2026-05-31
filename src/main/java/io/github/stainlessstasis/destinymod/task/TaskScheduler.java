package io.github.stainlessstasis.destinymod.task;

import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

public abstract class TaskScheduler {
    protected final Queue<Task> taskQueue = new ConcurrentLinkedQueue<>();
    protected final Map<UUID, Task> runningTasks = new ConcurrentHashMap<>();
    private final List<Task> tickCache = new ArrayList<>();

    protected TaskScheduler(){}

    public UUID runTaskLater(int delayTicks, @NotNull CancellableRunnable task) {
        if (delayTicks < 0) delayTicks = 0;

        Task newTask = new Task(delayTicks, task, false);
        UUID id = UUID.randomUUID();
        newTask.setId(id);
        taskQueue.add(newTask);
        runningTasks.put(id, newTask);
        return id;
    }

    public UUID runTaskMultiple(int numTasks, int initialDelayTicks, int intervalTicks, @NotNull CancellableRunnable task) {
        if (numTasks < 1) numTasks = 1;
        if (initialDelayTicks < 0) initialDelayTicks = 0;
        if (intervalTicks < 1) intervalTicks = 1;

        Task newTask = new Task(initialDelayTicks, task, true, intervalTicks, numTasks);
        UUID id = UUID.randomUUID();
        newTask.setId(id);

        runningTasks.put(id, newTask);
        taskQueue.add(newTask);
        return id;
    }

    public UUID runTaskRepeating(int delayTicks, int repeatInterval, @NotNull CancellableRunnable task) {
        return runTaskRepeating(delayTicks, repeatInterval, task, taskQueue, runningTasks );
    }

    protected UUID runTaskRepeating(int delayTicks, int repeatInterval, @NotNull CancellableRunnable task, Queue<Task> taskQueue, Map<UUID, Task> runningTasks) {
        if (delayTicks < 0) delayTicks = 0;
        if (repeatInterval < 1) repeatInterval = 1;

        Task newTask = new Task(delayTicks, task, true, repeatInterval);
        UUID id = UUID.randomUUID();
        newTask.setId(id);
        taskQueue.add(newTask);
        runningTasks.put(id, newTask);
        return id;
    }

    public boolean cancelTask(UUID taskId) {
        return cancelTask(taskId, runningTasks);
    }

    protected boolean cancelTask(UUID taskId, Map<UUID, Task> runningTasks) {
        Task task = runningTasks.remove(taskId);
        if (task != null) {
            task.getRunnable().cancel();
            return true;
        }
        return false;
    }

    protected void tick() {
        tick(taskQueue, runningTasks, tickCache);
    }

    protected void tick(Queue<Task> taskQueue, Map<UUID, Task> runningTasks, List<Task> processingCache) {
        if (taskQueue.isEmpty()) return;

        processingCache.clear();
        Task currentTask;
        while ((currentTask = taskQueue.poll()) != null) {
            processingCache.add(currentTask);
        }

        for (Task task : processingCache) {
            if (task.tick()) {
                taskQueue.add(task);
            } else {
                runningTasks.remove(task.getId());
            }
        }
        processingCache.clear();
    }

    protected static class Task {
        private int ticksRemaining;
        private final CancellableRunnable runnable;
        private final boolean isRepeating;
        private final int repeatInterval;
        private final int maxIterations;
        private int iterations = 0;
        private UUID id;

        Task(int ticksRemaining, CancellableRunnable runnable, boolean isRepeating) {
            this(ticksRemaining, runnable, isRepeating, 1, -1);
        }

        Task(int ticksRemaining, CancellableRunnable runnable, boolean isRepeating, int repeatInterval) {
            this(ticksRemaining, runnable, isRepeating, repeatInterval, -1);
        }

        Task(int ticksRemaining, CancellableRunnable runnable, boolean isRepeating, int repeatInterval, int maxIterations) {
            this.ticksRemaining = ticksRemaining;
            this.runnable = runnable;
            this.isRepeating = isRepeating;
            this.repeatInterval = repeatInterval;
            this.maxIterations = maxIterations;
        }

        void setId(UUID id) {
            this.id = id;
        }
        UUID getId() {
            return this.id;
        }
        CancellableRunnable getRunnable() { return this.runnable; }

        /**
         * @return true if the task should be kept in queue, and false if the task is finished
         */
        boolean tick() {
            if (this.runnable.isCancelled()) {
                return false;
            }

            if (ticksRemaining > 0) {
                ticksRemaining--;
                return true;
            }

            runnable.run();
            iterations++;

            if (this.runnable.isCancelled() || ((maxIterations > 0 && iterations >= maxIterations))) {
                return false;
            }

            if (isRepeating) {
                ticksRemaining = repeatInterval - 1;
                return true;
            }

            return false;
        }
    }
}
