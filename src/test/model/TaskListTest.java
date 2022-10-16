package model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;

class TaskListTest {

    private TaskList testTaskList;

    Task task1 = new Task("Shred papers");
    Task task2 = new Task("Walk dog");
    Task task3 = new Task("Do homework");
    Task task4 = new Task("Wash dishes");
    Task task5 = new Task("Clean floor");

    @BeforeEach
    void runBefore() {
        testTaskList = new TaskList();
    }

    @Test
    void testConstructor() {
        assertEquals(Collections.emptyList(), testTaskList.getTaskList());
    }

    @Test
    void testAdd() {
        Task newTask = new Task("Do work");
        testTaskList.addTask(newTask);
        assertEquals(testTaskList.getTaskList().get(0), newTask);
    }

    @Test
    void testAddTwoTasks() {
        testTaskList.addTask(task2);
        testTaskList.addTask(task1);
        assertEquals(testTaskList.getTaskList().get(0), task2);
        assertEquals(testTaskList.getTaskList().get(1), task1);
    }

    @Test
    void testRemoveMiddle() {
        testTaskList.addTask(task3);
        testTaskList.addTask(task4);
        testTaskList.addTask(task5);
        testTaskList.removeTask(1);
        TaskList dummy = new TaskList();
        dummy.addTask(task3);
        dummy.addTask(task5);
        assertFalse(testTaskList.getTaskList().contains(task4));
        assertEquals(dummy.getTaskList(),testTaskList.getTaskList());
    }

}