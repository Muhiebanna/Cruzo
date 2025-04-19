package com.example.bliqclone.api;

/**
 * Interface for authentication service API
 */
public interface AuthService {
    /**
     * Login with email and password
     * @param email User email
     * @param password User password
     * @param callback Callback to handle the result
     */
    void login(String email, String password, AuthServiceCallback<String> callback);
    
    /**
     * Register a new user
     * @param email User email
     * @param password User password
     * @param name User name
     * @param callback Callback to handle the result
     */
    void register(String email, String password, String name, AuthServiceCallback<String> callback);
    
    /**
     * Logout the current user
     * @param callback Callback to handle the result
     */
    void logout(AuthServiceCallback<Boolean> callback);
    
    /**
     * Check if a user is currently logged in
     * @param callback Callback to handle the result
     */
    void isLoggedIn(AuthServiceCallback<Boolean> callback);
    
    /**
     * Get the current user's ID
     * @param callback Callback to handle the result
     */
    void getCurrentUserId(AuthServiceCallback<String> callback);
    
    /**
     * Callback interface for authentication service API calls
     * @param <T> The type of the result
     */
    interface AuthServiceCallback<T> {
        void onSuccess(T result);
        void onError(String errorMessage);
    }
}
