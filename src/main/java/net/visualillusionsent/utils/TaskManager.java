/*
 * This file is part of VIUtils.
 *
 * Copyright © 2012-2013 Visual Illusions Entertainment
 *
 * VIUtils is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License,
 * or (at your option) any later version.
 *
 * VIUtils is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with VIUtils.
 * If not, see http://www.gnu.org/licenses/lgpl.html.
 */
package net.visualillusionsent.utils;

import java.util.Iterator;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import static java.util.concurrent.TimeUnit.*;

/**
 * Task Management System
 * <p>
 * Creates a Thread Pool for handling executing delayed and continuous tasks
 * 
 * @since 1.0.0
 * @version 1.1
 * @author Jason (darkdiplomat)
 */
public final class TaskManager{

    /**
     * Class Version
     */
    private static final float classVersion = 1.1F;
    /**
     * The ThreadPool object
     */
    private static final ScheduledThreadPoolExecutor threadpool;
    /**
     * The Map of Tasks
     */
    private static final ConcurrentHashMap<Task, ScheduledFuture<?>> tasks;
    static{
        threadpool = new ScheduledThreadPoolExecutor(8); // Set the max number of core idle threads
        threadpool.setKeepAliveTime(2, MINUTES); // How long to keep idle threads alive
        threadpool.allowCoreThreadTimeOut(false); // Don't allow the core threads to time out
        threadpool.setContinueExistingPeriodicTasksAfterShutdownPolicy(false); //Don't execute anything after shutdown
        threadpool.setExecuteExistingDelayedTasksAfterShutdownPolicy(false); //Don't execute anything after shutdown
        tasks = new ConcurrentHashMap<Task, ScheduledFuture<?>>(); // Create the map for Task tracking
        scheduleContinuedTaskInMillis(new TaskCleaner(), 10, 10); // Schedule the clean up
    }

    /**
     * Constructions disallowed
     */
    private TaskManager(){}

    /**
     * Executes a {@link Runnable} task immediately
     * 
     * @param task
     *            the {@link Runnable} task to execute
     * @throws UtilityException
     * <br>
     *             if task is null
     * @throws RejectedExecutionException
     *             at discretion of <tt>RejectedExecutionHandler</tt>, if task cannot be accepted
     *             for execution because the executor has been shut down.
     */
    public static final void executeTask(Runnable task) throws UtilityException, RejectedExecutionException{
        if(task == null){
            throw new UtilityException("arg.null", "Runnable task");
        }
        threadpool.execute(task); //wrap runnable for exception logging
    }

    /**
     * Submits a {@link Runnable} task to be executed. Execution may not happen immediately depending on the queued tasks
     * 
     * @param task
     *            the {@link Runnable} task to execute
     * @return a Future representing pending completion of the task
     * @throws UtilityException
     * <br>
     *             if task is null
     * @throws RejectedExecutionException
     *             at discretion of <tt>RejectedExecutionHandler</tt>, if task cannot be accepted
     *             for execution because the executor has been shut down.
     */
    public static final Future<?> submitTask(Runnable task) throws UtilityException, RejectedExecutionException{
        if(task == null){
            throw new UtilityException("arg.null", "Runnable task");
        }
        return threadpool.submit(task);
    }

    /**
     * Submits a {@link Callable} task to be executed. Execution may not happen immediately depending on the queued tasks
     * 
     * @param task
     *            the {@link Callable} task to execute
     * @return a {@link Future} representing pending completion of the task
     * @throws UtilityException
     * <br>
     *             if task is null
     * @throws RejectedExecutionException
     *             at discretion of <tt>RejectedExecutionHandler</tt>, if task cannot be accepted
     *             for execution because the executor has been shut down.
     */
    public static final <V> Future<V> submitTask(Callable<V> task) throws UtilityException, RejectedExecutionException{
        if(task == null){
            throw new UtilityException("arg.null", "Runnable task");
        }
        return threadpool.submit(task);
    }

    /**
     * Executes a {@link Runnable} task after a delay in microseconds
     * 
     * @param task
     *            the {@link Runnable} task to execute
     * @param delay
     *            the delay before execution
     * @return a ScheduledFuture representing pending completion of the task and whose get() method will return null upon completion
     * @throws UtilityException
     * <br>
     *             if task is null;<br>
     *             if the delay is equal to or less than zero
     * @throws RejectedExecutionException
     *             at discretion of <tt>RejectedExecutionHandler</tt>, if task cannot be accepted
     *             for execution because the executor has been shut down.
     */
    public static final ScheduledFuture<?> scheduleDelayedTaskInMicros(Runnable task, long delay) throws UtilityException, RejectedExecutionException{
        if(task == null){
            throw new UtilityException("arg.null", "Runnable task");
        }
        if(delay <= 0){
            throw new UtilityException("delay.negative", "delay");
        }
        ScheduledFuture<?> stask = threadpool.schedule(task, delay, MICROSECONDS);
        tasks.put(new Task(task), stask);
        return stask;
    }

