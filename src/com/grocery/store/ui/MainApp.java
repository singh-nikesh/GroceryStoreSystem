package com.grocery.store.ui;

import com.grocery.store.model.User;
import javax.swing.*;

public class MainApp extends JFrame {

    public MainApp(User user) {
        setTitle("Grocery Store System - " + user.getUsername());
        setSize(1000, 700); // Increased window size
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        initializeUI(user);
    }

    private void initializeUI(User user) {
        JTabbedPane tabbedPane = new JTabbedPane();

        // Common tabs for all users
        tabbedPane.addTab("Products", new ProductPanel());
        tabbedPane.addTab("Sales", new SalesPanel());
        tabbedPane.addTab("Sales History", new SalesHistoryPanel());

        // Admin-only features
        if ("admin".equalsIgnoreCase(user.getRole())) {
            tabbedPane.addTab("Users", new UserPanel());
            // Add other admin-specific panels here if needed
        }

        add(tabbedPane);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            LoginPanel login = new LoginPanel(null);
            login.setVisible(true);

            if (login.isAuthenticated()) {
                User loggedUser = login.getLoggedUser();
                if (loggedUser != null) {
                    new MainApp(loggedUser).setVisible(true);
                }
            } else {
                System.exit(0);
            }
        });
    }
}