package model;

public class Task {

    String title;

    // REQUIRES: title has non zero length
    // EFFECTS: creates a task with a given title
    public Task(String title) {
        this.title = title;
    }

    // getters

    public String getTitle() {
        return title;
    }

    // setters

    // MODIFIES: this
    // EFFECTS: changes a given task's title
    public void changeTitle(String newTitle) {
        title = newTitle;
    }

}
