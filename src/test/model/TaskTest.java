package model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class TaskTest {

    private Task testTask;

    @BeforeEach
    void runBefore() {
        testTask = new Task("Test");
    }

    @Test
    void testConstructor() {
        assertEquals("Test", testTask.getTitle());
    }

    @Test
    void testChangeTitle() {
        testTask.changeTitle("Do the laundry");
        assertEquals(testTask.getTitle(), "Do the laundry");
    }

}
