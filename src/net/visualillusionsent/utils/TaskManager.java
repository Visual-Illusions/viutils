package net.visualillusionsent.utils;

import java.util.ConcurrentModificationException;
import java.util.HashMap;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Runnable Task Management Object
 * <p>
 * Creates a Thread Pool for handling executing delayed and continuous tasks
 * <p>
 * This File is part of the VIUtils<br>
 * (c) 2012 Visual Illusions Entertainment <a href="http://visualillusionsent.net">http://visualillusionsent.net</a>
 * 
 * @since VIUtils 1.0
 * @version 1.0
 * @author Jason (darkdiplomat)
 */
public final class TaskManager {
    /**
     * The ThreadPool object
     */
    private final ScheduledThreadPoolExecutor threadpool;

    /**
     * The Map of Tasks
     */
    private HashMap<Runnable, ScheduledFuture<?>> tasks;

    /**
     * TaskManager instance
     */
    private static TaskManager instance;

    /**
     * Synchronization Lock Object
     */
    private static final Object lock = new Object();

    /**
     * Referneces to {@link TimeUnit} objects for less typing
     */
    private static final TimeUnit micro = TimeUnit.MICROSECONDS, milli = TimeUnit.MILLISECONDS, sec = TimeUnit.SECONDS, min = TimeUnit.MINUTES;

    static {
        instance = new TaskManager();
        scheduleContinuedTaskInMinutes(new TaskCacheClear(), 15, 15);
    }

    /**
     * Internal use Constructor to initialize the Thread Pool
     */
    private TaskManager() {
        threadpool = new ScheduledThreadPoolExecutor(128);
        threadpool.setKeepAliveTime(10, sec);
        threadpool.allowCoreThreadTimeOut(true);
        tasks = new HashMap<Runnable, ScheduledFuture<?>>();
    }

    /**
     * Executes a task immediately
     * 
     * @param task
     *            the task to execute
     */
    public static final void executeTask(Runnable task) {
        instance.threadpool.execute(task);
    }

    /**
     * Executes a task after a delay in microseconds
     * 
     * @param task
     *            the tesk to execute
     * @param delay
     *            the delay before execution
     */
    public static final ScheduledFuture<?> scheduleDelayedTaskInMicros(Runnable task, long delay) {
        ScheduledFuture<?> stask = instance.threadpool.schedule(task, delay, micro);
        instance.tasks.put(task, stask);
        return stask;
    }

    /**
     * Executes a task after a delay in milliseconds
     * 
     * @param task
     *            the tesk to execute
     * @param delay
     *            the delay before execution
     */
    public static final ScheduledFuture<?> scheduleDelayedTaskInMillis(Runnable task, long delay) {
        ScheduledFuture<?> stask = instance.threadpool.schedule(task, delay, milli);
        instance.tasks.put(task, stask);
        return stask;
    }

    /**
     * Executes a task after a delay in seconds
     * 
     * @param task
     *            the tesk to execute
     * @param delay
     *            the delay before execution
     */
    public static final ScheduledFuture<?> scheduleDelayedTaskInSeconds(Runnable task, long delay) {
        ScheduledFuture<?> stask = instance.threadpool.schedule(task, delay, sec);
        instance.tasks.put(task, stask);
        return stask;
    }

    /**
     * Executes a task after a delay in minutes
     * 
     * @param task
     *            the tesk to execute
     * @param delay
     *            the delay before execution
     */
    public static final ScheduledFuture<?> scheduleDelayedTaskInMinutes(Runnable task, long delay) {
        ScheduledFuture<?> stask = instance.threadpool.schedule(task, delay, min);
        instance.tasks.put(task, stask);
        return stask;
    }

