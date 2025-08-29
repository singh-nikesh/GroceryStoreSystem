package com.grocery.store.ui;

import com.grocery.store.dao.UserDAO;
import com.grocery.store.model.User;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.sql.SQLException;

public class LoginPanel extends JDialog {
    private JTextField tfUsername;
    private JPasswordField pfPassword;
    private boolean authenticated = false;
    private User loggedUser;

    public LoginPanel(JFrame parent) {
        super(parent, "Login", true);
        initializeUI();
    }

    private void initializeUI() {
        setLayout(new BorderLayout());
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(30, 40, 30, 40)); // Added padding
        panel.setBackground(new Color(245, 245, 245)); // Light gray background

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(10, 10, 10, 10); // Spacing between components

        JLabel lblTitle = new JLabel("Login");
        lblTitle.setFont(new Font("Arial", Font.BOLD, 22));
        lblTitle.setForeground(new Color(50, 50, 150)); // Dark blue color
        gbc.gridwidth = 2;
        gbc.gridx = 0;
        gbc.gridy = 0;
        panel.add(lblTitle, gbc);

        gbc.gridwidth = 1; // Reset width

        gbc.gridy++;
        panel.add(new JLabel("Username:"), gbc);

        gbc.gridx = 1;
        tfUsername = new JTextField(15);
        tfUsername.setFont(new Font("Arial", Font.PLAIN, 14));
        panel.add(tfUsername, gbc);

        gbc.gridx = 0;
        gbc.gridy++;
        panel.add(new JLabel("Password:"), gbc);

        gbc.gridx = 1;
        pfPassword = new JPasswordField(15);
        pfPassword.setFont(new Font("Arial", Font.PLAIN, 14));
        panel.add(pfPassword, gbc);

        gbc.gridx = 0;
        gbc.gridy++;
        gbc.gridwidth = 2;
        JButton btnLogin = new JButton("Login");
        btnLogin.setFont(new Font("Arial", Font.BOLD, 16));
        btnLogin.setBackground(new Color(50, 150, 250)); // Blue background
        btnLogin.setForeground(Color.WHITE); // White text
        btnLogin.setFocusPainted(false);
        btnLogin.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        btnLogin.addActionListener(this::performLogin);
        panel.add(btnLogin, gbc);

        add(panel, BorderLayout.CENTER);

        setSize(400, 300); // Increased size
        setResizable(false);
        setLocationRelativeTo(null);
    }

    private void performLogin(ActionEvent e) {
        try {
            UserDAO userDAO = new UserDAO();
            User user = userDAO.authenticate(
                    tfUsername.getText(),
                    new String(pfPassword.getPassword())
            );

            if (user != null) {
                authenticated = true;
                loggedUser = user;
                dispose();
            } else {
                JOptionPane.showMessageDialog(this,
                        "Invalid credentials",
                        "Login Failed",
                        JOptionPane.ERROR_MESSAGE);
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this,
                    "Database error: " + ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    public boolean isAuthenticated() {
        return authenticated;
    }

    public User getLoggedUser() {
        return loggedUser;
    }
}
