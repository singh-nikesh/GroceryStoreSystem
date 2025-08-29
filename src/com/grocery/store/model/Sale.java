// File: src/com/grocery/store/model/Sale.java
package com.grocery.store.model;

import java.sql.Date;

public class Sale {
    private String invoiceId;
    private String productName;
    private int quantity;
    private double price;
    private double total;
    private Date saleDate;

    // Constructor
    public Sale(String invoiceId, String productName, int quantity,
                double price, double total, Date saleDate) {
        this.invoiceId = invoiceId;
        this.productName = productName;
        this.quantity = quantity;
        this.price = price;
        this.total = total;
        this.saleDate = saleDate;
    }

    // Getters
    public String getInvoiceId() { return invoiceId; }
    public String getProductName() { return productName; }
    public int getQuantity() { return quantity; }
    public double getPrice() { return price; }
    public double getTotal() { return total; }
    public Date getSaleDate() { return saleDate; }
}