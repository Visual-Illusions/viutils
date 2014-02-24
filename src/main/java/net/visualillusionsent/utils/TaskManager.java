/*
 * This file is part of VIUtils.
 *
 * Copyright © 2012-2014 Visual Illusions Entertainment
 *
 * VIUtils is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License,
 * or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with this library.
 * If not, see http://www.gnu.org/licenses/lgpl.html.
 */
package net.visualillusionsent.utils;

import java.util.Iterator;
import java.util.concurrent.Callable;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import static java.util.concurrent.TimeUnit.HOURS;
import static java.util.concurrent.TimeUnit.MICROSECONDS;
import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static java.util.concurrent.TimeUnit.MINUTES;
import static java.util.concurrent.TimeUnit.SECONDS;
import static net.visualillusionsent.utils.Verify.notNegativeOrZero;
import static net.visualillusionsent.utils.Verify.notNull;

/**
 * Task Management System
 * <p/>
 * Creates a Thread Pool for handling executing delayed and continuous tasks
 *
 * @author Jason (darkdiplomat)
 * @version 1.3
 * @since 1.0.0
 */
public final class TaskManager {

    /** Class Version */
    private static final float classVersion = 1.3F; /* VIUtils 1.3.1 | 1.3 */
    /** The ThreadPool object */
    private static final ScheduledThreadPoolExecutor threadPool;
    /** The Map of Tasks */
    private static final ConcurrentHashMap<Task, ScheduledFuture<?>> tasks;

    static {
        threadPool = new ScheduledThreadPoolExecutor(8); // Set the max number of core idle threads
        threadPool.setKeepAliveTime(10, SECONDS); // How long to keep idle threads alive
        threadPool.allowCoreThreadTimeOut(true); // Allow the core threads to time out
        threadPool.setContinueExistingPeriodicTasksAfterShutdownPolicy(false); //Don't execute anything after shutdown
        threadPool.setExecuteExistingDelayedTasksAfterShutdownPolicy(false); //Don't execute anything after shutdown
        tasks = new ConcurrentHashMap<Task, ScheduledFuture<?>>(); // Create the map for Task tracking
        scheduleContinuedTaskInMillis(new TaskCleaner(), 10, 10); // Schedule the clean up
    }

    /** Constructions disallowed */
    private TaskManager() {
    }

    /**
     * Executes a {@link Runnable} task immediately
     *
     * @param task
     *         the {@link Runnable} task to execute
     *
     * @throws UtilityException
     *         <br>
     *         if task is null
     * @throws RejectedExecutionException
     *         at discretion of <tt>RejectedExecutionHandler</tt>, if task cannot be accepted
     *         for execution because the executor has been shut down.
     */
    public static void executeTask(Runnable task) throws UtilityException, RejectedExecutionException {
        notNull(task, "Runnable task");

        threadPool.execute(task); //wrap runnable for exception logging
    }

    /**
     * Submits a {@link Runnable} task to be executed. Execution may not happen immediately depending on the queued tasks
     *
     * @param task
     *         the {@link Runnable} task to execute
     *
     * @return a Future representing pending completion of the task
     *
     * @throws UtilityException
     *         <br>
     *         if task is null
     * @throws RejectedExecutionException
     *         at discretion of <tt>RejectedExecutionHandler</tt>, if task cannot be accepted
     *         for execution because the executor has been shut down.
     */
    public static Future<?> submitTask(Runnable task) throws UtilityException, RejectedExecutionException {
        notNull(task, "Runnable task");

        return threadPool.submit(task);
    }

    /**
     * Submits a {@link Callable} task to be executed. Execution may not happen immediately depending on the queued tasks
     *
     * @param task
     *         the {@link Callable} task to execute
     *
     * @return a {@link Future} representing pending completion of the task
     *
     * @throws UtilityException
     *         <br>
     *         if task is null
     * @throws RejectedExecutionException
     *         at discretion of <tt>RejectedExecutionHandler</tt>, if task cannot be accepted
     *         for execution because the executor has been shut down.
     */
    public static <V> Future<V> submitTask(Callable<V> task) throws UtilityException, RejectedExecutionException {
        notNull(task, "Callable task");

        return threadPool.submit(task);
    }

