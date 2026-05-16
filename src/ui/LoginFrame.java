package ui;

import services.AuthService;
import models.User;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class LoginFrame extends JFrame {

    public LoginFrame() {
        setTitle("Банкомат - Вход");
        setSize(500, 400);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        // Главная панель
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(new Color(20, 25, 35));

        // Заголовок
        JPanel headerPanel = new JPanel();
        headerPanel.setBackground(new Color(35, 45, 60));
        headerPanel.setPreferredSize(new Dimension(0, 80));

        JLabel titleLabel = new JLabel("Банкомат");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 28));
        titleLabel.setForeground(new Color(100, 200, 255));
        headerPanel.add(titleLabel);

        mainPanel.add(headerPanel, BorderLayout.NORTH);

        // Форма входа
        JPanel formPanel = new JPanel(new GridLayout(4, 2, 15, 15));
        formPanel.setBorder(new EmptyBorder(40, 40, 40, 40));
        formPanel.setBackground(new Color(20, 25, 35));

        JLabel cardLabel = new JLabel("Номер карты:");
        cardLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        cardLabel.setForeground(new Color(200, 200, 200));
        JTextField cardField = new JTextField();
        cardField.setFont(new Font("Arial", Font.PLAIN, 14));
        cardField.setPreferredSize(new Dimension(0, 35));
        cardField.setBackground(new Color(40, 50, 65));
        cardField.setForeground(Color.WHITE);
        cardField.setCaretColor(Color.WHITE);
        cardField.setBorder(BorderFactory.createLineBorder(new Color(60, 80, 100), 1));

        JLabel pinLabel = new JLabel("PIN код:");
        pinLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        pinLabel.setForeground(new Color(200, 200, 200));
        JPasswordField pinField = new JPasswordField();
        pinField.setFont(new Font("Arial", Font.PLAIN, 14));
        pinField.setPreferredSize(new Dimension(0, 35));
        pinField.setBackground(new Color(40, 50, 65));
        pinField.setForeground(Color.WHITE);
        pinField.setCaretColor(Color.WHITE);
        pinField.setBorder(BorderFactory.createLineBorder(new Color(60, 80, 100), 1));

        formPanel.add(cardLabel);
        formPanel.add(cardField);
        formPanel.add(pinLabel);
        formPanel.add(pinField);

        // Кнопка входа
        JButton loginBtn = new JButton("ВХОД");
        loginBtn.setFont(new Font("Arial", Font.BOLD, 16));
        loginBtn.setBackground(new Color(70, 130, 180));
        loginBtn.setForeground(Color.WHITE);
        loginBtn.setFocusPainted(false);
        loginBtn.setPreferredSize(new Dimension(0, 45));
        loginBtn.setBorder(BorderFactory.createLineBorder(new Color(100, 160, 220), 1));

        loginBtn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                loginBtn.setBackground(new Color(100, 160, 220));
                loginBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                loginBtn.setBackground(new Color(70, 130, 180));
            }
        });

        // Кнопка админ входа
        JButton adminBtn = new JButton("АДМИН");
        adminBtn.setFont(new Font("Arial", Font.BOLD, 16));
        adminBtn.setBackground(new Color(100, 100, 50));
        adminBtn.setForeground(Color.WHITE);
        adminBtn.setFocusPainted(false);
        adminBtn.setPreferredSize(new Dimension(0, 45));
        adminBtn.setBorder(BorderFactory.createLineBorder(new Color(150, 150, 100), 1));

        adminBtn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                adminBtn.setBackground(new Color(150, 150, 100));
                adminBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                adminBtn.setBackground(new Color(100, 100, 50));
            }
        });

        formPanel.add(loginBtn);
        formPanel.add(adminBtn);

        mainPanel.add(formPanel, BorderLayout.CENTER);

        // Подсказка
        JPanel bottomPanel = new JPanel();
        bottomPanel.setBackground(new Color(20, 25, 35));
        JLabel hintLabel = new JLabel("Тестовая карта: 1234567890123456, PIN: 1234");
        hintLabel.setFont(new Font("Arial", Font.ITALIC, 12));
        hintLabel.setForeground(new Color(120, 130, 150));
        bottomPanel.add(hintLabel);

        mainPanel.add(bottomPanel, BorderLayout.SOUTH);

        add(mainPanel);

        loginBtn.addActionListener(e -> {
            String card = cardField.getText().trim();
            String pin = new String(pinField.getPassword());

            if (card.isEmpty() || pin.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Пожалуйста, заполните все поля", 
                    "Ошибка ввода", JOptionPane.ERROR_MESSAGE);
                return;
            }

            AuthService auth = new AuthService();
            User user = auth.login(card, pin);

            if (user != null) {
                new UserDashboard(user);
                dispose();
            } else {
                JOptionPane.showMessageDialog(this, "❌ Неверный номер карты или PIN код", 
                    "Ошибка входа", JOptionPane.ERROR_MESSAGE);
                pinField.setText("");
            }
        });

        adminBtn.addActionListener(e -> {
            String adminUsername = JOptionPane.showInputDialog(this, "Введите имя пользователя администратора:");
            if (adminUsername == null) return;

            String adminPassword = JOptionPane.showInputDialog(this, "Введите пароль администратора:");
            if (adminPassword == null) return;

            if ("admin".equals(adminUsername) && "admin".equals(adminPassword)) {
                new AdminDashboard();
                dispose();
            } else {
                JOptionPane.showMessageDialog(this, "❌ Неверные учетные данные администратора", 
                    "Ошибка входа", JOptionPane.ERROR_MESSAGE);
            }
        });

        setVisible(true);
    }
}