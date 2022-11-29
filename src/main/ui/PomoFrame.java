package ui;

import model.CompletedTaskList;
import model.Event;
import model.EventLog;
import model.Task;
import model.TaskList;
import persistence.JsonReader;
import persistence.JsonWriter;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

/*
SOURCES:
    https://www.tutorialspoint.com/swingexamples/show_input_dialog_text.htm
    https://stackoverflow.com/questions/2536873/how-can-i-set-size-of-a-button
    https://stackoverflow.com/questions/16134549/how-to-make-a-splash-screen-for-gui
    https://stackoverflow.com/questions/10468149/jframe-on-close-operation
    https://github.students.cs.ubc.ca/CPSC210/AlarmSystem/
    https://github.students.cs.ubc.ca/CPSC210/JsonSerializationDemo
*/

/* The main graphical application for the Pomodoro app */

public class PomoFrame extends JFrame {

    private static final int WIDTH = 600;
    private static final int HEIGHT = 400;
    private JFrame frame;
    private JPanel buttonPanel;
    private JLabel timerLabel;
    private JLabel pomoIcon;
    private JDesktopPane desktop;
    private JInternalFrame actionPanel;
    private JInternalFrame timerPanel;
    private JScrollPane taskView;
    private JScrollPane compTaskView;
    private JMenuBar menuBar;

    private static final String TL_JSON_STORE = "./data/taskList.json";
    private static final String CTL_JSON_STORE = "./data/completedTaskList.json";
    private static final String ST_JSON_STORE = "./data/totalStudyTime.json";
    private TaskList tasks;
    private TaskList compTasks;
    private Timer timer;

    private int previousSessionTime;
    private int totalStudyTime;
    private int studyDur;
    private boolean isActive;
    private boolean isPaused;

    private JsonWriter jsonTaskWriter;
    private JsonReader jsonTaskReader;
    private JsonWriter jsonCompTaskWriter;
    private JsonReader jsonCompTaskReader;
    private JsonWriter jsonTimeWriter;
    private JsonReader jsonTimeReader;

    // The constructor: initializes the primary panes, panels, and fields.
    public PomoFrame() throws FileNotFoundException {

        splashScreen();

        tasks = new TaskList();
        compTasks = new CompletedTaskList();

        desktop = new JDesktopPane();
        desktop.addMouseListener(new DesktopFocusAction());

        taskViewSettings();
        compTaskViewSettings();
        inInternalFrames();
        addMenu();

        setContentPane(desktop);
        setTitle("CPSC 210: Pomodoro");
        setSize(WIDTH, HEIGHT);

        inJson();
        addButtonPanel();
        addTimerPanel();

        changePanelSettings();
        desktop.add(actionPanel);
        desktop.add(timerPanel);

        centreOnScreen();
        setVisible(true);
        addClosingOperation();

    }