    /**
     * Executes a task continually on a fixed delay in microseconds
     * 
     * @param task
     *            the tesk to execute
     * @param initialdelay
     *            the delay before initial execution
     * @param delay
     *            the delay between additional executions
     */
    public static final ScheduledFuture<?> scheduleContinuedTaskInMicros(Runnable task, long initialdelay, long delay) {
        ScheduledFuture<?> stask = instance.threadpool.scheduleAtFixedRate(task, initialdelay, delay, micro);
        instance.tasks.put(task, stask);
        return stask;
    }

    /**
     * Executes a task continually on a fixed delay in milliseconds
     * 
     * @param task
     *            the tesk to execute
     * @param initialdelay
     *            the delay before initial execution
     * @param delay
     *            the delay between additional executions
     */
    public static final ScheduledFuture<?> scheduleContinuedTaskInMillis(Runnable task, long initialdelay, long delay) {
        ScheduledFuture<?> stask = instance.threadpool.scheduleAtFixedRate(task, initialdelay, delay, milli);
        instance.tasks.put(task, stask);
        return stask;
    }

    /**
     * Executes a task continually on a fixed delay in seconds
     * 
     * @param task
     *            the tesk to execute
     * @param initialdelay
     *            the delay before initial execution
     * @param delay
     *            the delay between additional executions
     */
    public static final ScheduledFuture<?> scheduleContinuedTaskInSeconds(Runnable task, long initialdelay, long delay) {
        ScheduledFuture<?> stask = instance.threadpool.scheduleAtFixedRate(task, initialdelay, delay, sec);
        instance.tasks.put(task, stask);
        return stask;
    }

    /**
     * Executes a task continually on a fixed delay in minutes
     * 
     * @param task
     *            the tesk to execute
     * @param initialdelay
     *            the delay before initial execution
     * @param delay
     *            the delay between additional executions
     */
    public static final ScheduledFuture<?> scheduleContinuedTaskInMinutes(Runnable task, long initialdelay, long delay) {
        ScheduledFuture<?> stask = instance.threadpool.scheduleAtFixedRate(task, initialdelay, delay, min);
        instance.tasks.put(task, stask);
        return stask;
    }

    /**
     * Executes a task continually on a fixed delay in the specified TimeUnit
     * 
     * @param task
     *            the tesk to execute
     * @param initialdelay
     *            the delay before initial execution
     * @param delay
     *            the delay between additional executions
     * @param timeunit
     *            the {@link TimeUnit} to use
     */
    public static final ScheduledFuture<?> scheduleContinuedTask(Runnable task, long initialdelay, long delay, TimeUnit timeunit) {
        ScheduledFuture<?> stask = instance.threadpool.scheduleAtFixedRate(task, initialdelay, delay, timeunit);
        instance.tasks.put(task, stask);
        return stask;
    }

    /**
     * Runs periodically to clean up completed tasks from the cache
     */
    private static final void clearCompletedTasks() {
        synchronized (lock) {
            for (Runnable task : instance.tasks.keySet()) {
                if (instance.tasks.get(task).isDone()) {
                    instance.tasks.remove(task);
                }
            }
        }
    }

    /**
     * Removes a task from the pool
     * 
     * @param task
     *            the task to be removed
     * @return {@code true} if successfully removed, {@code false} otherwise
     */
    public static final boolean removeTask(Runnable task) {
        synchronized (lock) {
            boolean check = false;
            if (instance.tasks.containsKey(task)) {
                check = instance.tasks.get(task).cancel(true);
                instance.tasks.remove(task);
            }
            if (!check) {
                check = instance.threadpool.remove(task);
            }
            if (check) {
                instance.threadpool.purge();
            }
            return check;
        }
    }

    /**
     * Task Cache clearing class
     * <p>
     * Calls back to {@link TaskManager#clearCompletedTasks} every 15 minutes
     * 
     * @since VIUtils 1.0
     * @version 1.0
     * @author Jason (darkdiplomat)
     */
    private static final class TaskCacheClear implements Runnable { // Cleans up completed tasks
        @Override
        public void run() {
            try {
                clearCompletedTasks();
            }
            catch (ConcurrentModificationException cme) {}
        }
    }
}