    /**
     * Executes a {@link Callable} task after a delay in microseconds
     * 
     * @param task
     *            the {@link Callable} task to execute
     * @param delay
     *            the delay before execution
     * @return a ScheduledFuture representing pending completion of the task and whose get() method will return null upon completion
     * @throws UtilityException
     * <br>
     *             if task is null;<br>
     *             if the delay is equal to or less than zero
     * @throws RejectedExecutionException
     *             at discretion of <tt>RejectedExecutionHandler</tt>, if task cannot be accepted
     *             for execution because the executor has been shut down.
     */
    public static final <V> ScheduledFuture<V> scheduleDelayedTaskInMicros(Callable<V> task, long delay) throws UtilityException, RejectedExecutionException{
        if(task == null){
            throw new UtilityException("arg.null", "Callable task");
        }
        if(delay <= 0){
            throw new UtilityException("delay.negative", "delay");
        }
        ScheduledFuture<V> stask = threadpool.schedule(task, delay, MICROSECONDS);
        tasks.put(new Task(task), stask);
        return stask;
    }

    /**
     * Executes a {@link Runnable} task after a delay in milliseconds
     * 
     * @param task
     *            the {@link Runnable} task to execute
     * @param delay
     *            the delay before execution
     * @return a ScheduledFuture representing pending completion of the task and whose get() method will return null upon completion
     * @throws UtilityException
     * <br>
     *             if task is null;<br>
     *             if the delay is equal to or less than zero
     * @throws RejectedExecutionException
     *             at discretion of <tt>RejectedExecutionHandler</tt>, if task cannot be accepted
     *             for execution because the executor has been shut down.
     */
    public static final ScheduledFuture<?> scheduleDelayedTaskInMillis(Runnable task, long delay) throws UtilityException, RejectedExecutionException{
        if(task == null){
            throw new UtilityException("arg.null", "Runnable task");
        }
        if(delay <= 0){
            throw new UtilityException("delay.negative", "delay");
        }
        ScheduledFuture<?> stask = threadpool.schedule(task, delay, MILLISECONDS);
        tasks.put(new Task(task), stask);
        return stask;
    }

    /**
     * Executes a {@link Callable} task after a delay in milliseconds
     * 
     * @param task
     *            the {@link Callable} task to execute
     * @param delay
     *            the delay before execution
     * @return a ScheduledFuture representing pending completion of the task and whose get() method will return null upon completion
     * @throws UtilityException
     * <br>
     *             if task is null;<br>
     *             if the delay is equal to or less than zero
     * @throws RejectedExecutionException
     *             at discretion of <tt>RejectedExecutionHandler</tt>, if task cannot be accepted
     *             for execution because the executor has been shut down.
     */
    public static final <V> ScheduledFuture<V> scheduleDelayedTaskInMillis(Callable<V> task, long delay) throws UtilityException, RejectedExecutionException{
        if(task == null){
            throw new UtilityException("arg.null", "Callable task");
        }
        if(delay <= 0){
            throw new UtilityException("delay.negative", "delay");
        }
        ScheduledFuture<V> stask = threadpool.schedule(task, delay, MILLISECONDS);
        tasks.put(new Task(task), stask);
        return stask;
    }

    /**
     * Executes a {@link Runnable} task after a delay in seconds
     * 
     * @param task
     *            the {@link Runnable} task to execute
     * @param delay
     *            the delay before execution
     * @return a ScheduledFuture representing pending completion of the task and whose get() method will return null upon completion
     * @throws UtilityException
     * <br>
     *             if task is null;<br>
     *             if the delay is equal to or less than zero
     * @throws RejectedExecutionException
     *             at discretion of <tt>RejectedExecutionHandler</tt>, if task cannot be accepted
     *             for execution because the executor has been shut down.
     */
    public static final ScheduledFuture<?> scheduleDelayedTaskInSeconds(Runnable task, long delay) throws UtilityException, RejectedExecutionException{
        if(task == null){
            throw new UtilityException("arg.null", "Runnable task");
        }
        if(delay <= 0){
            throw new UtilityException("delay.negative", "delay");
        }
        ScheduledFuture<?> stask = threadpool.schedule(task, delay, SECONDS);
        tasks.put(new Task(task), stask);
        return stask;
    }

