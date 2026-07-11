package com.example.themannyhub.models;

public class User {
    private int id;
    private String username;
    private String passwordHash;  // Store hashed, never plain text
    private String displayName;
    private String role;          // "ADMIN", "TAILOR", etc.

    // Constructor
    public User(int id, String username, String passwordHash,
                String displayName, String role) {
        this.id = id;
        this.username = username;
        this.passwordHash = passwordHash;
        this.displayName = displayName;
        this.role = role;
    }

    // Getters and Setters
    public int getId() { return id; }
    public String getUsername() { return username; }
    public String getPasswordHash() { return passwordHash; }
    public String getDisplayName() { return displayName; }
    public String getRole() { return role; }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    @Override
    public String toString() {
        return displayName + " (" + role + ")";
    }
}