    /**
     * Executes a {@link Runnable} task after a delay in microseconds
     *
     * @param task
     *         the {@link Runnable} task to execute
     * @param delay
     *         the delay before execution
     *
     * @return a ScheduledFuture representing pending completion of the task and whose get() method will return null upon completion
     *
     * @throws UtilityException
     *         <br>
     *         if task is null;<br>
     *         if the delay is equal to or less than zero
     * @throws RejectedExecutionException
     *         at discretion of <tt>RejectedExecutionHandler</tt>, if task cannot be accepted
     *         for execution because the executor has been shut down.
     */
    public static ScheduledFuture<?> scheduleDelayedTaskInMicros(Runnable task, long delay) throws UtilityException, RejectedExecutionException {
        return scheduleDelayedTask(task, delay, MICROSECONDS);
    }

    /**
     * Executes a {@link Callable} task after a delay in microseconds
     *
     * @param task
     *         the {@link Callable} task to execute
     * @param delay
     *         the delay before execution
     *
     * @return a ScheduledFuture representing pending completion of the task and whose get() method will return null upon completion
     *
     * @throws UtilityException
     *         <br>
     *         if task is null;<br>
     *         if the delay is equal to or less than zero
     * @throws RejectedExecutionException
     *         at discretion of <tt>RejectedExecutionHandler</tt>, if task cannot be accepted
     *         for execution because the executor has been shut down.
     */
    public static <V> ScheduledFuture<V> scheduleDelayedTaskInMicros(Callable<V> task, long delay) throws UtilityException, RejectedExecutionException {
        return scheduleDelayedTask(task, delay, MICROSECONDS);
    }

    /**
     * Executes a {@link Runnable} task after a delay in milliseconds
     *
     * @param task
     *         the {@link Runnable} task to execute
     * @param delay
     *         the delay before execution
     *
     * @return a ScheduledFuture representing pending completion of the task and whose get() method will return null upon completion
     *
     * @throws UtilityException
     *         <br>
     *         if task is null;<br>
     *         if the delay is equal to or less than zero
     * @throws RejectedExecutionException
     *         at discretion of <tt>RejectedExecutionHandler</tt>, if task cannot be accepted
     *         for execution because the executor has been shut down.
     */
    public static ScheduledFuture<?> scheduleDelayedTaskInMillis(Runnable task, long delay) throws UtilityException, RejectedExecutionException {
        return scheduleDelayedTask(task, delay, MILLISECONDS);
    }

    /**
     * Executes a {@link Callable} task after a delay in milliseconds
     *
     * @param task
     *         the {@link Callable} task to execute
     * @param delay
     *         the delay before execution
     *
     * @return a ScheduledFuture representing pending completion of the task and whose get() method will return null upon completion
     *
     * @throws UtilityException
     *         <br>
     *         if task is null;<br>
     *         if the delay is equal to or less than zero
     * @throws RejectedExecutionException
     *         at discretion of <tt>RejectedExecutionHandler</tt>, if task cannot be accepted
     *         for execution because the executor has been shut down.
     */
    public static <V> ScheduledFuture<V> scheduleDelayedTaskInMillis(Callable<V> task, long delay) throws UtilityException, RejectedExecutionException {
        return scheduleDelayedTask(task, delay, MILLISECONDS);
    }

    /**
     * Executes a {@link Runnable} task after a delay in seconds
     *
     * @param task
     *         the {@link Runnable} task to execute
     * @param delay
     *         the delay before execution
     *
     * @return a ScheduledFuture representing pending completion of the task and whose get() method will return null upon completion
     *
     * @throws UtilityException
     *         <br>
     *         if task is null;<br>
     *         if the delay is equal to or less than zero
     * @throws RejectedExecutionException
     *         at discretion of <tt>RejectedExecutionHandler</tt>, if task cannot be accepted
     *         for execution because the executor has been shut down.
     */
    public static ScheduledFuture<?> scheduleDelayedTaskInSeconds(Runnable task, long delay) throws UtilityException, RejectedExecutionException {
        return scheduleDelayedTask(task, delay, SECONDS);
    }

    /**
     * Executes a {@link Callable} task after a delay in seconds
     *
     * @param task
     *         the {@link Callable} task to execute
     * @param delay
     *         the delay before execution
     *
     * @return a ScheduledFuture representing pending completion of the task and whose get() method will return null upon completion
     *
     * @throws UtilityException
     *         <br>
     *         if task is null;<br>
     *         if the delay is equal to or less than zero
     * @throws RejectedExecutionException
     *         at discretion of <tt>RejectedExecutionHandler</tt>, if task cannot be accepted
     *         for execution because the executor has been shut down.
     */
    public static <V> ScheduledFuture<V> scheduleDelayedTaskInSeconds(Callable<V> task, long delay) throws UtilityException, RejectedExecutionException {
        return scheduleDelayedTask(task, delay, SECONDS);
    }

