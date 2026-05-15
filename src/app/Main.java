package app;

import database.DatabaseInitializer;
import ui.LoginFrame;

public class Main {
    public static void main(String[] args) {
        DatabaseInitializer.init();
        new LoginFrame();
    }
}