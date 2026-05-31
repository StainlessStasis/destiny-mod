package io.github.stainlessstasis.destinymod.task;

public abstract class CancellableRunnable implements Runnable {
    private boolean cancelled = false;
    private int currentIteration = 0;
    private long timeElapsed = 0;
    private final long initialTime = System.currentTimeMillis();

    public void cancel() {
        this.cancelled = true;
    }
    public boolean isCancelled() {
        return this.cancelled;
    }
    public int getCurrentIteration(){return this.currentIteration;}

    /**
     * Gets the timestamp when the runnable was created, in milliseconds.
     */
    public long getInitialTime(){return this.initialTime;}
    /**
     * Gets the time since the runnable was created, in milliseconds.
     */
    public long getTimeElapsed(){return this.timeElapsed;}

    @Override
    public void run() {
        if (!isCancelled()) {
            timeElapsed = System.currentTimeMillis()-initialTime;
            execute();
            currentIteration++;
        }
    }

    protected abstract void execute();
}