    /**
     * Executes a {@link Runnable} task after a delay in minutes
     *
     * @param task
     *         the {@link Runnable} task to execute
     * @param delay
     *         the delay before execution
     *
     * @return a ScheduledFuture representing pending completion of the task and whose get() method will return null upon completion
     *
     * @throws UtilityException
     *         <br>
     *         if task is null;<br>
     *         if the delay is equal to or less than zero
     * @throws RejectedExecutionException
     *         at discretion of <tt>RejectedExecutionHandler</tt>, if task cannot be accepted
     *         for execution because the executor has been shut down.
     */
    public static ScheduledFuture<?> scheduleDelayedTaskInMinutes(Runnable task, long delay) throws UtilityException, RejectedExecutionException {
        return scheduleDelayedTask(task, delay, MINUTES);
    }

    /**
     * Executes a {@link Callable} task after a delay in microseconds
     *
     * @param task
     *         the {@link Callable} task to execute
     * @param delay
     *         the delay before execution
     *
     * @return a ScheduledFuture representing pending completion of the task and whose get() method will return null upon completion
     *
     * @throws UtilityException
     *         <br>
     *         if task is null;<br>
     *         if the delay is equal to or less than zero
     * @throws RejectedExecutionException
     *         at discretion of <tt>RejectedExecutionHandler</tt>, if task cannot be accepted
     *         for execution because the executor has been shut down.
     */
    public static <V> ScheduledFuture<V> scheduleDelayedTaskInMinutes(Callable<V> task, long delay) throws UtilityException, RejectedExecutionException {
        return scheduleDelayedTask(task, delay, MINUTES);
    }

    /**
     * Executes a {@link Runnable} task after a delay in Hours
     *
     * @param task
     *         the {@link Runnable} task to execute
     * @param delay
     *         the delay before execution
     *
     * @return a ScheduledFuture representing pending completion of the task and whose get() method will return null upon completion
     *
     * @throws UtilityException
     *         <br>
     *         if task is null;<br>
     *         if the delay is equal to or less than zero
     * @throws RejectedExecutionException
     *         at discretion of <tt>RejectedExecutionHandler</tt>, if task cannot be accepted
     *         for execution because the executor has been shut down.
     */
    public static ScheduledFuture<?> scheduleDelayedTaskInHours(Runnable task, long delay) throws UtilityException, RejectedExecutionException {
        return scheduleDelayedTask(task, delay, HOURS);
    }

    /**
     * Executes a {@link Callable} task after a delay in hours
     *
     * @param task
     *         the {@link Callable} task to execute
     * @param delay
     *         the delay before execution
     *
     * @return a ScheduledFuture representing pending completion of the task and whose get() method will return null upon completion
     *
     * @throws UtilityException
     *         <br>
     *         if task is null;<br>
     *         if the delay is equal to or less than zero
     * @throws RejectedExecutionException
     *         at discretion of <tt>RejectedExecutionHandler</tt>, if task cannot be accepted
     *         for execution because the executor has been shut down.
     */
    public static <V> ScheduledFuture<V> scheduleDelayedTaskInHours(Callable<V> task, long delay) throws UtilityException, RejectedExecutionException {
        return scheduleDelayedTask(task, delay, HOURS);
    }

    /**
     * Executes a {@link Runnable} task after a delay in the specified {@link TimeUnit}
     *
     * @param task
     *         the {@link Runnable} task to execute
     * @param delay
     *         the delay before execution
     * @param timeUnit
     *         the {@link TimeUnit} to use
     *
     * @return a ScheduledFuture representing pending completion of the task and whose get() method will return null upon completion
     *
     * @throws UtilityException
     *         <br>
     *         if task is null;<br>
     *         if the delay is equal to or less than zero<br/>
     *         or if timeUnit is null
     * @throws RejectedExecutionException
     *         at discretion of <tt>RejectedExecutionHandler</tt>, if task cannot be accepted
     *         for execution because the executor has been shut down.
     */
    public static ScheduledFuture<?> scheduleDelayedTask(Runnable task, long delay, TimeUnit timeUnit) throws UtilityException, RejectedExecutionException {
        notNull(task, "Runnable task");
        notNegativeOrZero(delay, "Delay");
        notNull(timeUnit, "TimeUnit timeUnit");

        ScheduledFuture<?> sTask = threadPool.schedule(task, delay, timeUnit);
        tasks.put(new Task(task), sTask);
        return sTask;
    }

