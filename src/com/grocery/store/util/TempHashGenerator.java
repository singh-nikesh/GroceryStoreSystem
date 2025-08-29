package com.grocery.store.util;

public class TempHashGenerator {
    public static void main(String[] args) {
        String password = "admin123"; // Your desired password
        String hashed = PasswordUtil.hashPassword(password);
        System.out.println("Generated Hash: " + hashed);
    }
}