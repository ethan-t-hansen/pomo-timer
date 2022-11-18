package ui;

import java.io.FileNotFoundException;

/* Main class, runs the PomoApp() */

public class Main {

    public static void main(String[] args) {
        try {
//            new PomoApp();
            new PomoFrame();
        } catch (FileNotFoundException e) {
            System.out.println("Unable to run application: file not found");
        }
    }

}
