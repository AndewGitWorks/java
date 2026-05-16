package ui;

import dao.ATMBalanceDAO;
import dao.TransactionDAO;
import dao.UserDAO;
import models.Transaction;
import models.User;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class AdminDashboard extends JFrame {
    private TransactionDAO transactionDAO;
    private UserDAO userDAO;
    private ATMBalanceDAO atmBalanceDAO;
    private JTable transactionsTable;
    private DefaultTableModel tableModel;

    public AdminDashboard() {
        setTitle("🔐 Администраторская панель");
        setSize(1000, 600);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(true);

        this.transactionDAO = new TransactionDAO();
        this.userDAO = new UserDAO();
        this.atmBalanceDAO = new ATMBalanceDAO();

        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBackground(new Color(20, 25, 35));
        mainPanel.setBorder(new EmptyBorder(15, 15, 15, 15));

        // Заголовок
        JPanel headerPanel = createHeaderPanel();
        mainPanel.add(headerPanel, BorderLayout.NORTH);

        // Панель фильтров
        JPanel filterPanel = createFilterPanel();
        mainPanel.add(filterPanel, BorderLayout.CENTER);

        // Таблица транзакций
        JPanel tablePanel = createTablePanel();
        mainPanel.add(tablePanel, BorderLayout.SOUTH);

        add(mainPanel);
        loadAllTransactions();
        setVisible(true);
    }

    private JPanel createHeaderPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 10));
        panel.setBackground(new Color(35, 45, 60));

        JLabel titleLabel = new JLabel("🏦 Администраторская панель");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 22));
        titleLabel.setForeground(new Color(100, 200, 255));

        double atmBalance = atmBalanceDAO.getBalance();
        JLabel balanceLabel = new JLabel("💰 Баланс банкомата: " + String.format("%.2f", atmBalance) + " ₽");
        balanceLabel.setFont(new Font("Arial", Font.BOLD, 14));
        balanceLabel.setForeground(new Color(100, 200, 100));

        JButton exitBtn = new JButton("Выход");
        exitBtn.setFont(new Font("Arial", Font.BOLD, 12));
        exitBtn.setBackground(new Color(180, 60, 60));
        exitBtn.setForeground(Color.WHITE);
        exitBtn.setFocusPainted(false);
        exitBtn.setPreferredSize(new Dimension(100, 30));
        exitBtn.setOpaque(true);
        exitBtn.setContentAreaFilled(true);
        exitBtn.setBorder(BorderFactory.createRaisedBevelBorder());

        exitBtn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                exitBtn.setBackground(new Color(220, 100, 100));
                exitBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                exitBtn.setBackground(new Color(180, 60, 60));
            }
        });

        exitBtn.addActionListener(e -> {
            new LoginFrame();
            dispose();
        });

        panel.add(titleLabel);
        panel.add(Box.createHorizontalStrut(30));
        panel.add(balanceLabel);
        panel.add(Box.createHorizontalGlue());
        panel.add(exitBtn);

        return panel;
    }

    private JPanel createFilterPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 10));
        panel.setBackground(new Color(20, 25, 35));

        JLabel filterLabel = new JLabel("Фильтры:");
        filterLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        filterLabel.setForeground(new Color(200, 200, 200));

        JButton allTransBtn = new JButton("Все операции");
        allTransBtn.setFont(new Font("Arial", Font.BOLD, 12));
        allTransBtn.setBackground(new Color(70, 120, 180));
        allTransBtn.setForeground(Color.WHITE);
        allTransBtn.setPreferredSize(new Dimension(140, 35));
        allTransBtn.setFocusPainted(false);
        allTransBtn.setOpaque(true);
        allTransBtn.setContentAreaFilled(true);
        allTransBtn.setBorder(BorderFactory.createRaisedBevelBorder());

        allTransBtn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                allTransBtn.setBackground(new Color(100, 150, 220));
                allTransBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                allTransBtn.setBackground(new Color(70, 120, 180));
            }
        });

        allTransBtn.addActionListener(e -> loadAllTransactions());

        JButton userTransBtn = new JButton("По пользователю");
        userTransBtn.setFont(new Font("Arial", Font.BOLD, 12));
        userTransBtn.setBackground(new Color(70, 120, 180));
        userTransBtn.setForeground(Color.WHITE);
        userTransBtn.setPreferredSize(new Dimension(140, 35));
        userTransBtn.setFocusPainted(false);
        userTransBtn.setOpaque(true);
        userTransBtn.setContentAreaFilled(true);
        userTransBtn.setBorder(BorderFactory.createRaisedBevelBorder());

        userTransBtn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                userTransBtn.setBackground(new Color(100, 150, 220));
                userTransBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                userTransBtn.setBackground(new Color(70, 120, 180));
            }
        });

        userTransBtn.addActionListener(e -> filterByUser());

        JButton typeTransBtn = new JButton("По типу операции");
        typeTransBtn.setFont(new Font("Arial", Font.BOLD, 12));
        typeTransBtn.setBackground(new Color(70, 120, 180));
        typeTransBtn.setForeground(Color.WHITE);
        typeTransBtn.setPreferredSize(new Dimension(140, 35));
        typeTransBtn.setFocusPainted(false);
        typeTransBtn.setOpaque(true);
        typeTransBtn.setContentAreaFilled(true);
        typeTransBtn.setBorder(BorderFactory.createRaisedBevelBorder());

        typeTransBtn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                typeTransBtn.setBackground(new Color(100, 150, 220));
                typeTransBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                typeTransBtn.setBackground(new Color(70, 120, 180));
            }
        });

        typeTransBtn.addActionListener(e -> filterByType());

        panel.add(filterLabel);
        panel.add(allTransBtn);
        panel.add(userTransBtn);
        panel.add(typeTransBtn);

        return panel;
    }

    private JPanel createTablePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(20, 25, 35));

        String[] columns = {"ID", "Пользователь", "Тип операции", "Сумма (₽)", "Дата"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        transactionsTable = new JTable(tableModel);
        transactionsTable.setBackground(new Color(40, 50, 65));
        transactionsTable.setForeground(new Color(200, 200, 200));
        transactionsTable.setGridColor(new Color(60, 80, 100));
        transactionsTable.setFont(new Font("Arial", Font.PLAIN, 12));
        transactionsTable.setRowHeight(25);

        JScrollPane scrollPane = new JScrollPane(transactionsTable);
        scrollPane.setBackground(new Color(20, 25, 35));
        scrollPane.getViewport().setBackground(new Color(40, 50, 65));

        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    private void loadAllTransactions() {
        tableModel.setRowCount(0);
        List<Transaction> transactions = transactionDAO.getAllTransactions();

        for (Transaction t : transactions) {
            User user = userDAO.getUserById(t.getUserId());
            String userName = user != null ? user.getCardNumber().substring(Math.max(0, user.getCardNumber().length() - 4)) : "Unknown";

            tableModel.addRow(new Object[]{
                    t.getId(),
                    userName,
                    t.getType(),
                    String.format("%.2f", t.getAmount()),
                    t.getCreatedAt()
            });
        }
    }

    private void filterByUser() {
        String userId = JOptionPane.showInputDialog(this, "Введите ID пользователя:");
        if (userId == null || userId.isEmpty()) return;

        try {
            int id = Integer.parseInt(userId);
            tableModel.setRowCount(0);
            List<Transaction> transactions = transactionDAO.getUserTransactions(id);

            User user = userDAO.getUserById(id);
            if (user == null) {
                JOptionPane.showMessageDialog(this, "❌ Пользователь не найден", "Ошибка", JOptionPane.ERROR_MESSAGE);
                return;
            }

            for (Transaction t : transactions) {
                tableModel.addRow(new Object[]{
                        t.getId(),
                        user.getCardNumber().substring(Math.max(0, user.getCardNumber().length() - 4)),
                        t.getType(),
                        String.format("%.2f", t.getAmount()),
                        t.getCreatedAt()
                });
            }

            if (transactions.isEmpty()) {
                JOptionPane.showMessageDialog(this, "ℹ️ Операций не найдено", "Информация", JOptionPane.INFORMATION_MESSAGE);
            }

        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "❌ Введите корректный ID", "Ошибка", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void filterByType() {
        String[] types = {"WITHDRAW", "DEPOSIT", "PHONE_PAYMENT", "UTILITIES_PAYMENT", "TRANSFER", "PENALTY_PAYMENT"};
        String selectedType = (String) JOptionPane.showInputDialog(
                this,
                "Выберите тип операции:",
                "Фильтр по типу",
                JOptionPane.QUESTION_MESSAGE,
                null,
                types,
                types[0]
        );

        if (selectedType == null) return;

        tableModel.setRowCount(0);
        List<Transaction> transactions = transactionDAO.getTransactionsByType(selectedType);

        for (Transaction t : transactions) {
            User user = userDAO.getUserById(t.getUserId());
            String userName = user != null ? user.getCardNumber().substring(Math.max(0, user.getCardNumber().length() - 4)) : "Unknown";

            tableModel.addRow(new Object[]{
                    t.getId(),
                    userName,
                    t.getType(),
                    String.format("%.2f", t.getAmount()),
                    t.getCreatedAt()
            });
        }

        if (transactions.isEmpty()) {
            JOptionPane.showMessageDialog(this, "ℹ️ Операций не найдено", "Информация", JOptionPane.INFORMATION_MESSAGE);
        }
    }
}