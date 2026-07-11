package com.example.themannyhub.services;

import com.example.themannyhub.data.UserDAO;
import com.example.themannyhub.models.User;
import com.example.themannyhub.utils.ValidationUtil;
import java.util.List;
import java.util.Optional;

public class AuthService {
    private List<User> users;
    private final UserDAO userDAO;
    private User currentUser;

    public AuthService() {
        this.userDAO = new UserDAO();
        this.users = userDAO.loadUsers();
    }

    public boolean authenticate(String username, String password) {
        Optional<User> userOpt = users.stream()
                .filter(u -> u.getUsername().equalsIgnoreCase(username))
                .findFirst();

        if (userOpt.isEmpty()) {
            return false;
        }

        User user = userOpt.get();
        String[] parts = user.getPasswordHash().split(":");
        String storedSalt = parts[0];
        String storedHash = parts[1];
        String inputHash = ValidationUtil.hashPassword(password, storedSalt);

        if (storedHash.equals(inputHash)) {
            this.currentUser = user;
            return true;
        }
        return false;
    }

    public User getCurrentUser() {
        return currentUser;
    }

    public void logout() {
        currentUser = null;
    }

    public boolean isLoggedIn() {
        return currentUser != null;
    }

    // For future: user management
    public void addUser(String username, String password,
                        String displayName, String role) {
        String salt = ValidationUtil.generateSalt();
        String hash = ValidationUtil.hashPassword(password, salt);
        int newId = users.stream().mapToInt(User::getId).max().orElse(0) + 1;

        User newUser = new User(newId, username, salt + ":" + hash,
                displayName, role);
        users.add(newUser);
        userDAO.saveUsers(users);
    }
}