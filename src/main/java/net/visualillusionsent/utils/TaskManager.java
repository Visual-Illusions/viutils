/*
 * Copyright © 2012-2013 Visual Illusions Entertainment.
 *  
 * This file is part of VIUtils.
 *
 * VIUtils is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * VIUtils is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; 
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License along with VIUtils.
 * If not, see http://www.gnu.org/licenses/lgpl.html
 */
package net.visualillusionsent.utils;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Runnable Task Management Object
 * <p>
 * Creates a Thread Pool for handling executing delayed and continuous tasks
 * 
 * @since 1.0
 * @version 1.0
 * @author Jason (darkdiplomat)
 */
public final class TaskManager {
    /**
     * The ThreadPool object
     */
    private ScheduledThreadPoolExecutor threadpool;

    /**
     * The Map of Tasks
     */
    private ConcurrentHashMap<Runnable, ScheduledFuture<?>> tasks;

    /**
     * TaskManager instance
     */
    private static TaskManager $;

    /**
     * Synchronization Lock Object
     */
    private static final Object lock = new Object();

    /**
     * Referneces to {@link TimeUnit} objects for less typing
     */
    private static final TimeUnit micro = TimeUnit.MICROSECONDS, milli = TimeUnit.MILLISECONDS, sec = TimeUnit.SECONDS, min = TimeUnit.MINUTES;

    static {
        $ = new TaskManager();
    }

    /**
     * Internal use Constructor to initialize the Thread Pool
     */
    private TaskManager() {
        threadpool = new ScheduledThreadPoolExecutor(8);
        threadpool.setKeepAliveTime(5, sec);
        threadpool.allowCoreThreadTimeOut(true);
        threadpool.setContinueExistingPeriodicTasksAfterShutdownPolicy(false);
        threadpool.setExecuteExistingDelayedTasksAfterShutdownPolicy(false);
        tasks = new ConcurrentHashMap<Runnable, ScheduledFuture<?>>();
    }

    /**
     * Executes a task immediately
     * 
     * @param task
     *            the task to execute
     * @throws UtilityException
     * <br>
     *             if task is null
     */
    public static final void executeTask(Runnable task) throws UtilityException {
        if (task == null) {
            throw new UtilityException("arg.null", "Runnable task");
        }
        $.threadpool.execute(task);
    }

    /**
     * Submits a task to be executed. Execution may not happen immediately depending on the queued tasks
     * 
     * @param task
     *            the task to execute
     * @return a Future representing pending completion of the task
     * @throws UtilityException
     */
    public static final Future<?> submitTask(Runnable task) throws UtilityException {
        if (task == null) {
            throw new UtilityException("arg.null", "Runnable task");
        }
        return $.threadpool.submit(task);
    }

    /**
     * Executes a task after a delay in microseconds
     * 
     * @param task
     *            the tesk to execute
     * @param delay
     *            the delay before execution
     * @return a ScheduledFuture representing pending completion of the task and whose get() method will return null upon completion
     * @throws UtilityException
     * <br>
     *             if task is null
     */
    public static final ScheduledFuture<?> scheduleDelayedTaskInMicros(Runnable task, long delay) throws UtilityException {
        if (task == null) {
            throw new UtilityException("arg.null", "Runnable task");
        }
        ScheduledFuture<?> stask = $.threadpool.schedule(task, delay, micro);
        $.tasks.put(task, stask);
        return stask;
    }

    /**
     * Executes a task after a delay in milliseconds
     * 
     * @param task
     *            the tesk to execute
     * @param delay
     *            the delay before execution
     * @return a ScheduledFuture representing pending completion of the task and whose get() method will return null upon completion
     * @throws UtilityException
     * <br>
     *             if task is null
     */
    public static final ScheduledFuture<?> scheduleDelayedTaskInMillis(Runnable task, long delay) throws UtilityException {
        if (task == null) {
            throw new UtilityException("arg.null", "Runnable task");
        }
        ScheduledFuture<?> stask = $.threadpool.schedule(task, delay, milli);
        $.tasks.put(task, stask);
        return stask;
    }

