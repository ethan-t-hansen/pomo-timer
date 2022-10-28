package model;

/* Represents a Task that has been completed */

public class CompletedTaskList extends TaskList {

    public CompletedTaskList() {
        super();
    }

    // EFFECTS: returns a statement for when the task list is empty
    @Override
    public String emptyStatement() {
        return "You have not completed any tasks.";
    }

    // EFFECTS: returns a statement that is followed by a list of completed tasks
    @Override
    public String statement() {
        return "Completed Tasks: ";
    }

}
