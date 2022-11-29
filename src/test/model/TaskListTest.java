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
        testTaskList.addTask(newTask, 0);
        assertEquals(testTaskList.taskAt(0), newTask);
    }

    @Test
    void testAddTwoTasks() {
        testTaskList.addTask(task2, 0);
        testTaskList.addTask(task1, 0);
        assertEquals(testTaskList.taskAt(0), task2);
        assertEquals(testTaskList.taskAt(1), task1);
    }

    @Test
    void testRemoveMiddle() {
        testTaskList.addTask(task3, 0);
        testTaskList.addTask(task4, 0);
        testTaskList.addTask(task5, 0);
        testTaskList.removeTask(1, 0);
        assertFalse(testTaskList.getTaskList().contains(task4));
        assertTrue(testTaskList.getTaskList().contains(task3));
        testTaskList.removeTask(0, 0);
        assertFalse(testTaskList.getTaskList().contains(task3));
    }

    @Test
    void testRemoveOne() {
        testTaskList.addTask(task3, 0);
        testTaskList.removeTask(0, 0);
        assertFalse(testTaskList.getTaskList().contains(task3));

        assertFalse(testTaskList.getTaskList().contains(task3));
    }

    @Test
    void testClearTaskList() {
        testTaskList.addTask(task1, 0);
        testTaskList.addTask(task2, 0);
        testTaskList.addTask(task3, 0);
        assertEquals(testTaskList.length(), 3);
        testTaskList.clearTaskList();
        assertEquals(testTaskList.length(), 0);
    }

    @Test
    void testEmptyStatement() {
        assertEquals(testTaskList.emptyStatement(), "You have no current tasks.");
    }

    @Test
    void testStatement() {
        assertEquals(testTaskList.statement(), "Current Tasks: ");
    }



}