    // set closing operation to print to console
    public void addClosingOperation() {
        this.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                printLog(EventLog.getInstance());
                System.exit(0);
            }
        });
    }

    // generates the splash screen for the application
    public void splashScreen() {
        JWindow window = new JWindow();
        ImageIcon loadingScreen = new ImageIcon("loading_screen.gif");
        window.getContentPane().add(new JLabel("", loadingScreen, SwingConstants.CENTER));
        int width = Toolkit.getDefaultToolkit().getScreenSize().width;
        int height = Toolkit.getDefaultToolkit().getScreenSize().height;
        window.setBounds((width - loadingScreen.getIconWidth() / 2) / 2,
                (height - loadingScreen.getIconHeight() / 2) / 2,
                loadingScreen.getIconWidth() / 2,
                loadingScreen.getIconHeight() / 2);
        window.setVisible(true);
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        window.setVisible(false);
        window.dispose();
    }

    // Helper to initialize internal frames
    public void inInternalFrames() {
        actionPanel = new JInternalFrame("Actions", false, false, false, false);
        actionPanel.setLayout(new BorderLayout());
        timerPanel = new JInternalFrame("Timer", false, false, false, false);
        timerPanel.setLayout(new BorderLayout());
        timerPanel.setBounds(WIDTH - 320, 0, 300, 120);
    }

    // Helper to change panel settings
    public void changePanelSettings() {
        actionPanel.pack();
        actionPanel.setVisible(true);
        timerPanel.setVisible(true);
    }

    // Helper to initialize the task view
    private void taskViewSettings() {
        DefaultListModel<String> myTasks = new DefaultListModel<>();
        myTasks.addElement("Current Tasks: ");
        for (int i = 0; i < tasks.length(); i++) {
            myTasks.addElement((i + 1) + ". " + tasks.getTaskList().get(i).getTitle());
        }
        JList currTaskList = new JList<String>(myTasks);

        taskView = new JScrollPane(currTaskList);
        taskView.setLayout(new ScrollPaneLayout());
        taskView.add(new JButton(new ReturnToDesktopAction()));
    }

    // Helper to initialize the completed task view
    private void compTaskViewSettings() {
        DefaultListModel<String> myTasks = new DefaultListModel<>();
        myTasks.addElement("Completed Tasks: ");
        for (int i = 0; i < compTasks.length(); i++) {
            myTasks.addElement((i + 1) + ". " + compTasks.getTaskList().get(i).getTitle());
        }
        JList compTaskList = new JList<String>(myTasks);

        compTaskView = new JScrollPane(compTaskList);
        compTaskView.setLayout(new ScrollPaneLayout());
        compTaskView.add(new JButton(new ReturnToDesktopAction()));
    }

    //Adds menu bar
    private void addMenu() {
        menuBar = new JMenuBar();
        JMenu fileMenu = new JMenu("File");
        fileMenu.setMnemonic('F');
        addMenuItem(fileMenu, new SaveTasksAction(), null);
        addMenuItem(fileMenu, new LoadTasksAction(), null);
        addMenuItem(fileMenu, new SaveCompTasksAction(), null);
        addMenuItem(fileMenu, new LoadCompTasksAction(), null);
        addMenuItem(fileMenu, new SaveSessionStudyTime(), null);
        addMenuItem(fileMenu, new LoadSessionStudyTime(), null);
        menuBar.add(fileMenu);

        addViewTaskTimerMenus();

        setJMenuBar(menuBar);
    }

    // Helper to add more to the menus
    private void addViewTaskTimerMenus() {
        JMenu viewMenu = new JMenu("View");
        viewMenu.setMnemonic('V');
        addMenuItem(viewMenu, new ViewTasksAction(), null);
        addMenuItem(viewMenu, new ViewCompTasksAction(), null);
        addMenuItem(viewMenu, new ReturnToDesktopAction(), null);
        menuBar.add(viewMenu);

        JMenu taskMenu = new JMenu("Task");
        taskMenu.setMnemonic('t');
        addMenuItem(taskMenu, new AddTaskAction(), null);
        addMenuItem(taskMenu, new RemoveTaskAction(), null);
        menuBar.add(taskMenu);

        JMenu systemMenu = new JMenu("Timer");
        systemMenu.setMnemonic('t');
        addMenuItem(systemMenu, new StartTimerAction(),
                KeyStroke.getKeyStroke("control T"));
        addMenuItem(systemMenu, new PauseTimerAction(),
                KeyStroke.getKeyStroke("control P"));
        addMenuItem(systemMenu, new ResetTimerAction(),
                KeyStroke.getKeyStroke("control R"));
        menuBar.add(systemMenu);
    }

    /**
     * Adds an item with given handler to the given menu
     * @param theMenu  menu to which new item is added
     * @param action   handler for new menu item
     * @param accelerator    keystroke accelerator for this menu item
     */
    private void addMenuItem(JMenu theMenu, AbstractAction action, KeyStroke accelerator) {
        JMenuItem menuItem = new JMenuItem(action);
        menuItem.setMnemonic(menuItem.getText().charAt(0));
        menuItem.setAccelerator(accelerator);
        theMenu.add(menuItem);
    }

    // Add panel to display timer
    private void addTimerPanel() {
        timerLabel = new JLabel(displayTime(studyDur));
        timerLabel.setFont(new Font("Serif", Font.PLAIN, 100));
        timerLabel.setHorizontalAlignment(JLabel.CENTER);
        timerPanel.add(timerLabel, BorderLayout.CENTER);
    }

    // Add panel of buttons to perform program functions
    private void addButtonPanel() {
        buttonPanel = new JPanel(new GridLayout(7, 1));
        buttonPanel.add(new JButton(new AddTaskAction()));
        buttonPanel.add(new JButton(new RemoveTaskAction()));
        buttonPanel.add(new JButton(new CompleteTaskAction()));
        buttonPanel.add(new JButton(new ClearTaskListAction()));
        buttonPanel.add(new JButton(new StartTimerAction()));
        buttonPanel.add(new JButton(new PauseTimerAction()));
        buttonPanel.add(new JButton(new ResetTimerAction()));

        actionPanel.add(buttonPanel, BorderLayout.WEST);
    }

    // Represents action to be taken when user wants to add a new task
    public class AddTaskAction extends AbstractAction {

        AddTaskAction() {
            super("Add Task");
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            String result = (String) JOptionPane.showInputDialog(
                    frame, "Add a Task", "",
                    JOptionPane.PLAIN_MESSAGE, null, null, "");
            if (result != null && result.length() > 0) {
                tasks.addTask(new Task(result), 0);
                JOptionPane.showMessageDialog(null, "Successfully added task: " + result,
                        "Success", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(null, "No task name was entered", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }


    // Represents action to be taken when user wants to remove a task
    public class RemoveTaskAction extends AbstractAction {

        RemoveTaskAction() {
            super("Remove a Task");
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            String result = (String) JOptionPane.showInputDialog(
                    frame, "Select the number of the task you'd like to remove", "",
                    JOptionPane.PLAIN_MESSAGE, null, null, "");
            if (result != null && result.length() > 0) {
                try {
                    String removed = tasks.taskAt(Integer.parseInt(result) - 1).getTitle();
                    tasks.removeTask(Integer.parseInt(result) - 1, 0);
                    JOptionPane.showMessageDialog(null, "Successfully removed task: " + removed,
                            "Success", JOptionPane.INFORMATION_MESSAGE);
                } catch (NumberFormatException numError) {
                    JOptionPane.showMessageDialog(null,
                            "Invalid input: not a number", "Error", JOptionPane.ERROR_MESSAGE);
                } catch (IndexOutOfBoundsException outOfBoundsException) {
                    JOptionPane.showMessageDialog(null,
                            "Invalid input: The specified task does not exist!", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } else {
                JOptionPane.showMessageDialog(null,
                        "No task name was entered", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    // Represents action to be taken when user wants to complete a task
    public class CompleteTaskAction extends AbstractAction {

        CompleteTaskAction() {
            super("Mark a task as complete");
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            String result = (String) JOptionPane.showInputDialog(
                    frame, "Select the number of the task you'd like to complete", "",
                    JOptionPane.PLAIN_MESSAGE, null, null, "");
            if (result != null && result.length() > 0) {
                try {
                    String removed = tasks.taskAt(Integer.parseInt(result) - 1).getTitle();
                    compTasks.addTask(new Task(tasks.taskAt(Integer.parseInt(result) - 1).getTitle()), 1);
                    tasks.removeTask(Integer.parseInt(result) - 1, 1);
                    JOptionPane.showMessageDialog(null, "Successfully completed task: " + removed,
                            "Success", JOptionPane.INFORMATION_MESSAGE);
                } catch (NumberFormatException numError) {
                    JOptionPane.showMessageDialog(null,
                            "Invalid input: not a number", "Error", JOptionPane.ERROR_MESSAGE);
                } catch (IndexOutOfBoundsException outOfBoundsException) {
                    JOptionPane.showMessageDialog(null,
                            "Invalid input: The specified task does not exist!", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } else {
                JOptionPane.showMessageDialog(null,
                        "No number was entered", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    // Represents action to be taken when user wants to complete a task
    public class ClearTaskListAction extends AbstractAction {

        ClearTaskListAction() {
            super("Clear task list");
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            tasks.clearTaskList();
            JOptionPane.showMessageDialog(null, "Cleared task list!",
                    "Success", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    // Represents action to be taken when user wants to start a timer
    public class StartTimerAction extends AbstractAction {

        StartTimerAction() {
            super("Start a new timer");
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            String result = (String) JOptionPane.showInputDialog(
                    frame, "Timer Duration (minutes): ", "",
                    JOptionPane.PLAIN_MESSAGE, null, null, "");
            if (isActive) {
                JOptionPane.showMessageDialog(null, "There is already a timer active.",
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
            if (result != null && result.length() > 0) {
                try {
                    studyDur = Integer.parseInt(result) * 60;
                    totalStudyTime += studyDur;
                    setTimer();
                    isActive = true;
                } catch (NumberFormatException numError) {
                    JOptionPane.showMessageDialog(null,
                            "Invalid input: not an integer", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } else {
                JOptionPane.showMessageDialog(null, "There was no input", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    // Represents action to be taken when user wants to pause the timer
    public class PauseTimerAction extends AbstractAction {

        PauseTimerAction() {
            super("Pause Timer");
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            if (!isActive) {
                JOptionPane.showMessageDialog(null, "There is no active timer.",
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
            pauseOrResume();
            this.putValue(Action.NAME, isPaused ? "Resume Timer" : "Pause Timer");
            revalidate();
            repaint();
        }
    }

    // Represents action to be taken when user wants to reset the timer
    public class ResetTimerAction extends AbstractAction {

        ResetTimerAction() {
            super("Reset Timer");
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            if (!isActive) {
                JOptionPane.showMessageDialog(null, "There is no active timer.",
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
            isPaused = false;
            isActive = false;
            totalStudyTime -= studyDur;
            studyDur = 0;
            revalidate();
            repaint();
        }
    }

    // Represents action to be taken when user wants to view their tasks
    public class ViewTasksAction extends AbstractAction {
        ViewTasksAction() {
            super("View Tasks");
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            taskViewSettings();
            setContentPane(taskView);
            revalidate();
            repaint();
        }
    }

    // Represents action to be taken when user wants to view their completed tasks
    public class ViewCompTasksAction extends AbstractAction {
        ViewCompTasksAction() {
            super("View Completed Tasks");
        }

        @Override
        public void actionPerformed(ActionEvent e) {

            compTaskViewSettings();
            setContentPane(compTaskView);
            revalidate();
            repaint();
        }
    }

    // Represents action to be taken when user wants to return to the main interface
    public class ReturnToDesktopAction extends AbstractAction {
        ReturnToDesktopAction() {
            super("Return to Home Screen");
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            setContentPane(desktop);
        }
    }

    // Represents action to be taken when user wants to save tasks to file
    public class SaveTasksAction extends AbstractAction {

        SaveTasksAction() {
            super("Save Task List");
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            saveTaskList(tasks, jsonTaskWriter);
        }
    }

    // Represents action to be taken when user wants to load their tasks
    public class LoadTasksAction extends AbstractAction {

        LoadTasksAction() {
            super("Load Task List");
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            loadTaskList();
        }
    }

    // Represents action to be taken when user wants to save their completed tasks
    public class SaveCompTasksAction extends AbstractAction {

        SaveCompTasksAction() {
            super("Save list of Completed Tasks");
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            saveTaskList(compTasks, jsonTaskWriter);
        }
    }

    // Represents action to be taken when user wants to load their completed tasks
    public class LoadCompTasksAction extends AbstractAction {

        LoadCompTasksAction() {
            super("Load list of Completed Tasks");
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            loadCompTaskList();
        }
    }

    // Represents action to be taken when user wants to log total time studied
    public class SaveSessionStudyTime extends AbstractAction {

        SaveSessionStudyTime() {
            super("Log total time studied");
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            saveSessionStudyTime();
        }
    }

    // Represents action to be taken when user wants to load previous session study time
    public class LoadSessionStudyTime extends AbstractAction {

        LoadSessionStudyTime() {
            super("Load last session's total study time");
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            loadStudySessionTime();
        }
    }

    // MODIFIES: studyDur
    // EFFECTS: initializes a timer that counts down until studyDur hits 0
    public void setTimer() {

        timer = new Timer();

        timer.scheduleAtFixedRate(new TimerTask() {
            public void run() {
                if (isPaused) {
                    timer.cancel();
                }
                if (studyDur <= 0) {
                    timer.cancel();
                    isActive = false;
                    studyDur = 0;
                    JOptionPane.showMessageDialog(null,
                            "Times up!", "Take a break.", JOptionPane.INFORMATION_MESSAGE);
                }
                timerLabel.setText(displayTime(studyDur));
                studyDur--;
            }
        }, 0, 1000);
    }

    // MODIFIES: isPaused
    // EFFECTS: either pauses or resumes a timer depending on the status of isPaused
    private void pauseOrResume() {
        if (!isPaused) {
            isPaused = true;
        } else {
            setTimer();
            isPaused = false;
        }
    }

    // EFFECTS: converts a given amount of time (in seconds) to a string format similar to a digital time display
    public static String displayTime(int timeLeft) {

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
        return minutes + ":" + seconds;
    }

    // centers main application window on desktop
    private void centreOnScreen() {
        int width = Toolkit.getDefaultToolkit().getScreenSize().width;
        int height = Toolkit.getDefaultToolkit().getScreenSize().height;
        setLocation((width - getWidth()) / 2, (height - getHeight()) / 2);
    }

    // Represents action to be taken when user clicks desktop
    private class DesktopFocusAction extends MouseAdapter {
        @Override
        public void mouseClicked(MouseEvent e) {
            PomoFrame.this.requestFocusInWindow();
        }
    }

    // print contents of the EventLog to the console
    public void printLog(EventLog el) {
        for (Event e : el) {
            System.out.println(e);
        }
    }

    // MODIFIES: this
    // EFFECTS: initializes json writers and readers
    public void inJson() {
        jsonTaskWriter = new JsonWriter(TL_JSON_STORE);
        jsonTaskReader = new JsonReader(TL_JSON_STORE);
        jsonCompTaskWriter = new JsonWriter(CTL_JSON_STORE);
        jsonCompTaskReader = new JsonReader(CTL_JSON_STORE);
        jsonTimeWriter = new JsonWriter(ST_JSON_STORE);
        jsonTimeReader = new JsonReader(ST_JSON_STORE);
    }

    // EFFECTS: saves the task list to file
    private void saveTaskList(TaskList tl, JsonWriter jr) {
        try {
            jr.open();
            jr.write(tl);
            jr.close();
            JOptionPane.showMessageDialog(null, "Saved tasks to: " + ST_JSON_STORE,
                    "Success!", JOptionPane.INFORMATION_MESSAGE);
        } catch (FileNotFoundException e) {
            JOptionPane.showMessageDialog(null, "Unable to write to file: " + ST_JSON_STORE,
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // EFFECTS: saves the task list to file
    private void saveSessionStudyTime() {
        try {
            jsonTimeWriter.open();
            jsonTimeWriter.write(totalStudyTime);
            jsonTimeWriter.close();
            JOptionPane.showMessageDialog(null, "Logged session study time to file: " + ST_JSON_STORE,
                    "Success!", JOptionPane.INFORMATION_MESSAGE);
        } catch (FileNotFoundException e) {
            JOptionPane.showMessageDialog(null, "Unable to write to file: " + ST_JSON_STORE,
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // MODIFIES: this
    // EFFECTS: loads task list from file
    private void loadTaskList() {
        try {
            tasks = jsonTaskReader.read();
            JOptionPane.showMessageDialog(null, "Loaded tasks from: " + ST_JSON_STORE,
                    "Success!", JOptionPane.INFORMATION_MESSAGE);
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Unable to write to file: " + ST_JSON_STORE,
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // MODIFIES: this
    // EFFECTS: loads task list from file
    private void loadCompTaskList() {
        try {
            compTasks = jsonCompTaskReader.read();
            JOptionPane.showMessageDialog(null, "Loaded tasks from: " + ST_JSON_STORE,
                    "Success!", JOptionPane.INFORMATION_MESSAGE);
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Unable to write to file: " + ST_JSON_STORE,
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // MODIFIES: this
    // EFFECTS: loads task list from file
    private void loadStudySessionTime() {
        try {
            previousSessionTime = jsonTimeReader.readInt();
            JOptionPane.showMessageDialog(null, "Loaded previous session study time from :" + ST_JSON_STORE,
                    "Success!", JOptionPane.INFORMATION_MESSAGE);
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Unable to write to file: " + ST_JSON_STORE,
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

}
