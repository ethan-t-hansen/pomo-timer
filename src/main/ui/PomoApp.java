package ui;

import model.*;

import java.util.Collections;
import java.util.Scanner;
import java.util.Timer;
import java.util.TimerTask;

public class PomoApp {

    private Scanner input;
    private TaskList tasks = new TaskList();
    private TaskList completedTasks = new CompletedTaskList();

    Timer timer = new Timer();
    int studyDur;
    boolean isActive = false;

    public PomoApp() {
        runPomo();
    }

    // MODIFIES: this
    // EFFECTS: processes user input
    private void runPomo() {
        boolean keepGoing = true;
        String command = null;

        inScanner();

        while (keepGoing) {
            displayMenu();
            command = input.next();
            command = command.toLowerCase();

            if (command.equals("q")) {
                keepGoing = false;
            } else {
                processCommand(command);
            }

        }

        System.out.println("\nTerminating...");
    }

    // MODIFIES: this
    // EFFECTS: initializes scanner
    private void inScanner() {
        input = new Scanner(System.in);
        input.useDelimiter("\n");
    }

    // MODIFIES: this
    // EFFECTS: processes user command
    private void processCommand(String command) {
        if (command.equals("a")) {
            doAddTask();
        } else if (command.equals("r")) {
            doRemoveTask();
        } else if (command.equals("v")) {
            printTasks(tasks);
            System.out.println();
            printTasks(completedTasks);
        } else if (command.equals("m")) {
            completeTask();
        } else if (command.equals("t")) {
            if (!isActive) {
                adjustSettings();
                setTimer();
                isActive = true;
            } else {
                System.out.println(displayTime(studyDur));
            }
        }
    }

    // MODIFIES: tasks
    // EFFECTS: adds a task to the task list
    private void doAddTask() {
        System.out.println("Please enter the title of your task:");
        Task newTask = new Task(input.next());
        tasks.addTask(newTask);
    }

    // MODIFIES: tasks
    // EFFECTS: removes a task from the task list
    // REQUIRES: task list is not empty
    private void doRemoveTask() {
        System.out.println("Please enter list number of the task you'd like to remove");
        int userInput = input.nextInt();
        if (userInput <= tasks.getTaskList().size() && userInput >= 1) {
            System.out.println("Successfully removed task with title:");
            System.out.println("\"" + tasks.getTaskList().get(userInput - 1).getTitle() + "\"");
            tasks.removeTask(userInput - 1);
        } else {
            System.out.println("The specified task doesn't exist!");
        }
    }

    // MODIFIES: tasks, completedTasks
    // EFFECTS: removes a task from the tasks and adds it to completedTasks
    // REQUIRES: task list is not empty
    private void completeTask() { // NOTE: some code duplication with doRemoveTask, can be improved
        System.out.println("Please enter list number of the task you've completed");
        int userInput = input.nextInt();
        if (userInput <= tasks.getTaskList().size() && userInput >= 1) {
            System.out.println("Successfully completed task with title:");
            System.out.println("\"" + tasks.getTaskList().get(userInput - 1).getTitle() + "\"");
            completedTasks.addTask(new Task(tasks.getTaskList().get(userInput - 1).getTitle()));
            tasks.removeTask(userInput - 1);
        } else {
            System.out.println("The specified task doesn't exist!");
        }
    }

    // EFFECTS: prints out current task list
    private void printTasks(TaskList taskList) {
        if (taskList.getTaskList().equals(Collections.emptyList())) {
            System.out.println(taskList.emptyStatement());
        } else {
            System.out.println(taskList.statement());
            int i = 1;
            for (Task task : taskList.getTaskList()) {
                System.out.println(i + ". " + task.getTitle());
                i++;
            }
        }
    }

    // MODIFIES: studyDur
    // EFFECTS: sets a timer duration by converting user input to seconds
    private void adjustSettings() {
        System.out.println("Set timer duration (in minutes): ");
        studyDur = input.nextInt() * 60;
    }

    // MODIFIES: studyDur
    // EFFECTS: initializes a timer that counts down until studyDur hits 0
    public void setTimer() {

        timer = new Timer();

        timer.scheduleAtFixedRate(new TimerTask() {

            public void run() {

                studyDur--;

                if (studyDur < 0) {
                    timer.cancel();
                    isActive = false;
                    System.out.println("Break time!");
                }

            }
        }, 0, 1000);
    }

    // EFFECTS: converts a given amount of time (in seconds) to a string format akin to a digital timer / clock
    public String displayTime(int timeLeft) {

        String seconds = Integer.toString(timeLeft % 60);
        String minutes = Integer.toString((timeLeft % 3600) / 60);
        String hours = Integer.toString(timeLeft / 3600);

        if (seconds.length() < 2) {
            seconds = "0" + seconds;
        }

        if (minutes.length() < 2) {
            minutes = "0" + minutes;
        }

        if (hours.length() < 2) {
            hours = "0" + hours;
        }
        return "Time remaining: " + minutes + ":" + seconds;
    }

    // EFFECTS: displays menu of options to user
    private void displayMenu() {

        String timerMenu;

        if (isActive) {
            timerMenu = "view time remaining on active timer";
        } else {
            timerMenu = "start a new timer";
        }

        System.out.println("\nSelect from:");
        System.out.println("\ta -> add a task");
        System.out.println("\tr -> remove a task");
        System.out.println("\tv -> view tasks");
        System.out.println("\tm -> mark a task as complete");
        System.out.println("\tt -> " + timerMenu);
        System.out.println("\tq -> quit");

    }

}
