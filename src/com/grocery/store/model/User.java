package com.grocery.store.model;

public class User {
    private int userId;
    private String username;
    private String password;
    private String role;

    // Constructor for 3 arguments (used when password isn't needed)
    public User(int userId, String username, String role) {
        this.userId = userId;
        this.username = username;
        this.role = role;
    }

    // Constructor for 4 arguments (used when password is included)
    public User(int userId, String username, String password, String role) {
        this.userId = userId;
        this.username = username;
        this.password = password;
        this.role = role;
    }

    // Getters
    public int getUserId() { return userId; }
    public String getUsername() { return username; }
    public String getPassword() { return password; }
    public String getRole() { return role; }
}