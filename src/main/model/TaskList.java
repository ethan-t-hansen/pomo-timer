package model;

import java.util.ArrayList;
import java.util.List;

public class TaskList {

    private List<Task> taskList;

    // EFFECTS: creates a new empty task list
    public TaskList() {
        this.taskList = new ArrayList<>();
    }

    // getters

    public List<Task> getTaskList() {
        return taskList;
    }

    public String emptyStatement() {
        return "You have no tasks left.";
    }

    public String statement() {
        return "Current Tasks: ";
    }

    // setters

    // MODIFIES: this
    // EFFECTS: adds a task to the task list
    public void addTask(Task task) {
        taskList.add(task);
    }

    // REQUIRES: Task list is not empty
    // MODIFIES: this
    // EFFECTS: removes the task from the specified index in a task list
    public void removeTask(int index) {
        taskList.remove(index);
    }

    // REQUIRES: completed list size > 0
    // MODIFIES: this
    // EFFECTS: removes all elements in the comp list
    public void clearTaskList() {
        for (int i = this.getTaskList().size() - 1; i >= 0; i--) {
            this.removeTask(i);
        }
    }

}