    /**
     * Executes a {@link Callable} task after a delay in the specified {@link TimeUnit}
     *
     * @param task
     *         the {@link Callable} task to execute
     * @param delay
     *         the delay before execution
     * @param timeUnit
     *         the {@link TimeUnit} to use
     *
     * @return a ScheduledFuture representing pending completion of the task and whose get() method will return null upon completion
     *
     * @throws UtilityException
     *         <br>
     *         if task is null;<br>
     *         if the delay is equal to or less than zero<br/>
     *         or if timeUnit is null
     * @throws RejectedExecutionException
     *         at discretion of <tt>RejectedExecutionHandler</tt>, if task cannot be accepted
     *         for execution because the executor has been shut down.
     */
    public static <V> ScheduledFuture<V> scheduleDelayedTask(Callable<V> task, long delay, TimeUnit timeUnit) throws UtilityException, RejectedExecutionException {
        notNull(task, "Callable task");
        notNegativeOrZero(delay, "Delay");
        notNull(timeUnit, "TimeUnit timeUnit");

        ScheduledFuture<V> sTask = threadPool.schedule(task, delay, timeUnit);
        tasks.put(new Task(task), sTask);
        return sTask;
    }

    /**
     * Executes a {@link Runnable} task continually on a fixed delay in microseconds
     *
     * @param task
     *         the {@link Runnable} task to execute
     * @param initialDelay
     *         the delay before initial execution
     * @param delay
     *         the delay between additional executions
     *
     * @return a ScheduledFuture representing pending completion of the task and whose get() method will throw an exception upon cancellation
     *
     * @throws UtilityException
     *         <br>
     *         if task is null;<br>
     *         if the delay is equal to or less than zero
     * @throws RejectedExecutionException
     *         at discretion of <tt>RejectedExecutionHandler</tt>, if task cannot be accepted
     *         for execution because the executor has been shut down.
     */
    public static ScheduledFuture<?> scheduleContinuedTaskInMicros(Runnable task, long initialDelay, long delay) throws UtilityException, RejectedExecutionException {
        return scheduleContinuedTask(task, initialDelay, delay, MICROSECONDS);
    }

    /**
     * Executes a {@link Runnable} task continually on a fixed delay in milliseconds
     *
     * @param task
     *         the {@link Runnable} task to execute
     * @param initialDelay
     *         the delay before initial execution
     * @param delay
     *         the delay between additional executions
     *
     * @return a ScheduledFuture representing pending completion of the task and whose get() method will throw an exception upon cancellation
     *
     * @throws UtilityException
     *         <br>
     *         if task is null;<br>
     *         if the delay is equal to or less than zero
     * @throws RejectedExecutionException
     *         at discretion of <tt>RejectedExecutionHandler</tt>, if task cannot be accepted
     *         for execution because the executor has been shut down.
     */
    public static ScheduledFuture<?> scheduleContinuedTaskInMillis(Runnable task, long initialDelay, long delay) throws UtilityException, RejectedExecutionException {
        return scheduleContinuedTask(task, initialDelay, delay, MILLISECONDS);
    }

    /**
     * Executes a {@link Runnable} task continually on a fixed delay in seconds
     *
     * @param task
     *         the {@link Runnable} task to execute
     * @param initialDelay
     *         the delay before initial execution
     * @param delay
     *         the delay between additional executions
     *
     * @return a ScheduledFuture representing pending completion of the task and whose get() method will throw an exception upon cancellation
     *
     * @throws UtilityException
     *         <br>
     *         if task is null;<br>
     *         if the delay is equal to or less than zero
     * @throws RejectedExecutionException
     *         at discretion of <tt>RejectedExecutionHandler</tt>, if task cannot be accepted
     *         for execution because the executor has been shut down.
     */
    public static ScheduledFuture<?> scheduleContinuedTaskInSeconds(Runnable task, long initialDelay, long delay) throws UtilityException, RejectedExecutionException {
        return scheduleContinuedTask(task, initialDelay, delay, SECONDS);
    }

