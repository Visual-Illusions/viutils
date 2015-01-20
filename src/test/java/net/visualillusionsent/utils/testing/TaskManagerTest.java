package net.visualillusionsent.utils.testing;

import net.visualillusionsent.utils.TaskManager;
import org.junit.Assert;
import org.junit.Test;

/**
 * Copyright (C) 2015 Visual Illusions Entertainment
 * All Rights Reserved.
 *
 * @author Jason Jones (darkdiplomat)
 */
public class TaskManagerTest {

    @Test
    public void testTaskManagerRemove() {
        TaskTest taskTest = new TaskTest();
        TaskManager.scheduleContinuedTaskInMillis(taskTest, 1, 20);
        try {
            Thread.sleep(2);
        }
        catch (InterruptedException e) {
        }
        Assert.assertTrue(TaskManager.removeTask(taskTest));
    }

    private class TaskTest implements Runnable {

        public void run() {
            System.out.println("Hello World");
        }
    }
}
