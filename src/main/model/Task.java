package model;

public class Task {

    String title;
    boolean complete;

    // REQUIRES: title has non zero length
    // EFFECTS: creates a task with a given title and a status of false
    public Task(String title) {
        this.title = title;
        this.complete = false;
    }

    // getters

    public String getTitle() {
        return title;
    }

    public Boolean isComplete() {
        return complete;
    }

    // setters

    // MODIFIES: this
    // EFFECTS: changes a given task's title
    public void changeTitle(String newTitle) {
        title = newTitle;
    }

}
