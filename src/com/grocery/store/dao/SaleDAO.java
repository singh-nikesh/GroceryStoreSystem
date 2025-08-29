package com.grocery.store.dao;

import com.grocery.store.model.Sale;
import com.grocery.store.util.DatabaseConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SaleDAO {
    // Fetch all sales records
    public List<Sale> getAllSales() throws SQLException {
        List<Sale> sales = new ArrayList<>();
        String sql = "SELECT invoice_id, product_name, quantity, price, total, sale_date FROM sales";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                sales.add(new Sale(
                        rs.getString("invoice_id"),
                        rs.getString("product_name"),
                        rs.getInt("quantity"),
                        rs.getDouble("price"),
                        rs.getDouble("total"),
                        rs.getDate("sale_date")
                ));
            }
        } catch (SQLException e) {
            System.err.println("Error fetching sales: " + e.getMessage());
            throw e;
        }
        return sales;
    }

    // NEW: Save a sale record to the database
    public void addSale(Sale sale) throws SQLException {
        String sql = "INSERT INTO sales (invoice_id, product_name, quantity, price, total, sale_date) " +
                "VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, sale.getInvoiceId());
            stmt.setString(2, sale.getProductName());
            stmt.setInt(3, sale.getQuantity());
            stmt.setDouble(4, sale.getPrice());
            stmt.setDouble(5, sale.getTotal());
            stmt.setDate(6, sale.getSaleDate()); // java.sql.Date

            stmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error saving sale: " + e.getMessage());
            throw e;
        }
    }
}