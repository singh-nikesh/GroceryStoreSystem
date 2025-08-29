package com.grocery.store.ui;

import com.grocery.store.dao.SaleDAO;
import com.grocery.store.model.Sale;
import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.sql.SQLException;
import java.text.SimpleDateFormat;

public class SalesHistoryPanel extends JPanel {
    private final DefaultTableModel salesModel;
    private final SaleDAO saleDAO;
    private final SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd");

    public SalesHistoryPanel() {
        setLayout(new BorderLayout(10, 10));
        setBackground(Color.WHITE);
        saleDAO = new SaleDAO();

        // Table setup with uneditable cells
        String[] columns = {"Invoice ID", "Product", "Quantity", "Price", "Total", "Date"};
        salesModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        JTable salesTable = new JTable(salesModel);
        styleTable(salesTable);

        // Panel for the Refresh button
        JPanel topPanel = new JPanel();
        topPanel.setLayout(new FlowLayout(FlowLayout.RIGHT));
        topPanel.setBackground(Color.WHITE);
        JButton refreshButton = new JButton("Refresh");
        styleButton(refreshButton);
        refreshButton.addActionListener(this::refreshSalesTable);

        topPanel.add(refreshButton);

        add(topPanel, BorderLayout.NORTH);
        add(new JScrollPane(salesTable), BorderLayout.CENTER);

        loadSalesData(); // Load data on initialization
    }

    public void refreshSalesTable(ActionEvent e) {
        salesModel.setRowCount(0); // Clear existing data
        loadSalesData(); // Reload from database
    }

    private void loadSalesData() {
        try {
            salesModel.setRowCount(0); // Clear existing data
            for (Sale sale : saleDAO.getAllSales()) {
                salesModel.addRow(new Object[]{
                        sale.getInvoiceId(),
                        sale.getProductName(),
                        sale.getQuantity(),
                        formatCurrency(sale.getPrice()),
                        formatCurrency(sale.getTotal()),
                        formatDate(sale.getSaleDate()) // Handles null dates safely
                });
            }
        } catch (SQLException e) {
            showErrorDialog("Failed to load sales data: " + e.getMessage());
        }
    }

    // Format currency values consistently
    private String formatCurrency(double value) {
        return String.format("$%.2f", value);
    }

    // Safely format dates with null check
    private String formatDate(java.sql.Date sqlDate) {
        if (sqlDate == null) return "N/A"; // Handle null dates
        return dateFormatter.format(sqlDate);
    }

    // Centralized error handling
    private void showErrorDialog(String message) {
        JOptionPane.showMessageDialog(
                this,
                message,
                "Database Error",
                JOptionPane.ERROR_MESSAGE
        );
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

    private void styleButton(JButton button) {
        button.setFont(new Font("Arial", Font.BOLD, 14));
        button.setBackground(new Color(50, 150, 250)); // Blue color
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
    }
}
