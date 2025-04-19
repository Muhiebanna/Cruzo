package com.example.bliqclone.api.mock;

import android.os.Handler;
import android.os.Looper;

import com.example.bliqclone.api.AuthService;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

/**
 * Mock implementation of the AuthService interface
 */
public class MockAuthServiceImpl implements AuthService {
    private static final Random random = new Random();
    private final Handler handler = new Handler(Looper.getMainLooper());
    
    // Mock user database
    private final Map<String, UserInfo> users = new HashMap<>();
    private String currentUserId = null;
    
    public MockAuthServiceImpl() {
        // Add some mock users
        UserInfo user1 = new UserInfo("user1@example.com", "password123", "Ahmed Mohamed");
        UserInfo user2 = new UserInfo("user2@example.com", "password456", "Sara Ahmed");
        
        users.put(user1.id, user1);
        users.put(user2.id, user2);
    }
    
    @Override
    public void login(String email, String password, AuthServiceCallback<String> callback) {
        // Simulate network delay
        handler.postDelayed(() -> {
            try {
                // Find user by email
                UserInfo user = null;
                for (UserInfo u : users.values()) {
                    if (u.email.equals(email)) {
                        user = u;
                        break;
                    }
                }
                
                if (user == null) {
                    callback.onError("User not found");
                    return;
                }
                
                if (!user.password.equals(password)) {
                    callback.onError("Invalid password");
                    return;
                }
                
                // Login successful
                currentUserId = user.id;
                callback.onSuccess(user.id);
            } catch (Exception e) {
                callback.onError("Login failed: " + e.getMessage());
            }
        }, 1000 + random.nextInt(500));
    }
    
    @Override
    public void register(String email, String password, String name, AuthServiceCallback<String> callback) {
        // Simulate network delay
        handler.postDelayed(() -> {
            try {
                // Check if email already exists
                for (UserInfo u : users.values()) {
                    if (u.email.equals(email)) {
                        callback.onError("Email already registered");
                        return;
                    }
                }
                
                // Create new user
                UserInfo newUser = new UserInfo(email, password, name);
                users.put(newUser.id, newUser);
                
                // Auto login
                currentUserId = newUser.id;
                callback.onSuccess(newUser.id);
            } catch (Exception e) {
                callback.onError("Registration failed: " + e.getMessage());
            }
        }, 1500 + random.nextInt(1000));
    }
    
    @Override
    public void logout(AuthServiceCallback<Boolean> callback) {
        // Simulate network delay
        handler.postDelayed(() -> {
            try {
                currentUserId = null;
                callback.onSuccess(true);
            } catch (Exception e) {
                callback.onError("Logout failed: " + e.getMessage());
            }
        }, 500 + random.nextInt(300));
    }
    
    @Override
    public void isLoggedIn(AuthServiceCallback<Boolean> callback) {
        // Simulate network delay
        handler.postDelayed(() -> {
            try {
                callback.onSuccess(currentUserId != null);
            } catch (Exception e) {
                callback.onError("Failed to check login status: " + e.getMessage());
            }
        }, 300 + random.nextInt(200));
    }
    
    @Override
    public void getCurrentUserId(AuthServiceCallback<String> callback) {
        // Simulate network delay
        handler.postDelayed(() -> {
            try {
                if (currentUserId == null) {
                    callback.onError("No user logged in");
                    return;
                }
                
                callback.onSuccess(currentUserId);
            } catch (Exception e) {
                callback.onError("Failed to get current user ID: " + e.getMessage());
            }
        }, 300 + random.nextInt(200));
    }
    
    /**
     * Class to store user information
     */
    private static class UserInfo {
        String id;
        String email;
        String password;
        String name;
        
        UserInfo(String email, String password, String name) {
            this.id = UUID.randomUUID().toString();
            this.email = email;
            this.password = password;
            this.name = name;
        }
    }
}