    /**
     * Executes a task after a delay in seconds
     * 
     * @param task
     *            the tesk to execute
     * @param delay
     *            the delay before execution
     * @return a ScheduledFuture representing pending completion of the task and whose get() method will return null upon completion
     * @throws UtilityException
     * <br>
     *             if task is null
     */
    public static final ScheduledFuture<?> scheduleDelayedTaskInSeconds(Runnable task, long delay) throws UtilityException {
        if (task == null) {
            throw new UtilityException("arg.null", "Runnable task");
        }
        ScheduledFuture<?> stask = $.threadpool.schedule(task, delay, sec);
        $.tasks.put(task, stask);
        return stask;
    }

    /**
     * Executes a task after a delay in minutes
     * 
     * @param task
     *            the tesk to execute
     * @param delay
     *            the delay before execution
     * @return a ScheduledFuture representing pending completion of the task and whose get() method will return null upon completion
     * @throws UtilityException
     * <br>
     *             if task is null
     */
    public static final ScheduledFuture<?> scheduleDelayedTaskInMinutes(Runnable task, long delay) throws UtilityException {
        if (task == null) {
            throw new UtilityException("arg.null", "Runnable task");
        }
        ScheduledFuture<?> stask = $.threadpool.schedule(task, delay, min);
        $.tasks.put(task, stask);
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
     * @return a ScheduledFuture representing pending completion of the task and whose get() method will return null upon completion
     * @throws UtilityException
     * <br>
     *             if task is null
     */
    public static final ScheduledFuture<?> scheduleContinuedTaskInMicros(Runnable task, long initialdelay, long delay) throws UtilityException {
        if (task == null) {
            throw new UtilityException("arg.null", "Runnable task");
        }
        ScheduledFuture<?> stask = $.threadpool.scheduleAtFixedRate(task, initialdelay, delay, micro);
        $.tasks.put(task, stask);
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
     * @return a ScheduledFuture representing pending completion of the task and whose get() method will return null upon completion
     * @throws UtilityException
     * <br>
     *             if task is null
     */
    public static final ScheduledFuture<?> scheduleContinuedTaskInMillis(Runnable task, long initialdelay, long delay) throws UtilityException {
        if (task == null) {
            throw new UtilityException("arg.null", "Runnable task");
        }
        ScheduledFuture<?> stask = $.threadpool.scheduleAtFixedRate(task, initialdelay, delay, milli);
        $.tasks.put(task, stask);
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
     * @return a ScheduledFuture representing pending completion of the task and whose get() method will return null upon completion
     * @throws UtilityException
     * <br>
     *             if task is null
     */
    public static final ScheduledFuture<?> scheduleContinuedTaskInSeconds(Runnable task, long initialdelay, long delay) throws UtilityException {
        if (task == null) {
            throw new UtilityException("arg.null", "Runnable task");
        }
        ScheduledFuture<?> stask = $.threadpool.scheduleAtFixedRate(task, initialdelay, delay, sec);
        $.tasks.put(task, stask);
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
     * @return a ScheduledFuture representing pending completion of the task and whose get() method will return null upon completion
     * @throws UtilityException
     * <br>
     *             if task is null
     */
    public static final ScheduledFuture<?> scheduleContinuedTaskInMinutes(Runnable task, long initialdelay, long delay) throws UtilityException {
        if (task == null) {
            throw new UtilityException("arg.null", "Runnable task");
        }
        ScheduledFuture<?> stask = $.threadpool.scheduleAtFixedRate(task, initialdelay, delay, min);
        $.tasks.put(task, stask);
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
     * @return a ScheduledFuture representing pending completion of the task and whose get() method will return null upon completion
     * @throws UtilityException
     * <br>
     *             if task is null
     */
    public static final ScheduledFuture<?> scheduleContinuedTask(Runnable task, long initialdelay, long delay, TimeUnit timeunit) throws UtilityException {
        if (task == null) {
            throw new UtilityException("arg.null", "Runnable task");
        }
        ScheduledFuture<?> stask = $.threadpool.scheduleAtFixedRate(task, initialdelay, delay, timeunit);
        $.tasks.put(task, stask);
        return stask;
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
            if ($.tasks.containsKey(task)) {
                check = $.tasks.get(task).cancel(true);
                $.tasks.remove(task);
            }
            if (!check) {
                check = $.threadpool.remove(task);
            }
            if (check) {
                $.threadpool.purge();
            }
            return check;
        }
    }

    /**
     * Terminates the ThreadPool
     */
    public static final void terminateThreadPool() {
        $.threadpool.shutdownNow();
        $.threadpool = null;
        $ = null;
    }
}
