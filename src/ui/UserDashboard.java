package ui;

import models.User;
import services.ATMService;
import services.BenchmarkService;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class UserDashboard extends JFrame {
    private User user;
    private JLabel balanceLabel;
    private ATMService atmService;
    private final BenchmarkService benchmarkService = new BenchmarkService();

    public UserDashboard(User user) {
        this.user = user;
        this.atmService = new ATMService();

        setTitle("Личный кабинет");
        setSize(700, 600);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout(10, 10));
        mainPanel.setBackground(new Color(20, 25, 35));
        mainPanel.setBorder(new EmptyBorder(20, 20, 20, 20));

        // Верхняя панель с информацией
        JPanel headerPanel = createHeaderPanel();
        mainPanel.add(headerPanel, BorderLayout.NORTH);

        // Центральная панель с кнопками операций
        JPanel operationsPanel = createOperationsPanel();
        JScrollPane scrollPane = new JScrollPane(operationsPanel);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(60, 80, 100), 1));
        scrollPane.setBackground(new Color(20, 25, 35));
        scrollPane.getViewport().setBackground(new Color(20, 25, 35));
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        // Нижняя панель с выходом
        JPanel bottomPanel = createBottomPanel();
        mainPanel.add(bottomPanel, BorderLayout.SOUTH);

        add(mainPanel);
        setVisible(true);
    }

    private JPanel createHeaderPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 10));
        panel.setBackground(new Color(35, 45, 60));

        JLabel userLabel = new JLabel("Привет, " + user.getCardNumber().substring(user.getCardNumber().length() - 4));
        userLabel.setFont(new Font("Arial", Font.PLAIN, 16));
        userLabel.setForeground(new Color(200, 200, 200));

        balanceLabel = new JLabel("Баланс: " + String.format("%.2f", user.getBalance()) + " ₽");
        balanceLabel.setFont(new Font("Arial", Font.BOLD, 20));
        balanceLabel.setForeground(new Color(100, 200, 255));

        panel.add(userLabel);
        panel.add(Box.createHorizontalStrut(50));
        panel.add(balanceLabel);

        return panel;
    }

    private JPanel createOperationsPanel() {
        JPanel panel = new JPanel(new GridLayout(5, 2, 15, 15));
        panel.setBackground(new Color(20, 25, 35));
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));

        panel.add(createServiceButton("Снять наличные", "withdraw"));
        panel.add(createServiceButton("Пополнить счет", "deposit"));
        panel.add(createServiceButton("Оплата телефона", "phone"));
        panel.add(createServiceButton("Оплата ЖКХ", "utilities"));
        panel.add(createServiceButton("Перевод на карту", "card_transfer"));
        panel.add(createServiceButton("Перевод пользователю", "user_transfer"));
        panel.add(createServiceButton("Оплата штрафов", "penalty"));
        panel.add(createServiceButton("Бенчмарк производительности", "benchmark"));
        panel.add(createServiceButton("История операций", "history"));

        return panel;
    }

    private JButton createServiceButton(String text, String action) {
        JButton button = new JButton(text);
        button.setFont(new Font("Arial", Font.PLAIN, 14));
        button.setPreferredSize(new Dimension(280, 60));
        button.setBackground(new Color(50, 80, 120));
        button.setForeground(Color.WHITE);
        button.setBorder(BorderFactory.createRaisedBevelBorder());
        button.setFocusPainted(false);

        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(new Color(80, 120, 180));
                button.setCursor(new Cursor(Cursor.HAND_CURSOR));
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(new Color(50, 80, 120));
            }
        });

        button.addActionListener(e -> handleOperation(action));

        return button;
    }

    private void handleOperation(String action) {
        try {
            switch (action) {
                case "withdraw" -> handleWithdraw();
                case "deposit" -> handleDeposit();
                case "phone" -> handlePhonePayment();
                case "utilities" -> handleUtilitiesPayment();
                case "card_transfer" -> handleCardTransfer();
                case "user_transfer" -> handleUserTransfer();
                case "penalty" -> handlePenaltyPayment();
                case "benchmark" -> handleBenchmark();
                case "history" -> showHistory();
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Ошибка: " + ex.getMessage(), "Ошибка", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void handleWithdraw() {
        String input = JOptionPane.showInputDialog(this, "Введите сумму для снятия:");
        if (input != null && !input.isEmpty()) {
            try {
                double amount = Double.parseDouble(input);
                setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
                atmService.withdrawAsync(user, amount).whenComplete((unused, ex) -> {
                    SwingUtilities.invokeLater(() -> {
                        setCursor(Cursor.getDefaultCursor());
                        if (ex != null) {
                            JOptionPane.showMessageDialog(this, ex.getCause().getMessage(), "Ошибка", JOptionPane.ERROR_MESSAGE);
                        } else {
                            updateBalance();
                            JOptionPane.showMessageDialog(this, "Снятие выполнено успешно!", "Успех", JOptionPane.INFORMATION_MESSAGE);
                        }
                    });
                });
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(), "Ошибка", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void handleDeposit() {
        String input = JOptionPane.showInputDialog(this, "Введите сумму для пополнения:");
        if (input != null && !input.isEmpty()) {
            try {
                double amount = Double.parseDouble(input);
                setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
                atmService.depositAsync(user, amount).whenComplete((unused, ex) -> {
                    SwingUtilities.invokeLater(() -> {
                        setCursor(Cursor.getDefaultCursor());
                        if (ex != null) {
                            JOptionPane.showMessageDialog(this, ex.getCause().getMessage(), "Ошибка", JOptionPane.ERROR_MESSAGE);
                        } else {
                            updateBalance();
                            JOptionPane.showMessageDialog(this, "Пополнение выполнено успешно!", "Успех", JOptionPane.INFORMATION_MESSAGE);
                        }
                    });
                });
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(), "Ошибка", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void handlePhonePayment() {
        JPanel panel = new JPanel(new GridLayout(2, 2, 10, 10));
        JTextField phoneField = new JTextField();
        JTextField amountField = new JTextField();

        panel.add(new JLabel("Номер телефона:"));
        panel.add(phoneField);
        panel.add(new JLabel("Сумма (₽):"));
        panel.add(amountField);

        int result = JOptionPane.showConfirmDialog(this, panel, "Оплата телефона", JOptionPane.OK_CANCEL_OPTION);
        if (result == JOptionPane.OK_OPTION) {
            try {
                String phone = phoneField.getText();
                double amount = Double.parseDouble(amountField.getText());
                setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
                atmService.payPhoneAsync(user, phone, amount).whenComplete((unused, ex) -> {
                    SwingUtilities.invokeLater(() -> {
                        setCursor(Cursor.getDefaultCursor());
                        if (ex != null) {
                            JOptionPane.showMessageDialog(this, ex.getCause().getMessage(), "Ошибка", JOptionPane.ERROR_MESSAGE);
                        } else {
                            updateBalance();
                            JOptionPane.showMessageDialog(this, "Платеж выполнен успешно!", "Успех", JOptionPane.INFORMATION_MESSAGE);
                        }
                    });
                });
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(), "Ошибка", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void handleUtilitiesPayment() {
        JPanel panel = new JPanel(new GridLayout(2, 2, 10, 10));
        JTextField accountField = new JTextField();
        JTextField amountField = new JTextField();

        panel.add(new JLabel("Номер счета ЖКХ:"));
        panel.add(accountField);
        panel.add(new JLabel("Сумма (₽):"));
        panel.add(amountField);

        int result = JOptionPane.showConfirmDialog(this, panel, "Оплата ЖКХ", JOptionPane.OK_CANCEL_OPTION);
        if (result == JOptionPane.OK_OPTION) {
            try {
                String account = accountField.getText();
                double amount = Double.parseDouble(amountField.getText());
                setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
                atmService.payUtilitiesAsync(user, account, amount).whenComplete((unused, ex) -> {
                    SwingUtilities.invokeLater(() -> {
                        setCursor(Cursor.getDefaultCursor());
                        if (ex != null) {
                            JOptionPane.showMessageDialog(this, ex.getCause().getMessage(), "Ошибка", JOptionPane.ERROR_MESSAGE);
                        } else {
                            updateBalance();
                            JOptionPane.showMessageDialog(this, "Платеж выполнен успешно!", "Успех", JOptionPane.INFORMATION_MESSAGE);
                        }
                    });
                });
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(), "Ошибка", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void handleCardTransfer() {
        JPanel panel = new JPanel(new GridLayout(2, 2, 10, 10));
        JTextField cardField = new JTextField();
        JTextField amountField = new JTextField();

        panel.add(new JLabel("Номер карты получателя:"));
        panel.add(cardField);
        panel.add(new JLabel("Сумма (₽):"));
        panel.add(amountField);

        int result = JOptionPane.showConfirmDialog(this, panel, "Перевод на карту", JOptionPane.OK_CANCEL_OPTION);
        if (result == JOptionPane.OK_OPTION) {
            try {
                String card = cardField.getText();
                double amount = Double.parseDouble(amountField.getText());
                setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
                atmService.transferToCardAsync(user, card, amount).whenComplete((unused, ex) -> {
                    SwingUtilities.invokeLater(() -> {
                        setCursor(Cursor.getDefaultCursor());
                        if (ex != null) {
                            JOptionPane.showMessageDialog(this, ex.getCause().getMessage(), "Ошибка", JOptionPane.ERROR_MESSAGE);
                        } else {
                            updateBalance();
                            JOptionPane.showMessageDialog(this, "Перевод выполнен успешно!", "Успех", JOptionPane.INFORMATION_MESSAGE);
                        }
                    });
                });
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(), "Ошибка", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void handleUserTransfer() {
        JPanel panel = new JPanel(new GridLayout(2, 2, 10, 10));
        JTextField userField = new JTextField();
        JTextField amountField = new JTextField();

        panel.add(new JLabel("Номер карты пользователя:"));
        panel.add(userField);
        panel.add(new JLabel("Сумма (₽):"));
        panel.add(amountField);

        int result = JOptionPane.showConfirmDialog(this, panel, "Перевод пользователю", JOptionPane.OK_CANCEL_OPTION);
        if (result == JOptionPane.OK_OPTION) {
            try {
                String userCard = userField.getText();
                double amount = Double.parseDouble(amountField.getText());
                setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
                atmService.transferToUserAsync(user, userCard, amount).whenComplete((unused, ex) -> {
                    SwingUtilities.invokeLater(() -> {
                        setCursor(Cursor.getDefaultCursor());
                        if (ex != null) {
                            JOptionPane.showMessageDialog(this, ex.getCause().getMessage(), "Ошибка", JOptionPane.ERROR_MESSAGE);
                        } else {
                            updateBalance();
                            JOptionPane.showMessageDialog(this, "Перевод выполнен успешно!", "Успех", JOptionPane.INFORMATION_MESSAGE);
                        }
                    });
                });
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(), "Ошибка", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void handlePenaltyPayment() {
        JPanel panel = new JPanel(new GridLayout(2, 2, 10, 10));
        JTextField penaltyField = new JTextField();
        JTextField amountField = new JTextField();

        panel.add(new JLabel("Номер постановления:"));
        panel.add(penaltyField);
        panel.add(new JLabel("Сумма (₽):"));
        panel.add(amountField);

        int result = JOptionPane.showConfirmDialog(this, panel, "Оплата штрафов", JOptionPane.OK_CANCEL_OPTION);
        if (result == JOptionPane.OK_OPTION) {
            try {
                String penalty = penaltyField.getText();
                double amount = Double.parseDouble(amountField.getText());
                setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
                atmService.payPenaltyAsync(user, penalty, amount).whenComplete((unused, ex) -> {
                    SwingUtilities.invokeLater(() -> {
                        setCursor(Cursor.getDefaultCursor());
                        if (ex != null) {
                            JOptionPane.showMessageDialog(this, ex.getCause().getMessage(), "Ошибка", JOptionPane.ERROR_MESSAGE);
                        } else {
                            updateBalance();
                            JOptionPane.showMessageDialog(this, "Платеж выполнен успешно!", "Успех", JOptionPane.INFORMATION_MESSAGE);
                        }
                    });
                });
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(), "Ошибка", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void showHistory() {
        JOptionPane.showMessageDialog(this, 
            "История операций сохраняется в базе данных.\nПосмотреть можно через админ панель.",
            "История", JOptionPane.INFORMATION_MESSAGE);
    }

    private void handleBenchmark() {
        JPanel panel = new JPanel(new GridLayout(2, 2, 10, 10));
        JTextField operationsField = new JTextField("50");
        JTextField threadsField = new JTextField(String.valueOf(Runtime.getRuntime().availableProcessors()));

        panel.add(new JLabel("Количество операций:"));
        panel.add(operationsField);
        panel.add(new JLabel("Потоков для многопоточного режима:"));
        panel.add(threadsField);

        int result = JOptionPane.showConfirmDialog(this, panel, "Бенчмарк вычислений", JOptionPane.OK_CANCEL_OPTION);
        if (result == JOptionPane.OK_OPTION) {
            try {
                int operations = Integer.parseInt(operationsField.getText());
                int threads = Integer.parseInt(threadsField.getText());
                setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));

                SwingWorker<BenchmarkService.BenchmarkResult, Void> worker = new SwingWorker<>() {
                    @Override
                    protected BenchmarkService.BenchmarkResult doInBackground() {
                        return benchmarkService.compareBenchmark(operations, threads);
                    }

                    @Override
                    protected void done() {
                        setCursor(Cursor.getDefaultCursor());
                        try {
                            BenchmarkService.BenchmarkResult result = get();
                            JOptionPane.showMessageDialog(UserDashboard.this,
                                    result.format(),
                                    "Результаты бенчмарка",
                                    JOptionPane.INFORMATION_MESSAGE);
                        } catch (Exception e) {
                            JOptionPane.showMessageDialog(UserDashboard.this,
                                    "Ошибка при выполнении бенчмарка: " + getRootErrorMessage(e),
                                    "Ошибка",
                                    JOptionPane.ERROR_MESSAGE);
                        }
                    }
                };

                worker.execute();
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Введите корректные числа", "Ошибка", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private String getRootErrorMessage(Throwable throwable) {
        Throwable root = throwable;
        while (root.getCause() != null) {
            root = root.getCause();
        }
        return root.getMessage() != null ? root.getMessage() : "Неизвестная ошибка";
    }

    private JPanel createBottomPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        panel.setBackground(new Color(20, 25, 35));

        JButton logoutBtn = new JButton("Выход");
        logoutBtn.setFont(new Font("Arial", Font.BOLD, 14));
        logoutBtn.setBackground(new Color(180, 50, 50));
        logoutBtn.setForeground(Color.WHITE);
        logoutBtn.setFocusPainted(false);
        logoutBtn.setPreferredSize(new Dimension(120, 40));
        logoutBtn.setBorder(BorderFactory.createRaisedBevelBorder());
        logoutBtn.setOpaque(true);
        logoutBtn.setContentAreaFilled(true);

        logoutBtn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                logoutBtn.setBackground(new Color(220, 80, 80));
                logoutBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                logoutBtn.setBackground(new Color(180, 50, 50));
            }
        });

        logoutBtn.addActionListener(e -> {
            new LoginFrame();
            dispose();
        });

        panel.add(logoutBtn);
        return panel;
    }

    private void updateBalance() {
        balanceLabel.setText("Баланс: " + String.format("%.2f", user.getBalance()) + " ₽");
    }
}