    /**
     * Executes a {@link Callable} task after a delay in seconds
     * 
     * @param task
     *            the {@link Callable} task to execute
     * @param delay
     *            the delay before execution
     * @return a ScheduledFuture representing pending completion of the task and whose get() method will return null upon completion
     * @throws UtilityException
     * <br>
     *             if task is null;<br>
     *             if the delay is equal to or less than zero
     * @throws RejectedExecutionException
     *             at discretion of <tt>RejectedExecutionHandler</tt>, if task cannot be accepted
     *             for execution because the executor has been shut down.
     */
    public static final <V> ScheduledFuture<V> scheduleDelayedTaskInSeconds(Callable<V> task, long delay) throws UtilityException, RejectedExecutionException{
        if(task == null){
            throw new UtilityException("arg.null", "Callable task");
        }
        if(delay <= 0){
            throw new UtilityException("delay.negative", "delay");
        }
        ScheduledFuture<V> stask = threadpool.schedule(task, delay, SECONDS);
        tasks.put(new Task(task), stask);
        return stask;
    }

    /**
     * Executes a {@link Runnable} task after a delay in minutes
     * 
     * @param task
     *            the {@link Runnable} task to execute
     * @param delay
     *            the delay before execution
     * @return a ScheduledFuture representing pending completion of the task and whose get() method will return null upon completion
     * @throws UtilityException
     * <br>
     *             if task is null;<br>
     *             if the delay is equal to or less than zero
     * @throws RejectedExecutionException
     *             at discretion of <tt>RejectedExecutionHandler</tt>, if task cannot be accepted
     *             for execution because the executor has been shut down.
     */
    public static final ScheduledFuture<?> scheduleDelayedTaskInMinutes(Runnable task, long delay) throws UtilityException, RejectedExecutionException{
        if(task == null){
            throw new UtilityException("arg.null", "Runnable task");
        }
        if(delay <= 0){
            throw new UtilityException("delay.negative", "delay");
        }
        ScheduledFuture<?> stask = threadpool.schedule(task, delay, MINUTES);
        tasks.put(new Task(task), stask);
        return stask;
    }

    /**
     * Executes a {@link Callable} task after a delay in microseconds
     * 
     * @param task
     *            the {@link Callable} task to execute
     * @param delay
     *            the delay before execution
     * @return a ScheduledFuture representing pending completion of the task and whose get() method will return null upon completion
     * @throws UtilityException
     * <br>
     *             if task is null;<br>
     *             if the delay is equal to or less than zero
     * @throws RejectedExecutionException
     *             at discretion of <tt>RejectedExecutionHandler</tt>, if task cannot be accepted
     *             for execution because the executor has been shut down.
     */
    public static final <V> ScheduledFuture<V> scheduleDelayedTaskInMinutes(Callable<V> task, long delay) throws UtilityException, RejectedExecutionException{
        if(task == null){
            throw new UtilityException("arg.null", "Callable task");
        }
        if(delay <= 0){
            throw new UtilityException("delay.negative", "delay");
        }
        ScheduledFuture<V> stask = threadpool.schedule(task, delay, MINUTES);
        tasks.put(new Task(task), stask);
        return stask;
    }

    /**
     * Executes a {@link Runnable} task after a delay in Hours
     * 
     * @param task
     *            the {@link Runnable} task to execute
     * @param delay
     *            the delay before execution
     * @return a ScheduledFuture representing pending completion of the task and whose get() method will return null upon completion
     * @throws UtilityException
     * <br>
     *             if task is null;<br>
     *             if the delay is equal to or less than zero
     * @throws RejectedExecutionException
     *             at discretion of <tt>RejectedExecutionHandler</tt>, if task cannot be accepted
     *             for execution because the executor has been shut down.
     */
    public static final ScheduledFuture<?> scheduleDelayedTaskInHours(Runnable task, long delay) throws UtilityException, RejectedExecutionException{
        if(task == null){
            throw new UtilityException("arg.null", "Runnable task");
        }
        if(delay <= 0){
            throw new UtilityException("delay.negative", "delay");
        }
        ScheduledFuture<?> stask = threadpool.schedule(task, delay, HOURS);
        tasks.put(new Task(task), stask);
        return stask;
    }

