package model;

import org.json.JSONArray;
import org.json.JSONObject;
import persistence.Writable;

import java.util.ArrayList;
import java.util.List;

/* Represents a list of Task objects */

public class TaskList implements Writable {

    private final List<Task> taskList;

    // EFFECTS: creates a new empty task list
    public TaskList() {
        this.taskList = new ArrayList<>();
    }

    // getters

    public List<Task> getTaskList() {
        return taskList;
    }

    public int length() {
        return taskList.size();
    }

    public Task taskAt(int index) {
        return taskList.get(index);
    }

    public String emptyStatement() {
        return "You have no current tasks.";
    }

    public String statement() {
        return "Current Tasks: ";
    }

    // setters

    // MODIFIES: this
    // EFFECTS: adds a task to the task list
    //          type is a 0 or 1: indicates whether task is being added to a task list or completed
    public void addTask(Task task, int type) {
        taskList.add(task);
        if (type == 0) {
            EventLog.getInstance().logEvent(new Event("New task added: " + task.getTitle()));
        }
    }

    // REQUIRES: Task list is not empty
    // MODIFIES: this
    // EFFECTS: removes the task from the specified index in a task list
    //          type is a 0 or 1: indicates whether task is being removed or completed
    public void removeTask(int index, int type) {
        Task t = taskList.get(index);
        String msg = "Task removed: ";
        taskList.remove(index);
        if (type == 1) {
            msg = "Task completed: ";
        }
        EventLog.getInstance().logEvent(new Event(msg + t.getTitle()));
    }

    // REQUIRES: completed list size > 0
    // MODIFIES: this
    // EFFECTS: removes all elements in the comp list
    public void clearTaskList() {
        for (int i = this.getTaskList().size() - 1; i >= 0; i--) {
            this.removeTask(i, 0);
        }
        EventLog.getInstance().logEvent(new Event("Task list cleared."));
    }

    @Override
    public JSONObject toJson() {
        JSONObject json = new JSONObject();
        json.put("Tasks", tasksToJson());
        return json;
    }



    // EFFECTS: returns things in this workroom as a JSON array
    private JSONArray tasksToJson() {
        JSONArray jsonArray = new JSONArray();

        for (Task t : taskList) {
            jsonArray.put(t.toJson());
        }

        return jsonArray;
    }


}
