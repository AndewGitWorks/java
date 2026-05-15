package ui;

import javax.swing.*;

public class AdminDashboard extends JFrame {

    public AdminDashboard() {
        setTitle("Admin");
        setSize(400, 300);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        add(new JLabel("Admin panel"));

        setVisible(true);
    }
}