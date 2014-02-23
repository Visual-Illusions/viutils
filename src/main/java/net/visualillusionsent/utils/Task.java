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
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with this program.
 * If not, see http://www.gnu.org/licenses/lgpl.html.
 */
package net.visualillusionsent.utils;

import java.util.concurrent.Callable;
import java.util.logging.Logger;

/**
 * Wrapper used to combine {@link Runnable} and {@link Callable} objects given to the TaskManager
 *
 * @author Jason (darkdiplomat)
 */
final class Task {

    /* The Runnable/Callable task */
    private final Object task;

    /**
     * Constructs a new Task from a {@link Runnable}
     *
     * @param runnable
     *         the {@link Runnable} task to wrap
     */
    Task(Runnable runnable) {
        this.task = runnable;
    }

    /**
     * Constructs a new Task from a {@link Callable}
     *
     * @param callable
     *         the {@link Callable} task to wrap
     */
    Task(Callable<?> callable) {
        this.task = callable;
    }

    /**
     * Prints out the error that was caused if any
     *
     * @param thrown
     *         the {@link Throwable} retrieved from the {@link java.util.concurrent.Future} object
     */
    final void printError(Throwable thrown) {
        Logger.getLogger(Logger.GLOBAL_LOGGER_NAME).severe("[VIUtils] Unhandled Exception thrown from Task: " + task.toString() + ". Check the viuitlslogs for more details");
        UtilsLogger.severe("Exception in Task: " + task.toString(), thrown);
    }

    @Override
    public final boolean equals(Object obj) {
        if (obj instanceof Task) {
            return obj == this;
        }
        return task.equals(obj);
    }

    @Override
    public final int hashCode() {
        return task.hashCode();
    }
}
