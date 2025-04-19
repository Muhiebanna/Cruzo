package com.example.bliqclone.test;

import android.util.Log;

import com.example.bliqclone.api.ApiManager;
import com.example.bliqclone.api.ExtendedRideService;
import com.example.bliqclone.models.Ride;
import com.example.bliqclone.models.ServiceProvider;

import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Test runner for the Cruzo app
 */
public class TestRunner {
    private static final String TAG = "CruzoTestRunner";
    
    public static void main(String[] args) {
        Log.i(TAG, "Starting Cruzo app tests...");
        
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
        Log.i(TAG, "Test Results:");
        Log.i(TAG, "Functionality Tests: " + (functionalityPassed ? "PASSED" : "FAILED"));
        Log.i(TAG, "inDrive Offer Tests: " + (inDriveOfferPassed ? "PASSED" : "FAILED"));
        Log.i(TAG, "Driver Mode Tests: " + (driverModePassed ? "PASSED" : "FAILED"));
        
        boolean allPassed = functionalityPassed && inDriveOfferPassed && driverModePassed;
        Log.i(TAG, "Overall Result: " + (allPassed ? "PASSED" : "FAILED"));
        
        // Additional test to verify the extended ride service implementation
        testExtendedRideService();
    }
    
    /**
     * Test the extended ride service implementation
     */
    private static void testExtendedRideService() {
        Log.i(TAG, "Testing extended ride service implementation...");
        
        final AtomicBoolean testPassed = new AtomicBoolean(false);
        final CountDownLatch latch = new CountDownLatch(1);
        
        ExtendedRideService rideService = (ExtendedRideService) ApiManager.getInstance().getRideService();
        
        // Test searching rides by location name
        rideService.searchRides("Nasr City, Cairo", "Maadi, Cairo", new ExtendedRideService.RideServiceCallback<List<Ride>>() {
            @Override
            public void onSuccess(List<Ride> rides) {
                try {
                    // Test sorting by price
                    List<Ride> sortedByPrice = rideService.sortRidesByPrice(rides);
                    boolean pricesSorted = true;
                    for (int i = 0; i < sortedByPrice.size() - 1; i++) {
                        if (sortedByPrice.get(i).getPrice() > sortedByPrice.get(i + 1).getPrice()) {
                            pricesSorted = false;
                            break;
                        }
                    }
                    
                    // Test sorting by ETA
                    List<Ride> sortedByEta = rideService.sortRidesByEta(rides);
                    boolean etasSorted = true;
                    for (int i = 0; i < sortedByEta.size() - 1; i++) {
                        if (sortedByEta.get(i).getEtaMinutes() > sortedByEta.get(i + 1).getEtaMinutes()) {
                            etasSorted = false;
                            break;
                        }
                    }
                    
                    // Test finding best ride
                    Ride bestRide = rideService.findBestRide(rides);
                    boolean bestRideFound = bestRide != null;
                    
                    // Test inDrive offer
                    rideService.sendInDriveOffer("Nasr City, Cairo", "Maadi, Cairo", 75.0, 
                            new ExtendedRideService.RideServiceCallback<String>() {
                        @Override
                        public void onSuccess(String offerId) {
                            // Test checking offer status
                            rideService.checkInDriveOfferStatus(offerId, 
                                    new ExtendedRideService.RideServiceCallback<ExtendedRideService.OfferStatus>() {
                                @Override
                                public void onSuccess(ExtendedRideService.OfferStatus status) {
                                    // Test passed if we got this far
                                    testPassed.set(pricesSorted && etasSorted && bestRideFound);
                                    Log.i(TAG, "Extended ride service test " + 
                                            (testPassed.get() ? "PASSED" : "FAILED"));
                                    latch.countDown();
                                }
                                
                                @Override
                                public void onError(String errorMessage) {
                                    Log.e(TAG, "Failed to check offer status: " + errorMessage);
                                    latch.countDown();
                                }
                            });
                        }
                        
                        @Override
                        public void onError(String errorMessage) {
                            Log.e(TAG, "Failed to send inDrive offer: " + errorMessage);
                            latch.countDown();
                        }
                    });
                } catch (Exception e) {
                    Log.e(TAG, "Test failed with exception: " + e.getMessage());
                    latch.countDown();
                }
            }
            
            @Override
            public void onError(String errorMessage) {
                Log.e(TAG, "Failed to search rides: " + errorMessage);
                latch.countDown();
            }
        });
        
        try {
            latch.await(15, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            Log.e(TAG, "Test interrupted", e);
        }
        
        Log.i(TAG, "Extended ride service test result: " + (testPassed.get() ? "PASSED" : "FAILED"));
    }
}