    /**
     * Executes a {@link Callable} task after a delay in hours
     * 
     * @param task
     *            the {@link Callable} task to execute
     * @param delay
     *            the delay before execution
     * @return a ScheduledFuture representing pending completion of the task and whose get() method will return null upon completion
     * @throws UtilityException
     * <br>
     *             if task is null;<br>
     *             if the delay is equal to or less than zero
     * @throws RejectedExecutionException
     *             at discretion of <tt>RejectedExecutionHandler</tt>, if task cannot be accepted
     *             for execution because the executor has been shut down.
     */
    public static final <V> ScheduledFuture<V> scheduleDelayedTaskInHours(Callable<V> task, long delay) throws UtilityException, RejectedExecutionException{
        if(task == null){
            throw new UtilityException("arg.null", "Callable task");
        }
        if(delay <= 0){
            throw new UtilityException("delay.negative", "delay");
        }
        ScheduledFuture<V> stask = threadpool.schedule(task, delay, HOURS);
        tasks.put(new Task(task), stask);
        return stask;
    }

    /**
     * Executes a {@link Runnable} task continually on a fixed delay in microseconds
     * 
     * @param task
     *            the {@link Runnable} task to execute
     * @param initialdelay
     *            the delay before initial execution
     * @param delay
     *            the delay between additional executions
     * @return a ScheduledFuture representing pending completion of the task and whose get() method will throw an exception upon cancellation
     * @throws UtilityException
     * <br>
     *             if task is null;<br>
     *             if the delay is equal to or less than zero
     * @throws RejectedExecutionException
     *             at discretion of <tt>RejectedExecutionHandler</tt>, if task cannot be accepted
     *             for execution because the executor has been shut down.
     */
    public static final ScheduledFuture<?> scheduleContinuedTaskInMicros(Runnable task, long initialdelay, long delay) throws UtilityException, RejectedExecutionException{
        if(task == null){
            throw new UtilityException("arg.null", "Runnable task");
        }
        if(initialdelay <= 0 || delay <= 0){
            throw new UtilityException("delay.negative", "delay");
        }
        ScheduledFuture<?> stask = threadpool.scheduleAtFixedRate(task, initialdelay, delay, MICROSECONDS);
        tasks.put(new Task(task), stask);
        return stask;
    }

    /**
     * Executes a {@link Runnable} task continually on a fixed delay in milliseconds
     * 
     * @param task
     *            the {@link Runnable} task to execute
     * @param initialdelay
     *            the delay before initial execution
     * @param delay
     *            the delay between additional executions
     * @return a ScheduledFuture representing pending completion of the task and whose get() method will throw an exception upon cancellation
     * @throws UtilityException
     * <br>
     *             if task is null;<br>
     *             if the delay is equal to or less than zero
     * @throws RejectedExecutionException
     *             at discretion of <tt>RejectedExecutionHandler</tt>, if task cannot be accepted
     *             for execution because the executor has been shut down.
     */
    public static final ScheduledFuture<?> scheduleContinuedTaskInMillis(Runnable task, long initialdelay, long delay) throws UtilityException, RejectedExecutionException{
        if(task == null){
            throw new UtilityException("arg.null", "Runnable task");
        }
        if(initialdelay <= 0 || delay <= 0){
            throw new UtilityException("delay.negative", "delay");
        }
        ScheduledFuture<?> stask = threadpool.scheduleAtFixedRate(task, initialdelay, delay, MILLISECONDS);
        tasks.put(new Task(task), stask);
        return stask;
    }

    /**
     * Executes a {@link Runnable} task continually on a fixed delay in seconds
     * 
     * @param task
     *            the {@link Runnable} task to execute
     * @param initialdelay
     *            the delay before initial execution
     * @param delay
     *            the delay between additional executions
     * @return a ScheduledFuture representing pending completion of the task and whose get() method will throw an exception upon cancellation
     * @throws UtilityException
     * <br>
     *             if task is null;<br>
     *             if the delay is equal to or less than zero
     * @throws RejectedExecutionException
     *             at discretion of <tt>RejectedExecutionHandler</tt>, if task cannot be accepted
     *             for execution because the executor has been shut down.
     */
    public static final ScheduledFuture<?> scheduleContinuedTaskInSeconds(Runnable task, long initialdelay, long delay) throws UtilityException, RejectedExecutionException{
        if(task == null){
            throw new UtilityException("arg.null", "Runnable task");
        }
        if(initialdelay <= 0 || delay <= 0){
            throw new UtilityException("delay.negative", "delay");
        }
        ScheduledFuture<?> stask = threadpool.scheduleAtFixedRate(task, initialdelay, delay, SECONDS);
        tasks.put(new Task(task), stask);
        return stask;
    }

