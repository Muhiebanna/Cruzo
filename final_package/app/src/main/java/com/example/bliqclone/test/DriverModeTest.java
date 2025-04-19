package com.example.bliqclone.test;

import android.util.Log;

import com.example.bliqclone.api.ApiManager;
import com.example.bliqclone.api.DriverService;
import com.example.bliqclone.models.EarningRecord;
import com.example.bliqclone.models.RideRequest;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Test class for testing the driver mode functionality
 */
public class DriverModeTest {
    private static final String TAG = "DriverModeTest";
    private final ApiManager apiManager;
    private PrintWriter logWriter;
    
    public DriverModeTest() {
        apiManager = ApiManager.getInstance();
        try {
            logWriter = new PrintWriter(new FileWriter("/home/ubuntu/test_output/driver_mode_test_results.log"));
        } catch (IOException e) {
            Log.e(TAG, "Error creating log file: " + e.getMessage());
        }
    }
    
    /**
     * Run all driver mode tests
     * @return true if all tests pass, false otherwise
     */
    public boolean runAllTests() {
        boolean allPassed = true;
        
        logInfo("Starting driver mode tests...");
        
        // Test getting available requests
        allPassed &= testGetAvailableRequests();
        
        // Test accepting a request
        allPassed &= testAcceptRequest();
        
        // Test rejecting a request
        allPassed &= testRejectRequest();
        
        // Test getting earnings data
        allPassed &= testGetEarnings();
        
        logInfo("All driver mode tests " + (allPassed ? "PASSED" : "FAILED"));
        
        if (logWriter != null) {
            logWriter.close();
        }
        
        return allPassed;
    }
    
