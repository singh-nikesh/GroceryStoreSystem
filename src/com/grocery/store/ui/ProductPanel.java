package com.grocery.store.ui;

import com.grocery.store.dao.ProductDAO;
import com.grocery.store.model.Product;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.sql.SQLException;

public class ProductPanel extends JPanel {
    private final ProductDAO productDAO;
    private final DefaultTableModel tableModel;

    public ProductPanel() {
        productDAO = new ProductDAO();
        setLayout(new BorderLayout(10, 10));
        setBackground(Color.WHITE);

        // Table Setup
        String[] columns = {"ID", "Name", "Category", "Price", "Quantity"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Make table non-editable
            }
        };

        JTable productTable = new JTable(tableModel);
        styleTable(productTable);

        // Form Panel
        JPanel formPanel = createAddProductForm();

        // Add components
        add(new JScrollPane(productTable), BorderLayout.CENTER);
        add(formPanel, BorderLayout.SOUTH);

        loadProducts();
    }

    private JPanel createAddProductForm() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Add New Product"));
        panel.setBackground(Color.WHITE);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.gridx = 0;

        // Form Components
        JTextField nameField = new JTextField(15);
        JTextField categoryField = new JTextField(15);
        JTextField priceField = new JTextField(15);
        JTextField quantityField = new JTextField(15);

        gbc.gridy = 0;
        panel.add(new JLabel("Name:"), gbc);
        gbc.gridx = 1;
        panel.add(nameField, gbc);

        gbc.gridx = 0;
        gbc.gridy++;
        panel.add(new JLabel("Category:"), gbc);
        gbc.gridx = 1;
        panel.add(categoryField, gbc);

        gbc.gridx = 0;
        gbc.gridy++;
        panel.add(new JLabel("Price ($):"), gbc);
        gbc.gridx = 1;
        panel.add(priceField, gbc);

        gbc.gridx = 0;
        gbc.gridy++;
        panel.add(new JLabel("Quantity:"), gbc);
        gbc.gridx = 1;
        panel.add(quantityField, gbc);

        gbc.gridx = 0;
        gbc.gridy++;
        gbc.gridwidth = 2;
        JButton addButton = new JButton("Add Product");
        styleButton(addButton);
        addButton.addActionListener(e -> {
            try {
                if (validateInputs(nameField, categoryField, priceField, quantityField)) {
                    Product product = new Product(
                            0,
                            nameField.getText().trim(),
                            categoryField.getText().trim(),
                            Double.parseDouble(priceField.getText()),
                            Integer.parseInt(quantityField.getText())
                    );

                    productDAO.addProduct(product);
                    loadProducts();
                    clearFields(nameField, categoryField, priceField, quantityField);
                }
            } catch (SQLException | NumberFormatException ex) {
                JOptionPane.showMessageDialog(this,
                        "Error: " + ex.getMessage(),
                        "Input Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        });

        panel.add(addButton, gbc);

        return panel;
    }

    private boolean validateInputs(JTextField... fields) {
        for (JTextField field : fields) {
            if (field.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(this,
                        "All fields are required!",
                        "Validation Error",
                        JOptionPane.WARNING_MESSAGE);
                return false;
            }
        }
        return true;
    }

    private void loadProducts() {
        try {
            tableModel.setRowCount(0);
            for (Product product : productDAO.getAllProducts()) {
                tableModel.addRow(new Object[]{
                        product.getProductId(),
                        product.getName(),
                        product.getCategory(),
                        String.format("$%.2f", product.getPrice()),
                        product.getQuantity()
                });
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this,
                    "Error loading products: " + e.getMessage(),
                    "Database Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void clearFields(JTextField... fields) {
        for (JTextField field : fields) {
            field.setText("");
        }
    }

    private void styleButton(JButton button) {
        button.setFont(new Font("Arial", Font.BOLD, 14));
        button.setBackground(new Color(50, 150, 250)); // Blue
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
    }

    private void styleTable(JTable table) {
        table.setRowHeight(25);
        table.setFont(new Font("Arial", Font.PLAIN, 14));
        table.setGridColor(new Color(200, 200, 200));

        JTableHeader header = table.getTableHeader();
        header.setFont(new Font("Arial", Font.BOLD, 14));
        header.setBackground(new Color(50, 150, 250));
        header.setForeground(Color.WHITE);

        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        for (int i = 0; i < table.getColumnCount(); i++) {
            table.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
        }
    }
}
