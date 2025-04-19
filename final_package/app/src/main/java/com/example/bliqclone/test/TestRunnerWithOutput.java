package com.example.bliqclone.test;

import android.content.Context;
import android.util.Log;

import com.example.bliqclone.api.ApiManager;
import com.example.bliqclone.api.ExtendedRideService;
import com.example.bliqclone.models.Ride;
import com.example.bliqclone.models.ServiceProvider;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Test runner for the Cruzo app with output to file
 */
public class TestRunnerWithOutput {
    private static final String TAG = "CruzoTestRunner";
    private static PrintWriter logWriter;
    
    public static void main(String[] args) {
        try {
            // Set up log file
            logWriter = new PrintWriter(new FileWriter("/home/ubuntu/test_output/test_results.log"));
            
            logInfo("Starting Cruzo app tests...");
            
            // Run functionality tests
            FunctionalityTest functionalityTest = new FunctionalityTest();
            boolean functionalityPassed = functionalityTest.runAllTests();
            
            // Run inDrive offer tests
            InDriveOfferTest inDriveOfferTest = new InDriveOfferTest();
            boolean inDriveOfferPassed = inDriveOfferTest.runAllTests();
            
            // Run driver mode tests
            DriverModeTest driverModeTest = new DriverModeTest();
            boolean driverModePassed = driverModeTest.runAllTests();
            
            // Print overall results
            logInfo("Test Results:");
            logInfo("Functionality Tests: " + (functionalityPassed ? "PASSED" : "FAILED"));
            logInfo("inDrive Offer Tests: " + (inDriveOfferPassed ? "PASSED" : "FAILED"));
            logInfo("Driver Mode Tests: " + (driverModePassed ? "PASSED" : "FAILED"));
            
            boolean allPassed = functionalityPassed && inDriveOfferPassed && driverModePassed;
            logInfo("Overall Result: " + (allPassed ? "PASSED" : "FAILED"));
            
            // Additional test to verify the extended ride service implementation
            testExtendedRideService();
            
            logWriter.close();
        } catch (IOException e) {
            Log.e(TAG, "Error writing to log file: " + e.getMessage());
        }
    }
    
    /**
     * Test the extended ride service implementation
     */
    private static void testExtendedRideService() {
        logInfo("Testing extended ride service implementation...");
        
        final AtomicBoolean testPassed = new AtomicBoolean(false);
        final CountDownLatch latch = new CountDownLatch(1);
        
        ExtendedRideService rideService = (ExtendedRideService) ApiManager.getInstance().getRideService();
        
        // Test searching rides by location name
        rideService.searchRides("Nasr City, Cairo", "Maadi, Cairo", new ExtendedRideService.RideServiceCallback<List<Ride>>() {
            @Override
            public void onSuccess(List<Ride> rides) {
                try {
                    logInfo("Found " + rides.size() + " rides");
                    
                    // Test sorting by price
                    List<Ride> sortedByPrice = rideService.sortRidesByPrice(rides);
                    boolean pricesSorted = true;
                    for (int i = 0; i < sortedByPrice.size() - 1; i++) {
                        if (sortedByPrice.get(i).getPrice() > sortedByPrice.get(i + 1).getPrice()) {
                            pricesSorted = false;
                            break;
                        }
                    }
                    logInfo("Prices sorted: " + pricesSorted);
                    
                    // Test sorting by ETA
                    List<Ride> sortedByEta = rideService.sortRidesByEta(rides);
                    boolean etasSorted = true;
                    for (int i = 0; i < sortedByEta.size() - 1; i++) {
                        if (sortedByEta.get(i).getEtaMinutes() > sortedByEta.get(i + 1).getEtaMinutes()) {
                            etasSorted = false;
                            break;
                        }
                    }
                    logInfo("ETAs sorted: " + etasSorted);
                    
                    // Test finding best ride
                    Ride bestRide = rideService.findBestRide(rides);
                    boolean bestRideFound = bestRide != null;
                    logInfo("Best ride found: " + bestRideFound);
                    
                    // Test inDrive offer
                    rideService.sendInDriveOffer("Nasr City, Cairo", "Maadi, Cairo", 75.0, 
                            new ExtendedRideService.RideServiceCallback<String>() {
                        @Override
                        public void onSuccess(String offerId) {
                            logInfo("inDrive offer sent, ID: " + offerId);
                            
                            // Test checking offer status
                            rideService.checkInDriveOfferStatus(offerId, 
                                    new ExtendedRideService.RideServiceCallback<ExtendedRideService.OfferStatus>() {
                                @Override
                                public void onSuccess(ExtendedRideService.OfferStatus status) {
                                    logInfo("Offer status: " + status.getStatus());
                                    
                                    // Test passed if we got this far
                                    testPassed.set(pricesSorted && etasSorted && bestRideFound);
                                    logInfo("Extended ride service test " + 
                                            (testPassed.get() ? "PASSED" : "FAILED"));
                                    latch.countDown();
                                }
                                
                                @Override
                                public void onError(String errorMessage) {
                                    logError("Failed to check offer status: " + errorMessage);
                                    latch.countDown();
                                }
                            });
                        }
                        
                        @Override
                        public void onError(String errorMessage) {
                            logError("Failed to send inDrive offer: " + errorMessage);
                            latch.countDown();
                        }
                    });
                } catch (Exception e) {
                    logError("Test failed with exception: " + e.getMessage());
                    latch.countDown();
                }
            }
            
            @Override
            public void onError(String errorMessage) {
                logError("Failed to search rides: " + errorMessage);
                latch.countDown();
            }
        });
        
        try {
            latch.await(15, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            logError("Test interrupted: " + e.getMessage());
        }
        
        logInfo("Extended ride service test result: " + (testPassed.get() ? "PASSED" : "FAILED"));
    }
    
    private static void logInfo(String message) {
        Log.i(TAG, message);
        if (logWriter != null) {
            logWriter.println("[INFO] " + message);
            logWriter.flush();
        }
    }
    
    private static void logError(String message) {
        Log.e(TAG, message);
        if (logWriter != null) {
            logWriter.println("[ERROR] " + message);
            logWriter.flush();
        }
    }
}
