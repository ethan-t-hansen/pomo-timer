package persistence;

import model.Task;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class JsonTest {
    protected void checkTask(String title, Task task) {
        assertEquals(title, task.getTitle());
    }
}
