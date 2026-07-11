package com.example.themannyhub.data;

import com.example.themannyhub.models.User;
import org.json.JSONArray;
import org.json.JSONObject;
import java.nio.file.*;
import java.util.*;
import static com.example.themannyhub.utils.ValidationUtil.generateSalt;
import static com.example.themannyhub.utils.ValidationUtil.hashPassword;

public class UserDAO {
    private static final String FILE_PATH = "users.json";

    public List<User> loadUsers() {
        List<User> users = new ArrayList<>();
        try {
            String content = Files.readString(Path.of(FILE_PATH));
            JSONArray jsonArray = new JSONArray(content);

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject obj = jsonArray.getJSONObject(i);
                User user = new User(
                        obj.getInt("id"),
                        obj.getString("username"),
                        obj.getString("passwordHash"),
                        obj.getString("displayName"),
                        obj.optString("role", "TAILOR")
                );
                // Also load salt if stored separately
                users.add(user);
            }
        } catch (Exception e) {
            System.out.println("Users file not found or empty. Creating default.");
            createDefaultUsers();
            return loadUsers(); // Retry after creating defaults
        }
        return users;
    }

    public void saveUsers(List<User> users) {
        JSONArray jsonArray = new JSONArray();
        for (User user : users) {
            JSONObject obj = new JSONObject();
            obj.put("id", user.getId());
            obj.put("username", user.getUsername());
            obj.put("passwordHash", user.getPasswordHash());
            obj.put("displayName", user.getDisplayName());
            obj.put("role", user.getRole());
            jsonArray.put(obj);
        }
        try {
            Files.writeString(Path.of(FILE_PATH), jsonArray.toString(4));
        } catch (Exception e) {
            System.err.println("Error saving users: " + e.getMessage());
        }
    }

    private void createDefaultUsers() {
        List<User> defaultUsers = new ArrayList<>();
        // Create default admin with known password
        String salt = generateSalt();
        String hash = hashPassword("admin123", salt);
        defaultUsers.add(new User(1, "admin", salt + ":" + hash,
                "Administrator", "ADMIN"));
        saveUsers(defaultUsers);
    }
}