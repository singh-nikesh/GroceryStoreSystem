package com.grocery.store.ui;

import com.grocery.store.dao.ProductDAO;
import com.grocery.store.dao.SaleDAO;
import com.grocery.store.model.Product;
import com.grocery.store.model.Sale;
import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.sql.SQLException;
import java.sql.Date;
import java.util.List;

public class SalesPanel extends JPanel {
    private final DefaultTableModel salesModel;
    private final ProductDAO productDAO;
    private final SaleDAO saleDAO;
    private String currentInvoiceId;
    private final JComboBox<String> productComboBox;
    private final JSpinner quantitySpinner;

    public SalesPanel() {
        setLayout(new BorderLayout(10, 10));
        setBackground(Color.WHITE);
        productDAO = new ProductDAO();
        saleDAO = new SaleDAO();

        // Sales Table Setup
        String[] columns = {"Invoice ID", "Product", "Quantity", "Price", "Total"};
        salesModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        JTable salesTable = new JTable(salesModel);
        styleTable(salesTable);

        // Checkout Panel Components
        JPanel checkoutPanel = new JPanel(new GridBagLayout());
        checkoutPanel.setBorder(BorderFactory.createTitledBorder("New Sale"));
        checkoutPanel.setBackground(Color.WHITE);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.gridx = 0;

        productComboBox = new JComboBox<>();
        quantitySpinner = new JSpinner(new SpinnerNumberModel(1, 1, 100, 1));

        gbc.gridy = 0;
        checkoutPanel.add(new JLabel("Product:"), gbc);
        gbc.gridx = 1;
        checkoutPanel.add(productComboBox, gbc);

        gbc.gridx = 0;
        gbc.gridy++;
        checkoutPanel.add(new JLabel("Quantity:"), gbc);
        gbc.gridx = 1;
        checkoutPanel.add(quantitySpinner, gbc);

        gbc.gridx = 0;
        gbc.gridy++;
        gbc.gridwidth = 2;
        JButton addItemButton = new JButton("Add Item");
        JButton checkoutButton = new JButton("Checkout");

        styleButton(addItemButton);
        styleButton(checkoutButton);

        JPanel buttonPanel = new JPanel();
        buttonPanel.setBackground(Color.WHITE);
        buttonPanel.add(addItemButton);
        buttonPanel.add(checkoutButton);

        checkoutPanel.add(buttonPanel, gbc);

        add(new JScrollPane(salesTable), BorderLayout.CENTER);
        add(checkoutPanel, BorderLayout.SOUTH);

        // Event Handlers
        addItemButton.addActionListener(this::addItemToSale);
        checkoutButton.addActionListener(this::processCheckout);

        loadProductsIntoComboBox();
        startNewSale();
    }

    private void loadProductsIntoComboBox() {
        try {
            List<Product> products = productDAO.getAllProducts();
            productComboBox.removeAllItems();
            for (Product product : products) {
                productComboBox.addItem(product.getName());
            }
        } catch (SQLException e) {
            showError("Error loading products: " + e.getMessage());
        }
    }

    private Product getSelectedProduct() throws SQLException {
        String productName = (String) productComboBox.getSelectedItem();
        return productDAO.getProductByName(productName);
    }

    private void startNewSale() {
        currentInvoiceId = "INV-" + System.currentTimeMillis();
        salesModel.setRowCount(0);
    }

    private void addItemToSale(ActionEvent e) {
        try {
            Product product = getSelectedProduct();
            int quantity = (int) quantitySpinner.getValue();

            if (product.getQuantity() < quantity) {
                showWarning("Insufficient stock! Available: " + product.getQuantity());
                return;
            }

            salesModel.addRow(new Object[]{
                    currentInvoiceId,
                    product.getName(),
                    quantity,
                    formatCurrency(product.getPrice()),
                    formatCurrency(quantity * product.getPrice())
            });

        } catch (SQLException ex) {
            showError("Database error: " + ex.getMessage());
        }
    }

    private void processCheckout(ActionEvent e) {
        try {
            for (int row = 0; row < salesModel.getRowCount(); row++) {
                // Update inventory
                String productName = (String) salesModel.getValueAt(row, 1);
                int quantitySold = (Integer) salesModel.getValueAt(row, 2);
                Product product = productDAO.getProductByName(productName);
                productDAO.updateProductQuantity(product.getProductId(), quantitySold);

                // Save sale record
                Sale sale = new Sale(
                        (String) salesModel.getValueAt(row, 0),
                        productName,
                        quantitySold,
                        parseCurrency(salesModel.getValueAt(row, 3)),
                        parseCurrency(salesModel.getValueAt(row, 4)),
                        new Date(System.currentTimeMillis())
                );
                saleDAO.addSale(sale);
            }

            JOptionPane.showMessageDialog(this,
                    "Sale completed!\nTotal: " + formatCurrency(calculateGrandTotal()),
                    "Success",
                    JOptionPane.INFORMATION_MESSAGE);

            startNewSale();

        } catch (SQLException | NumberFormatException ex) {
            showError("Checkout failed: " + ex.getMessage());
        }
    }

    // Helper Methods
    private String formatCurrency(double amount) {
        return String.format("$%.2f", amount);
    }

    private double parseCurrency(Object value) {
        return Double.parseDouble(value.toString().replace("$", ""));
    }

    private double calculateGrandTotal() {
        double total = 0;
        for (int row = 0; row < salesModel.getRowCount(); row++) {
            total += parseCurrency(salesModel.getValueAt(row, 4));
        }
        return total;
    }

    private void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "Error", JOptionPane.ERROR_MESSAGE);
    }

    private void showWarning(String message) {
        JOptionPane.showMessageDialog(this, message, "Warning", JOptionPane.WARNING_MESSAGE);
    }

    private void styleButton(JButton button) {
        button.setFont(new Font("Arial", Font.BOLD, 14));
        button.setBackground(new Color(50, 150, 250)); // Blue color
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
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
