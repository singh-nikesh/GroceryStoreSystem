package com.grocery.store.util;

public class HashGenerator {
    public static void main(String[] args) {
        String password = "admin123";
        String hashedPassword = PasswordUtil.hashPassword(password);
        System.out.println("Generated Hash: " + hashedPassword);
    }
}