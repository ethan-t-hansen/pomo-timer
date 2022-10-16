package model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;

public class CompletedTaskListTest {

    CompletedTaskList testCompletedTaskList;

    @BeforeEach
    void runBefore() {
        testCompletedTaskList = new CompletedTaskList();
    }

    @Test
    void testConstructor() {
        assertEquals(Collections.emptyList(), testCompletedTaskList.getTaskList());
    }

    @Test
    void testEmptyStatement() {
        assertEquals(testCompletedTaskList.emptyStatement(), "You have not completed any tasks.");
    }

    @Test
    void testStatement() {
        assertEquals(testCompletedTaskList.statement(), "Completed Tasks: ");
    }
}