    /**
     * Executes a {@link Runnable} task continually on a fixed delay in minutes
     *
     * @param task
     *         the {@link Runnable} task to execute
     * @param initialDelay
     *         the delay before initial execution
     * @param delay
     *         the delay between additional executions
     *
     * @return a ScheduledFuture representing pending completion of the task and whose get() method will throw an exception upon cancellation
     *
     * @throws UtilityException
     *         <br>
     *         if task is null;<br>
     *         if the delay is equal to or less than zero
     * @throws RejectedExecutionException
     *         at discretion of <tt>RejectedExecutionHandler</tt>, if task cannot be accepted
     *         for execution because the executor has been shut down.
     */
    public static ScheduledFuture<?> scheduleContinuedTaskInMinutes(Runnable task, long initialDelay, long delay) throws UtilityException, RejectedExecutionException {
        return scheduleContinuedTask(task, initialDelay, delay, MINUTES);
    }

    /**
     * Executes a {@link Runnable} task continually on a fixed delay in the specified TimeUnit
     *
     * @param task
     *         the {@link Runnable} task to execute
     * @param initialDelay
     *         the delay before initial execution
     * @param delay
     *         the delay between additional executions
     * @param timeUnit
     *         the {@link TimeUnit} to use
     *
     * @return a ScheduledFuture representing pending completion of the task and whose get() method will throw an exception upon cancellation
     *
     * @throws UtilityException
     *         <br>
     *         if task is null;<br>
     *         if the delay is equal to or less than zero
     * @throws RejectedExecutionException
     *         at discretion of <tt>RejectedExecutionHandler</tt>, if task cannot be accepted
     *         for execution because the executor has been shut down.
     */
    public static ScheduledFuture<?> scheduleContinuedTask(Runnable task, long initialDelay, long delay, TimeUnit timeUnit) throws UtilityException, RejectedExecutionException {
        notNull(task, "Runnable task");
        notNegativeOrZero(initialDelay, "long initialDelay");

        ScheduledFuture<?> sTask = threadPool.scheduleAtFixedRate(task, initialDelay, delay, timeUnit);
        tasks.put(new Task(task), sTask);
        return sTask;
    }

    /**
     * Removes a {@link Runnable} task from the pool
     *
     * @param task
     *         the {@link Runnable} task to be removed
     *
     * @return {@code true} if successfully removed, {@code false} if already stopped or non-existent
     */
    public static boolean removeTask(Runnable task) {
        boolean check = false;
        if (tasks.containsKey(task)) {
            check = tasks.get(task).cancel(true);
            tasks.remove(task);
        }
        if (!check) {
            check = threadPool.remove(task);
        }
        if (check) {
            threadPool.purge();
        }
        return check;
    }

    /**
     * Removes a {@link Callable} task from the pool
     *
     * @param task
     *         the {@link Callable} task to be removed
     *
     * @return {@code true} if successfully removed, {@code false} if already stopped or non-existent
     */
    public static boolean removeTask(Callable<?> task) {
        boolean check = false;
        if (tasks.containsKey(task)) {
            check = tasks.get(task).cancel(true);
            tasks.remove(task);
        }
        return check;
    }

    /**
     * Internal Task cleanup
     *
     * @author Jason (darkdiplomat)
     */
    private static final class TaskCleaner implements Runnable {

        @Override
        public final void run() { //Run to clean up any memory leaks we may cause by holding dead tasks
            if (tasks.isEmpty()) {
                return; // skip cleaning if there are no tasks
            }
            Iterator<Task> taskItr = tasks.keySet().iterator();
            while (taskItr.hasNext()) {
                Task task = taskItr.next(); //Get the task
                if (tasks.get(task).isDone()) { // Check task for completion
                    try {
                        tasks.get(task).get(); // Test for execution exceptions
                    }
                    catch (CancellationException cex) {
                        // Don't care if it was cancelled
                    }
                    catch (InterruptedException e) {
                        // The task was probably canceled so skip this
                    }
                    catch (ExecutionException eex) {
                        //This is important if an exception was caused in execution, print out the exception immediately
                        task.printError(eex.getCause()); // Call the printError message for the proper Task name rather than the wrapper's name
                    }
                    taskItr.remove();
                }
            }
        }
    }

    /**
     * Gets this class's version number
     *
     * @return the class version
     */
    public static float getClassVersion() {
        return classVersion;
    }
}
