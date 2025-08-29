package com.grocery.store.dao;

import com.grocery.store.util.DatabaseConnection; // Critical import
import java.sql.*;

public class SalesDAO {
    public void recordSale(int productId, int quantity, double price) throws SQLException {
        String sql = "INSERT INTO invoice_items (invoice_id, product_id, quantity, price) " +
                "VALUES (LAST_INSERT_ID(), ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection(); // Now works
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, productId);
            stmt.setInt(2, quantity);
            stmt.setDouble(3, price);
            stmt.executeUpdate();
        }
    }
}