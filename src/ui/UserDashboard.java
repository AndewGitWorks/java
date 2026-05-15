package ui;

import models.User;
import services.CashService;

import javax.swing.*;
import java.awt.*;

public class UserDashboard extends JFrame {

    public UserDashboard(User user) {
        setTitle("User Dashboard");
        setSize(500, 400);
        setLocationRelativeTo(null);

        JLabel balanceLabel = new JLabel("Баланс: " + user.getBalance());

        JButton withdrawBtn = new JButton("Снять");
        JButton depositBtn = new JButton("Пополнить");

        CashService service = new CashService();

        withdrawBtn.addActionListener(e -> {
            String input = JOptionPane.showInputDialog("Введите сумму");

            double newBalance = service.withdraw(
                    Double.parseDouble(input),
                    user.getBalance()
            );

            user.setBalance(newBalance);
            balanceLabel.setText("Баланс: " + newBalance);
        });

        setLayout(new FlowLayout());

        add(balanceLabel);
        add(withdrawBtn);
        add(depositBtn);

        setVisible(true);
    }
}