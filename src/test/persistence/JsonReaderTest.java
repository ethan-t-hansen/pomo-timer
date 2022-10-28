package persistence;

import model.Task;
import model.TaskList;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

class JsonReaderTest extends JsonTest {

    @Test
    void testReaderNonExistentFile() {
        JsonReader reader = new JsonReader("data/noSuchFile.json");
        try {
            TaskList tl = reader.read();
            fail("IOException expected");
        } catch (IOException e) {
            // pass
        }
    }

    @Test
    void testReaderEmptyWorkRoom() {
        JsonReader reader = new JsonReader("./data/testReaderEmptyTaskList.json");
        try {
            TaskList tl = reader.read();
            assertEquals(0, tl.length());
        } catch (IOException e) {
            fail("Couldn't read from file");
        }
    }

    @Test
    void testReaderGeneralWorkRoom() {
        JsonReader reader = new JsonReader("./data/testReaderGeneralTaskList.json");
        try {
            TaskList tl = reader.read();
            List<Task> newTaskList = tl.getTaskList();
            assertEquals(2, newTaskList.size());
            assertEquals("Do dishes", newTaskList.get(0).getTitle());
            assertEquals("Clean room", newTaskList.get(1).getTitle());
        } catch (IOException e) {
            fail("Couldn't read from file");
        }
    }
}