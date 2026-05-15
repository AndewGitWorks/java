package ui;

import services.AuthService;
import models.User;

import javax.swing.*;
import java.awt.*;

public class LoginFrame extends JFrame {

    public LoginFrame() {
        setTitle("ATM");
        setSize(400, 300);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel panel = new JPanel(new GridLayout(3, 2, 10, 10));

        JTextField cardField = new JTextField();
        JPasswordField pinField = new JPasswordField();
        JButton loginBtn = new JButton("Login");

        panel.add(new JLabel("Card Number"));
        panel.add(cardField);
        panel.add(new JLabel("PIN"));
        panel.add(pinField);
        panel.add(new JLabel(""));
        panel.add(loginBtn);

        add(panel);

        loginBtn.addActionListener(e -> {
            AuthService auth = new AuthService();

            User user = auth.login(
                    cardField.getText(),
                    new String(pinField.getPassword())
            );

            if (user != null) {
                new UserDashboard(user);
                dispose();
            } else {
                JOptionPane.showMessageDialog(this, "Неверные данные");
            }
        });

        setVisible(true);
    }
}