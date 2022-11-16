package ui;

import model.CompletedTaskList;
import model.Task;
import model.TaskList;
import persistence.JsonReader;
import persistence.JsonWriter;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.text.NumberFormat;
import java.util.Scanner;
import java.util.Timer;

public class PomoFrame extends JFrame implements ActionListener {

    private static final int WIDTH = 800;
    private static final int HEIGHT = 600;
    private static final String FILE_DESCRIPTOR = "...file";
    private static final String SCREEN_DESCRIPTOR = "...screen";
    private JComboBox<String> printCombo;
    private JDesktopPane desktop;
    private JFrame frame;
    private JPanel buttonPanel;

    private static final String TL_JSON_STORE = "./data/taskList.json";
    private static final String CTL_JSON_STORE = "./data/completedTaskList.json";
    private static final String ST_JSON_STORE = "./data/totalStudyTime.json";
    private Scanner input;
    private TaskList tasks;
    private TaskList completedTasks;
    private Timer timer;
    private JsonWriter jsonTaskWriter;
    private JsonReader jsonTaskReader;
    private JsonWriter jsonCompTaskWriter;
    private JsonReader jsonCompTaskReader;
    private JsonWriter jsonTimeWriter;
    private JsonReader jsonTimeReader;

    public PomoFrame() {

        tasks = new TaskList();
        completedTasks = new CompletedTaskList();

        frame = new JFrame("CPSC 210: Pomodoro");
        frame.setSize(WIDTH, HEIGHT);

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));

        buttonPanel = new JPanel();
        buttonPanel.setLayout(new GridLayout(4, 1));
        buttonPanel.setMaximumSize(new Dimension(150, 400));
        buttonPanel.setAlignmentX(0);
        addButtonPanel();

        mainPanel.add(buttonPanel);
        frame.setContentPane(mainPanel);

        frame.setSize(520,600);
        frame.setMinimumSize(new Dimension(520,600));
        frame.setVisible(true);
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setVisible(true);

    }

    // Helper to add buttons for adding tasks, completing tasks, etc.
    private void addButtonPanel() {
        buttonPanel.add(new JButton(new AddTaskAction()));
        buttonPanel.add(new JButton(new RemoveTaskAction()));
    }

    // Adds an inputted task to the tasklist
    public class AddTaskAction extends AbstractAction {

        AddTaskAction() {
            super("Add Task");
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            String result = (String) JOptionPane.showInputDialog(
                    frame,
                    "Add a Task",
                    "",
                    JOptionPane.PLAIN_MESSAGE,
                    null,
                    null,
                    ""
            );
            if (result != null && result.length() > 0) {
                tasks.addTask(new Task(result));
            } else {
                JOptionPane.showMessageDialog(null, "No task name was entered", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    // Adds an inputted task to the tasklist
    public class RemoveTaskAction extends AbstractAction {

        RemoveTaskAction() {
            super("Remove A Task");
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            String result = (String) JOptionPane.showInputDialog(
                    frame,
                    "Select the number of the task you'd like to remove",
                    "",
                    JOptionPane.PLAIN_MESSAGE,
                    null,
                    null,
                    ""
            );
            if (result != null && result.length() > 0) {
                try {
                    tasks.removeTask(Integer.parseInt(result));
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

    /**
     * Helper to create print options combo box
     * @return  the combo box
     */
    private JComboBox<String> createPrintCombo() {
        printCombo = new JComboBox<String>();
        printCombo.addItem(FILE_DESCRIPTOR);
        printCombo.addItem(SCREEN_DESCRIPTOR);
        return printCombo;
    }

    /**
     * Helper to centre main application window on desktop
     */
    private void centreOnScreen() {
        int width = Toolkit.getDefaultToolkit().getScreenSize().width;
        int height = Toolkit.getDefaultToolkit().getScreenSize().height;
        setLocation((width - getWidth()) / 2, (height - getHeight()) / 2);
    }

    /**
     * Represents action to be taken when user clicks desktop
     * to switch focus. (Needed for key handling.)
     */
    private class DesktopFocusAction extends MouseAdapter {
        @Override
        public void mouseClicked(MouseEvent e) {
            PomoFrame.this.requestFocusInWindow();
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {

    }

    // starts the application
    public static void main(String[] args) {
        new PomoFrame();
    }

}