    /**
     * Test getting available requests
     * @return true if test passes, false otherwise
     */
    private boolean testGetAvailableRequests() {
        logInfo("Testing getting available requests...");
        
        final AtomicBoolean testPassed = new AtomicBoolean(false);
        final CountDownLatch latch = new CountDownLatch(1);
        
        DriverService driverService = apiManager.getDriverService();
        
        driverService.getAvailableRequests(new DriverService.DriverServiceCallback<List<RideRequest>>() {
            @Override
            public void onSuccess(List<RideRequest> requests) {
                logInfo("Got " + requests.size() + " available requests");
                
                // Log details of each request
                for (int i = 0; i < requests.size(); i++) {
                    RideRequest request = requests.get(i);
                    logInfo("Request " + (i + 1) + ": " + request.getPickupLocation() + " to " + 
                            request.getDropoffLocation() + ", estimated fare: " + request.getEstimatedFare());
                }
                
                testPassed.set(true);
                latch.countDown();
            }
            
            @Override
            public void onError(String errorMessage) {
                logError("Failed to get available requests: " + errorMessage);
                latch.countDown();
            }
        });
        
        try {
            latch.await(10, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            logError("Test interrupted: " + e.getMessage());
            return false;
        }
        
        logInfo("Get available requests test " + (testPassed.get() ? "PASSED" : "FAILED"));
        return testPassed.get();
    }
    
    /**
     * Test accepting a request
     * @return true if test passes, false otherwise
     */
    private boolean testAcceptRequest() {
        logInfo("Testing accepting a request...");
        
        final AtomicBoolean testPassed = new AtomicBoolean(false);
        final CountDownLatch latch = new CountDownLatch(1);
        
        DriverService driverService = apiManager.getDriverService();
        
        driverService.getAvailableRequests(new DriverService.DriverServiceCallback<List<RideRequest>>() {
            @Override
            public void onSuccess(List<RideRequest> requests) {
                if (requests.isEmpty()) {
                    logInfo("No requests available to accept, test passes by default");
                    testPassed.set(true);
                    latch.countDown();
                    return;
                }
                
                // Accept the first request
                RideRequest request = requests.get(0);
                logInfo("Accepting request: " + request.getPickupLocation() + " to " + request.getDropoffLocation());
                
                driverService.acceptRideRequest(request, new DriverService.DriverServiceCallback<Boolean>() {
                    @Override
                    public void onSuccess(Boolean result) {
                        logInfo("Request accepted successfully");
                        testPassed.set(true);
                        latch.countDown();
                    }
                    
                    @Override
                    public void onError(String errorMessage) {
                        logError("Failed to accept request: " + errorMessage);
                        latch.countDown();
                    }
                });
            }
            
            @Override
            public void onError(String errorMessage) {
                logError("Failed to get available requests: " + errorMessage);
                latch.countDown();
            }
        });
        
        try {
            latch.await(15, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            logError("Test interrupted: " + e.getMessage());
            return false;
        }
        
        logInfo("Accept request test " + (testPassed.get() ? "PASSED" : "FAILED"));
        return testPassed.get();
    }
    
    /**
     * Test rejecting a request
     * @return true if test passes, false otherwise
     */
    private boolean testRejectRequest() {
        logInfo("Testing rejecting a request...");
        
        final AtomicBoolean testPassed = new AtomicBoolean(false);
        final CountDownLatch latch = new CountDownLatch(1);
        
        DriverService driverService = apiManager.getDriverService();
        
        driverService.getAvailableRequests(new DriverService.DriverServiceCallback<List<RideRequest>>() {
            @Override
            public void onSuccess(List<RideRequest> requests) {
                if (requests.isEmpty()) {
                    logInfo("No requests available to reject, test passes by default");
                    testPassed.set(true);
                    latch.countDown();
                    return;
                }
                
                // Reject the first request
                RideRequest request = requests.get(0);
                logInfo("Rejecting request: " + request.getPickupLocation() + " to " + request.getDropoffLocation());
                
                driverService.rejectRideRequest(request, new DriverService.DriverServiceCallback<Boolean>() {
                    @Override
                    public void onSuccess(Boolean result) {
                        logInfo("Request rejected successfully");
                        testPassed.set(true);
                        latch.countDown();
                    }
                    
                    @Override
                    public void onError(String errorMessage) {
                        logError("Failed to reject request: " + errorMessage);
                        latch.countDown();
                    }
                });
            }
            
            @Override
            public void onError(String errorMessage) {
                logError("Failed to get available requests: " + errorMessage);
                latch.countDown();
            }
        });
        
        try {
            latch.await(15, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            logError("Test interrupted: " + e.getMessage());
            return false;
        }
        
        logInfo("Reject request test " + (testPassed.get() ? "PASSED" : "FAILED"));
        return testPassed.get();
    }
    
    /**
     * Test getting earnings data
     * @return true if test passes, false otherwise
     */
    private boolean testGetEarnings() {
        logInfo("Testing getting earnings data...");
        
        final AtomicBoolean testPassed = new AtomicBoolean(false);
        final CountDownLatch latch = new CountDownLatch(1);
        
        DriverService driverService = apiManager.getDriverService();
        
        // Test getting weekly earnings
        driverService.getEarnings("week", new DriverService.DriverServiceCallback<DriverService.EarningsData>() {
            @Override
            public void onSuccess(DriverService.EarningsData data) {
                logInfo("Got earnings data: " + data.getTotalEarnings() + " EGP, " + 
                        data.getTotalRides() + " rides, " + data.getAverageRating() + " average rating");
                
                List<EarningRecord> records = data.getRecords();
                logInfo("Earnings records: " + records.size());
                
                // Log details of some records
                int numToLog = Math.min(5, records.size());
                for (int i = 0; i < numToLog; i++) {
                    EarningRecord record = records.get(i);
                    logInfo("Record " + (i + 1) + ": " + record.getAmount() + " EGP, " + 
                            record.getPickupLocation() + " to " + record.getDropoffLocation() + 
                            ", rating: " + record.getRating());
                }
                
                testPassed.set(true);
                latch.countDown();
            }
            
            @Override
            public void onError(String errorMessage) {
                logError("Failed to get earnings data: " + errorMessage);
                latch.countDown();
            }
        });
        
        try {
            latch.await(10, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            logError("Test interrupted: " + e.getMessage());
            return false;
        }
        
        logInfo("Get earnings test " + (testPassed.get() ? "PASSED" : "FAILED"));
        return testPassed.get();
    }
    
    private void logInfo(String message) {
        Log.i(TAG, message);
        if (logWriter != null) {
            logWriter.println("[INFO] " + message);
            logWriter.flush();
        }
    }
    
    private void logError(String message) {
        Log.e(TAG, message);
        if (logWriter != null) {
            logWriter.println("[ERROR] " + message);
            logWriter.flush();
        }
    }
}
