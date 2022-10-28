package persistence;

import model.Task;
import model.TaskList;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

class JsonWriterTest extends JsonTest {
    //NOTE TO CPSC 210 STUDENTS: the strategy in designing tests for the JsonWriter is to
    //write data to a file and then use the reader to read it back in and check that we
    //read in a copy of what was written out.

    @Test
    void testWriterInvalidFile() {
        try {
            TaskList tl = new TaskList();
            JsonWriter writer = new JsonWriter("./data/my\0illegal:fileName.json");
            writer.open();
            fail("IOException was expected");
        } catch (IOException e) {
            // pass
        }
    }

    @Test
    void testWriterEmptyTaskList() {
        try {
            TaskList tl = new TaskList();
            JsonWriter writer = new JsonWriter("./data/testWriterEmptyTaskList.json");
            writer.open();
            writer.write(tl);
            writer.close();

            JsonReader reader = new JsonReader("./data/testWriterEmptyTaskList.json");
            tl = reader.read();
            assertEquals(0, tl.length());
        } catch (IOException e) {
            fail("Exception should not have been thrown");
        }
    }

    @Test
    void testWriterGeneralWorkroom() {
        try {
            TaskList tl = new TaskList();
            tl.addTask(new Task("Mop floor"));
            tl.addTask(new Task("Wash dishes"));
            JsonWriter writer = new JsonWriter("./data/testWriterGeneralTaskList.json");
            writer.open();
            writer.write(tl);
            writer.close();

            JsonReader reader = new JsonReader("./data/testWriterGeneralTaskList.json");
            tl = reader.read();
            List<Task> tasks = tl.getTaskList();
            assertEquals(2, tasks.size());
            checkTask("Mop floor", tasks.get(0));
            checkTask("Wash dishes", tasks.get(1));

        } catch (IOException e) {
            fail("Exception should not have been thrown");
        }
    }
}