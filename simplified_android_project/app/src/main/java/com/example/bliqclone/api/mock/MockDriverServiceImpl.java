package com.example.bliqclone.api.mock;

import android.os.Handler;
import android.os.Looper;

import com.example.bliqclone.api.DriverService;
import com.example.bliqclone.models.RideRequest;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;

/**
 * Mock implementation of the DriverService interface
 */
public class MockDriverServiceImpl implements DriverService {
    private static final Random random = new Random();
    private final Handler handler = new Handler(Looper.getMainLooper());
    
    @Override
    public void getAvailableRequests(DriverServiceCallback<List<RideRequest>> callback) {
        // Simulate network delay
        handler.postDelayed(() -> {
            try {
                List<RideRequest> requests = generateMockRequests();
                callback.onSuccess(requests);
            } catch (Exception e) {
                callback.onError("Failed to get available requests: " + e.getMessage());
            }
        }, 1000 + random.nextInt(500));
    }
    
    @Override
    public void acceptRideRequest(RideRequest request, DriverServiceCallback<Boolean> callback) {
        // Simulate network delay
        handler.postDelayed(() -> {
            try {
                // 90% success rate for accepting requests
                boolean success = random.nextDouble() < 0.9;
                if (success) {
                    callback.onSuccess(true);
                } else {
                    callback.onError("Failed to accept request. Another driver may have accepted it.");
                }
            } catch (Exception e) {
                callback.onError("Failed to accept request: " + e.getMessage());
            }
        }, 1500 + random.nextInt(1000));
    }
    
    @Override
    public void rejectRideRequest(RideRequest request, DriverServiceCallback<Boolean> callback) {
        // Simulate network delay
        handler.postDelayed(() -> {
            try {
                // Always succeed for rejecting
                callback.onSuccess(true);
            } catch (Exception e) {
                callback.onError("Failed to reject request: " + e.getMessage());
            }
        }, 800 + random.nextInt(700));
    }
    
    @Override
    public void completeRide(String rideId, DriverServiceCallback<Boolean> callback) {
        // Simulate network delay
        handler.postDelayed(() -> {
            try {
                // 95% success rate for completing rides
                boolean success = random.nextDouble() < 0.95;
                if (success) {
                    callback.onSuccess(true);
                } else {
                    callback.onError("Failed to complete ride. Please try again.");
                }
            } catch (Exception e) {
                callback.onError("Failed to complete ride: " + e.getMessage());
            }
        }, 1200 + random.nextInt(800));
    }
    
    @Override
    public void cancelRide(String rideId, String reason, DriverServiceCallback<Boolean> callback) {
        // Simulate network delay
        handler.postDelayed(() -> {
            try {
                // 90% success rate for canceling rides
                boolean success = random.nextDouble() < 0.9;
                if (success) {
                    callback.onSuccess(true);
                } else {
                    callback.onError("Failed to cancel ride. Please try again.");
                }
            } catch (Exception e) {
                callback.onError("Failed to cancel ride: " + e.getMessage());
            }
        }, 1000 + random.nextInt(800));
    }
    
    /**
     * Generate mock ride requests
     */
    private List<RideRequest> generateMockRequests() {
        List<RideRequest> requests = new ArrayList<>();
        
        // Generate 0-5 random requests
        int numRequests = random.nextInt(6);
        
        for (int i = 0; i < numRequests; i++) {
            RideRequest request = new RideRequest(
                    UUID.randomUUID().toString(),
                    getRandomPickupLocation(),
                    getRandomDropoffLocation(),
                    10 + random.nextDouble() * 50, // 10-60 EGP estimated fare
                    1 + random.nextInt(5), // 1-5 km distance
                    System.currentTimeMillis() - (random.nextInt(10) * 60 * 1000) // 0-10 minutes ago
            );
            
            requests.add(request);
        }
        
        return requests;
    }
    
    /**
     * Get a random pickup location in Cairo
     */
    private String getRandomPickupLocation() {
        String[] locations = {
                "Nasr City, Cairo",
                "Maadi, Cairo",
                "Downtown Cairo",
                "Giza",
                "Heliopolis, Cairo"
        };
        
        return locations[random.nextInt(locations.length)];
    }
    
    /**
     * Get a random dropoff location in Cairo
     */
    private String getRandomDropoffLocation() {
        String[] locations = {
                "Cairo International Airport",
                "Egyptian Museum",
                "Khan el-Khalili",
                "Cairo University",
                "Tahrir Square",
                "Giza Pyramids"
        };
        
        return locations[random.nextInt(locations.length)];
    }
}
