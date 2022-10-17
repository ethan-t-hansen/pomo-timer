package ui;

import model.*;

import java.util.Collections;
import java.util.Scanner;
import java.util.Timer;
import java.util.TimerTask;

public class PomoApp {

    private Scanner input;
    private TaskList tasks;
    private TaskList completedTasks;
    private Timer timer;

    int studyDur;
    int lastDur;
    boolean isActive;
    boolean isPaused;

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
                System.out.println("\nGoodbye!");
                System.exit(0);
            } else {
                processCommand(command);
            }

        }
    }

    // MODIFIES: this
    // EFFECTS: initializes scanner, task lists, and variables
    private void inScanner() {
        isActive = false;
        isPaused = false;
        tasks = new TaskList();
        completedTasks = new CompletedTaskList();
        input = new Scanner(System.in);
        input.useDelimiter("\n");
    }

    // MODIFIES: this
    // EFFECTS: processes user command
    private void processCommand(String command) {
        if (command.equals("a")) {
            doAddTask();
        } else if (command.equals("x")) {
            doRemoveTask();
        } else if (command.equals("v")) {
            printTaskSummary();
        } else if (command.equals("c")) {
            completeTask();
        } else if (command.equals("t")) {
            if (!isActive) {
                makeNewTimer();
            } else {
                System.out.println(displayTime(studyDur));
            }
        } else if (command.equals("p")) {
            pauseOrResume();
        } else if (command.equals("r")) {
            resetTimer();
        } else {
            System.out.println("Invalid command");
        }
    }

    // EFFECTS: displays menu of options to user
    private void displayMenu() {

        System.out.println("\nSelect from:");
        System.out.println("\ta -> add a task");
        System.out.println("\tx -> remove a task");
        System.out.println("\tv -> view tasks");
        System.out.println("\tc -> mark a task as complete");
        if (isActive) {
            System.out.println("\tt -> view time remaining on active timer");
            if (isPaused) {
                System.out.println("\tp -> resume timer");
            } else {
                System.out.println("\tp -> pause timer");
            }
            System.out.println("\tr -> reset timer");
        } else {
            System.out.println("\tt -> start a new timer");
        }
        System.out.println("\tq -> quit");
    }

    // MODIFIES: tasks
    // EFFECTS: adds a task to the task list
    private void doAddTask() {
        System.out.println("Please enter the title of your task:");
        Task newTask = new Task(input.next());
        tasks.addTask(newTask);
        System.out.println("Succesfully added task with title: " + newTask.getTitle());
    }

    // MODIFIES: tasks
    // EFFECTS: removes a task from the task list
    // REQUIRES: task list is not empty
    private void doRemoveTask() {
        System.out.println("Please enter list number of the task you'd like to remove");
        int userInput = input.nextInt();
        if (userInput <= tasks.length() && userInput >= 1) {
            System.out.println("Successfully removed task with title:");
            System.out.println("\"" + tasks.taskAt(userInput - 1).getTitle() + "\"");
            tasks.removeTask(userInput - 1);
        } else {
            System.out.println("The specified task doesn't exist!");
        }
    }

    // MODIFIES: tasks, completedTasks
    // EFFECTS: removes a task from the tasks and adds it to completedTasks
    // REQUIRES: task list is not empty
    //      note: some code duplication with doRemoveTask, can be optimized
    private void completeTask() {
        System.out.println("Please enter list number of the task you've completed");
        int userInput = input.nextInt();
        if (userInput <= tasks.length() && userInput >= 1) {
            System.out.println("Successfully completed task with title:");
            System.out.println("\"" + tasks.taskAt(userInput - 1).getTitle() + "\"");
            completedTasks.addTask(new Task(tasks.taskAt(userInput - 1).getTitle()));
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

    // EFFECTS: prints both current and completed tasks
    private void printTaskSummary() {
        printTasks(tasks);
        System.out.println();
        printTasks(completedTasks);
    }

    // MODIFIES: studyDur
    // EFFECTS: sets a timer duration by converting user input to seconds
    private void adjustSettings() {
        System.out.println("Set timer duration (in minutes): ");
        studyDur = input.nextInt() * 60;
    }

    // MODIFIES: isActive
    // EFFECTS: sequence of method calls to initialize a new timer
    private void makeNewTimer() {
        adjustSettings();
        setTimer();
        System.out.println("Timer started!");
        isActive = true;
    }

    // MODIFIES: isPaused
    // EFFECTS: either pauses or resumes a timer depending on the status of isPaused
    private void pauseOrResume() {
        if (!isPaused) {
            isPaused = true;
            System.out.println("Timer paused.");
        } else {
            setTimer();
            isPaused = false;
            System.out.println("Timer resumed.");
        }
    }

    // MODIFIES: isPaused, isActive, studyDur
    // EFFECTS: makes a timer inactive, resets isPaused, and sets the study dur to the last time set
    private void resetTimer() {
        isPaused = false;
        isActive = false;
        studyDur = lastDur;
        System.out.println("Timer reset.");
    }

    // MODIFIES: studyDur
    // EFFECTS: initializes a timer that counts down until studyDur hits 0
    public void setTimer() {

        lastDur = studyDur;
        timer = new Timer();

        timer.scheduleAtFixedRate(new TimerTask() {
            public void run() {
                studyDur--;
                if (isPaused) {
                    timer.cancel();
                }
                if (studyDur < 0) {
                    timer.cancel();
                    isActive = false;
                    System.out.println("Break time!");
                }
            }
        }, 0, 1000);
    }

    // EFFECTS: converts a given amount of time (in seconds) to a string format similar to a digital time display
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

}