    /**
     * Executes a {@link Runnable} task continually on a fixed delay in minutes
     * 
     * @param task
     *            the {@link Runnable} task to execute
     * @param initialdelay
     *            the delay before initial execution
     * @param delay
     *            the delay between additional executions
     * @return a ScheduledFuture representing pending completion of the task and whose get() method will throw an exception upon cancellation
     * @throws UtilityException
     * <br>
     *             if task is null;<br>
     *             if the delay is equal to or less than zero
     * @throws RejectedExecutionException
     *             at discretion of <tt>RejectedExecutionHandler</tt>, if task cannot be accepted
     *             for execution because the executor has been shut down.
     */
    public static final ScheduledFuture<?> scheduleContinuedTaskInMinutes(Runnable task, long initialdelay, long delay) throws UtilityException, RejectedExecutionException{
        if(task == null){
            throw new UtilityException("arg.null", "Runnable task");
        }
        if(initialdelay <= 0 || delay <= 0){
            throw new UtilityException("delay.negative", "delay");
        }
        ScheduledFuture<?> stask = threadpool.scheduleAtFixedRate(task, initialdelay, delay, MINUTES);
        tasks.put(new Task(task), stask);
        return stask;
    }

    /**
     * Executes a {@link Runnable} task continually on a fixed delay in the specified TimeUnit
     * 
     * @param task
     *            the {@link Runnable} task to execute
     * @param initialdelay
     *            the delay before initial execution
     * @param delay
     *            the delay between additional executions
     * @param timeunit
     *            the {@link TimeUnit} to use
     * @return a ScheduledFuture representing pending completion of the task and whose get() method will throw an exception upon cancellation
     * @throws UtilityException
     * <br>
     *             if task is null;<br>
     *             if the delay is equal to or less than zero
     * @throws RejectedExecutionException
     *             at discretion of <tt>RejectedExecutionHandler</tt>, if task cannot be accepted
     *             for execution because the executor has been shut down.
     */
    public static final ScheduledFuture<?> scheduleContinuedTask(Runnable task, long initialdelay, long delay, TimeUnit timeunit) throws UtilityException, RejectedExecutionException{
        if(task == null){
            throw new UtilityException("arg.null", "Runnable task");
        }
        if(timeunit == null){
            throw new UtilityException("arg.null", "Runnable task");
        }
        if(initialdelay <= 0 || delay <= 0){
            throw new UtilityException("delay.negative", "delay");
        }
        ScheduledFuture<?> stask = threadpool.scheduleAtFixedRate(task, initialdelay, delay, timeunit);
        tasks.put(new Task(task), stask);
        return stask;
    }

    /**
     * Removes a {@link Runnable} task from the pool
     * 
     * @param task
     *            the {@link Runnable} task to be removed
     * @return {@code true} if successfully removed, {@code false} if already stopped or non-existent
     */
    public static final boolean removeTask(Runnable task){
        boolean check = false;
        if(tasks.containsKey(task)){
            check = tasks.get(task).cancel(true);
            tasks.remove(task);
        }
        if(!check){
            check = threadpool.remove(task);
        }
        if(check){
            threadpool.purge();
        }
        return check;
    }

    /**
     * Removes a {@link Callable} task from the pool
     * 
     * @param task
     *            the {@link Callable} task to be removed
     * @return {@code true} if successfully removed, {@code false} if already stopped or non-existent
     */
    public static final boolean removeTask(Callable<?> task){
        boolean check = false;
        if(tasks.containsKey(task)){
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
    private static final class TaskCleaner implements Runnable{

        @Override
        public final void run(){ //Run to clean up any memory leaks we may cause by holding dead tasks
            if(tasks.isEmpty()){
                return; // skip cleaning if there are no tasks
            }
            Iterator<Task> taskItr = tasks.keySet().iterator();
            while(taskItr.hasNext()){
                Task task = taskItr.next(); //Get the task
                if(tasks.get(task).isDone()){ // Check task for completion
                    try{
                        tasks.get(task).get(); // Test for execution exceptions
                    }
                    catch(InterruptedException e){
                        // The task was probably canceled so skip this
                    }
                    catch(ExecutionException eex){
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
    public final static float getClassVersion(){
        return classVersion;
    }
}
