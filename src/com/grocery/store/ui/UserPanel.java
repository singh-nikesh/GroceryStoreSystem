package com.grocery.store.ui;

import com.grocery.store.dao.UserDAO;
import com.grocery.store.model.User;
import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.sql.SQLException;
import java.util.List;

public class UserPanel extends JPanel {
    private final DefaultTableModel userModel;
    private final UserDAO userDAO;
    private JTable userTable;
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JComboBox<String> roleComboBox;
    private JButton logoutButton;

    public UserPanel() {
        setLayout(new BorderLayout(10, 10));
        setBackground(Color.WHITE);
        userDAO = new UserDAO();

        // Top Panel with Logout Button
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(Color.WHITE);
        logoutButton = new JButton("Logout");
        styleButton(logoutButton);
        logoutButton.addActionListener(this::logout);
        topPanel.add(logoutButton, BorderLayout.EAST);
        add(topPanel, BorderLayout.NORTH);

        // User Table Setup
        String[] columns = {"User ID", "Username", "Role"};
        userModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        userTable = new JTable(userModel);
        userTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        userTable.getSelectionModel().addListSelectionListener(this::populateFormFromSelectedRow);
        JScrollPane scrollPane = new JScrollPane(userTable);

        // Control Panel for Adding/Editing/Deleting Users
        JPanel controlPanel = createControlPanel();

        add(scrollPane, BorderLayout.CENTER);
        add(controlPanel, BorderLayout.SOUTH);

        loadUsers();
    }

    private JPanel createControlPanel() {
        JPanel panel = new JPanel(new GridLayout(0, 2, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        panel.setBackground(Color.WHITE);

        // Form Components
        usernameField = new JTextField();
        passwordField = new JPasswordField();
        roleComboBox = new JComboBox<>(new String[]{"Admin", "Staff"});

        // Buttons
        JButton addButton = new JButton("Add User");
        JButton editButton = new JButton("Edit User");
        JButton deleteButton = new JButton("Delete User");

        styleButton(addButton);
        styleButton(editButton);
        styleButton(deleteButton);

        // Add Components to Panel
        panel.add(new JLabel("Username:"));
        panel.add(usernameField);
        panel.add(new JLabel("Password:"));
        panel.add(passwordField);
        panel.add(new JLabel("Role:"));
        panel.add(roleComboBox);
        panel.add(addButton);
        panel.add(editButton);
        panel.add(deleteButton);

        // Event Listeners
        addButton.addActionListener(this::addUser);
        editButton.addActionListener(this::editUser);
        deleteButton.addActionListener(this::deleteUser);

        return panel;
    }

    private void loadUsers() {
        try {
            userModel.setRowCount(0);
            List<User> users = userDAO.getAllUsers();
            for (User user : users) {
                userModel.addRow(new Object[]{
                        user.getUserId(),
                        user.getUsername(),
                        user.getRole()
                });
            }
        } catch (SQLException e) {
            showError("Error loading users: " + e.getMessage());
        }
    }

    private void addUser(ActionEvent e) {
        try {
            String username = usernameField.getText().trim();
            String password = new String(passwordField.getPassword());
            String role = (String) roleComboBox.getSelectedItem();

            if (validateInput(username, password)) {
                userDAO.addUser(new User(0, username, password, role));
                clearForm();
                loadUsers();
            }
        } catch (SQLException ex) {
            showError("Error adding user: " + ex.getMessage());
        }
    }

    private void editUser(ActionEvent e) {
        int selectedRow = getSelectedRow();
        if (selectedRow == -1) return;

        try {
            int userId = (int) userModel.getValueAt(selectedRow, 0);
            String username = usernameField.getText().trim();
            String password = new String(passwordField.getPassword());
            String role = (String) roleComboBox.getSelectedItem();

            if (validateInput(username, password)) {
                userDAO.updateUser(new User(userId, username, password, role));
                clearForm();
                loadUsers();
            }
        } catch (SQLException ex) {
            showError("Error updating user: " + ex.getMessage());
        }
    }

    private void deleteUser(ActionEvent e) {
        int selectedRow = getSelectedRow();
        if (selectedRow == -1) return;

        int confirm = JOptionPane.showConfirmDialog(
                this,
                "Are you sure you want to delete this user?",
                "Confirm Deletion",
                JOptionPane.YES_NO_OPTION
        );

        if (confirm == JOptionPane.YES_OPTION) {
            try {
                int userId = (int) userModel.getValueAt(selectedRow, 0);
                userDAO.deleteUser(userId);
                loadUsers();
            } catch (SQLException ex) {
                showError("Error deleting user: " + ex.getMessage());
            }
        }
    }

    private boolean validateInput(String username, String password) {
        if (username.isEmpty() || password.isEmpty()) {
            showError("Username and password cannot be empty!");
            return false;
        }
        return true;
    }

    private int getSelectedRow() {
        int selectedRow = userTable.getSelectedRow();
        if (selectedRow == -1) {
            showError("Please select a user from the table!");
            return -1;
        }
        return selectedRow;
    }

    private void clearForm() {
        usernameField.setText("");
        passwordField.setText("");
        roleComboBox.setSelectedIndex(0);
    }

    private void populateFormFromSelectedRow(javax.swing.event.ListSelectionEvent e) {
        if (!e.getValueIsAdjusting()) {
            int selectedRow = userTable.getSelectedRow();
            if (selectedRow != -1) {
                usernameField.setText(userModel.getValueAt(selectedRow, 1).toString());
                // For security, password is not auto-filled.
                passwordField.setText("");
                roleComboBox.setSelectedItem(userModel.getValueAt(selectedRow, 2).toString());
            }
        }
    }

    private void logout(ActionEvent e) {
        int confirm = JOptionPane.showConfirmDialog(
                this,
                "Are you sure you want to logout?",
                "Logout Confirmation",
                JOptionPane.YES_NO_OPTION
        );
        if (confirm == JOptionPane.YES_OPTION) {
            // Implement your logout logic here, e.g., navigate to the login screen.
            JOptionPane.showMessageDialog(this, "Logged out successfully!", "Logout", JOptionPane.INFORMATION_MESSAGE);
            // For example, you might close the current frame:
            SwingUtilities.getWindowAncestor(this).dispose();
        }
    }

    private void styleButton(JButton button) {
        button.setFont(new Font("Arial", Font.BOLD, 14));
        button.setBackground(new Color(50, 150, 250));
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(5, 15, 5, 15));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
    }

    private void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "Error", JOptionPane.ERROR_MESSAGE);
    }
}
