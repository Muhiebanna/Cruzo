package com.example.bliqclone.api;

import com.example.bliqclone.api.mock.ExtendedMockDriverServiceImpl;
import com.example.bliqclone.api.mock.ExtendedMockRideServiceImpl;
import com.example.bliqclone.api.mock.MockAuthServiceImpl;

/**
 * Updated Singleton class to manage API services with extended implementations
 */
public class ApiManager {
    private static ApiManager instance;
    
    private ExtendedRideService rideService;
    private AuthService authService;
    private DriverService driverService;
    
    private ApiManager() {
        // Initialize mock services with extended implementations
        rideService = new ExtendedMockRideServiceImpl();
        authService = new MockAuthServiceImpl();
        driverService = new ExtendedMockDriverServiceImpl();
    }
    
    public static synchronized ApiManager getInstance() {
        if (instance == null) {
            instance = new ApiManager();
        }
        return instance;
    }
    
    public ExtendedRideService getRideService() {
        return rideService;
    }
    
    public AuthService getAuthService() {
        return authService;
    }
    
    public DriverService getDriverService() {
        return driverService;
    }
}
