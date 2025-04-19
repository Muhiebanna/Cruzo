package com.example.bliqclone.test;

import android.util.Log;

import com.example.bliqclone.api.ApiManager;
import com.example.bliqclone.api.ExtendedRideService;
import com.example.bliqclone.api.RideService;
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
 * Test class for testing the core functionality of the Cruzo app
 */
public class FunctionalityTest {
    private static final String TAG = "FunctionalityTest";
    private final ApiManager apiManager;
    private PrintWriter logWriter;
    
    public FunctionalityTest() {
        apiManager = ApiManager.getInstance();
        try {
            logWriter = new PrintWriter(new FileWriter("/home/ubuntu/test_output/functionality_test_results.log"));
        } catch (IOException e) {
            Log.e(TAG, "Error creating log file: " + e.getMessage());
        }
    }
    
    /**
     * Run all functionality tests
     * @return true if all tests pass, false otherwise
     */
    public boolean runAllTests() {
        boolean allPassed = true;
        
        logInfo("Starting Cruzo functionality tests...");
        
        // Test ride comparison
        allPassed &= testRideComparison();
        
        // Test inDrive offer system
        allPassed &= testInDriveOfferSystem();
        
        // Test driver mode
        allPassed &= testDriverMode();
        
        // Test Egyptian ride services
        allPassed &= testEgyptianRideServices();
        
        logInfo("All functionality tests " + (allPassed ? "PASSED" : "FAILED"));
        
        if (logWriter != null) {
            logWriter.close();
        }
        
        return allPassed;
    }
    
    /**
     * Test ride comparison functionality
     * @return true if test passes, false otherwise
     */
    private boolean testRideComparison() {
        logInfo("Testing ride comparison...");
        
        final AtomicBoolean testPassed = new AtomicBoolean(false);
        final CountDownLatch latch = new CountDownLatch(1);
        
        ExtendedRideService rideService = (ExtendedRideService) apiManager.getRideService();
        
        rideService.searchRides("Nasr City, Cairo", "Maadi, Cairo", new RideService.RideServiceCallback<List<Ride>>() {
            @Override
            public void onSuccess(List<Ride> rides) {
                try {
                    logInfo("Found " + rides.size() + " rides");
                    
                    // Check if we got rides from multiple providers
                    boolean hasMultipleProviders = false;
                    ServiceProvider firstProvider = null;
                    
                    if (rides.size() > 1) {
                        firstProvider = rides.get(0).getServiceProvider();
                        for (int i = 1; i < rides.size(); i++) {
                            if (rides.get(i).getServiceProvider() != firstProvider) {
                                hasMultipleProviders = true;
                                break;
                            }
                        }
                    }
                    logInfo("Multiple providers: " + hasMultipleProviders);
                    
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
                    if (bestRideFound) {
                        logInfo("Best ride: " + bestRide.getServiceProvider().getDisplayName() + 
                                " " + bestRide.getRideType() + ", " + bestRide.getPrice() + " EGP, " + 
                                bestRide.getEtaMinutes() + " min ETA");
                    }
                    
                    // Test passed if we have multiple providers, sorting works, and best ride found
                    testPassed.set(hasMultipleProviders && pricesSorted && etasSorted && bestRideFound);
                    
                    logInfo("Ride comparison test " + (testPassed.get() ? "PASSED" : "FAILED"));
                } finally {
                    latch.countDown();
                }
            }
            
            @Override
            public void onError(String errorMessage) {
                logError("Ride comparison test FAILED: " + errorMessage);
                latch.countDown();
            }
        });
        
        try {
            latch.await(10, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            logError("Test interrupted: " + e.getMessage());
            return false;
        }
        
        return testPassed.get();
    }
    
    /**
     * Test inDrive offer system
     * @return true if test passes, false otherwise
     */
    private boolean testInDriveOfferSystem() {
        logInfo("Testing inDrive offer system...");
        
        final AtomicBoolean testPassed = new AtomicBoolean(false);
        final CountDownLatch latch = new CountDownLatch(1);
        
        ExtendedRideService rideService = (ExtendedRideService) apiManager.getRideService();
        
        // Test sending an offer
        rideService.sendInDriveOffer("Nasr City, Cairo", "Maadi, Cairo", 75.0, new RideService.RideServiceCallback<String>() {
            @Override
            public void onSuccess(String offerId) {
                logInfo("Offer sent successfully, ID: " + offerId);
                
                // Test checking offer status
                rideService.checkInDriveOfferStatus(offerId, new RideService.RideServiceCallback<ExtendedRideService.OfferStatus>() {
                    @Override
                    public void onSuccess(ExtendedRideService.OfferStatus status) {
                        logInfo("Offer status: " + status.getStatus());
                        
                        // Test responding to counter-offer
                        if (status.getStatus().equals("counter_offer")) {
                            logInfo("Counter-offer amount: " + status.getCounterOfferAmount());
                            
                            rideService.respondToCounterOffer(offerId, true, new RideService.RideServiceCallback<Boolean>() {
                                @Override
                                public void onSuccess(Boolean result) {
                                    logInfo("Counter-offer accepted successfully");
                                    testPassed.set(true);
                                    latch.countDown();
                                }
                                
                                @Override
                                public void onError(String errorMessage) {
                                    logError("Failed to respond to counter-offer: " + errorMessage);
                                    latch.countDown();
                                }
                            });
                        } else {
                            // If no counter-offer, test still passes
                            logInfo("No counter-offer received, test passes by default");
                            testPassed.set(true);
                            latch.countDown();
                        }
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
        
        try {
            latch.await(15, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            logError("Test interrupted: " + e.getMessage());
            return false;
        }
        
        logInfo("inDrive offer system test " + (testPassed.get() ? "PASSED" : "FAILED"));
        return testPassed.get();
    }
    
    /**
     * Test driver mode functionality
     * @return true if test passes, false otherwise
     */
    private boolean testDriverMode() {
        logInfo("Testing driver mode...");
        
        final AtomicBoolean testPassed = new AtomicBoolean(false);
        final CountDownLatch latch = new CountDownLatch(1);
        
        apiManager.getDriverService().getAvailableRequests(new com.example.bliqclone.api.DriverService.DriverServiceCallback<List<com.example.bliqclone.models.RideRequest>>() {
            @Override
            public void onSuccess(List<com.example.bliqclone.models.RideRequest> requests) {
                logInfo("Got " + requests.size() + " available requests");
                
                // Test getting earnings data
                apiManager.getDriverService().getEarnings("week", new com.example.bliqclone.api.DriverService.DriverServiceCallback<com.example.bliqclone.api.DriverService.EarningsData>() {
                    @Override
                    public void onSuccess(com.example.bliqclone.api.DriverService.EarningsData data) {
                        logInfo("Got earnings data: " + data.getTotalEarnings() + " EGP, " + 
                                data.getTotalRides() + " rides, " + data.getAverageRating() + " average rating");
                        
                        // Test accepting a request if available
                        if (!requests.isEmpty()) {
                            apiManager.getDriverService().acceptRideRequest(requests.get(0), new com.example.bliqclone.api.DriverService.DriverServiceCallback<Boolean>() {
                                @Override
                                public void onSuccess(Boolean result) {
                                    logInfo("Request accepted successfully");
                                    testPassed.set(true);
                                    latch.countDown();
                                }
                                
                                @Override
                                public void onError(String errorMessage) {
                                    logError("Failed to accept ride request: " + errorMessage);
                                    latch.countDown();
                                }
                            });
                        } else {
                            // If no requests, test still passes
                            logInfo("No requests available, test passes by default");
                            testPassed.set(true);
                            latch.countDown();
                        }
                    }
                    
                    @Override
                    public void onError(String errorMessage) {
                        logError("Failed to get earnings data: " + errorMessage);
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
        
        logInfo("Driver mode test " + (testPassed.get() ? "PASSED" : "FAILED"));
        return testPassed.get();
    }
    
    /**
     * Test Egyptian ride services
     * @return true if test passes, false otherwise
     */
    private boolean testEgyptianRideServices() {
        logInfo("Testing Egyptian ride services...");
        
        final AtomicBoolean testPassed = new AtomicBoolean(false);
        final CountDownLatch latch = new CountDownLatch(1);
        
        ExtendedRideService rideService = (ExtendedRideService) apiManager.getRideService();
        
        // Test searching rides with all Egyptian services
        rideService.searchRides("Nasr City, Cairo", "Maadi, Cairo", new RideService.RideServiceCallback<List<Ride>>() {
            @Override
            public void onSuccess(List<Ride> rides) {
                // Check if we have rides from all Egyptian services
                boolean hasUber = false;
                boolean hasBolt = false;
                boolean hasCareem = false;
                boolean hasInDrive = false;
                boolean hasSwvl = false;
                boolean hasDidi = false;
                boolean hasHalan = false;
                
                for (Ride ride : rides) {
                    ServiceProvider provider = ride.getServiceProvider();
                    switch (provider) {
                        case UBER:
                            hasUber = true;
                            break;
                        case BOLT:
                            hasBolt = true;
                            break;
                        case CAREEM:
                            hasCareem = true;
                            break;
                        case INDRIVE:
                            hasInDrive = true;
                            break;
                        case SWVL:
                            hasSwvl = true;
                            break;
                        case DIDI:
                            hasDidi = true;
                            break;
                        case HALAN:
                            hasHalan = true;
                            break;
                    }
                }
                
                // Test passes if we have at least 4 of the 7 Egyptian services
                int count = 0;
                if (hasUber) count++;
                if (hasBolt) count++;
                if (hasCareem) count++;
                if (hasInDrive) count++;
                if (hasSwvl) count++;
                if (hasDidi) count++;
                if (hasHalan) count++;
                
                testPassed.set(count >= 4);
                
                logInfo("Egyptian ride services test " + (testPassed.get() ? "PASSED" : "FAILED"));
                logInfo("Services found: " + count + "/7");
                logInfo("Uber: " + hasUber);
                logInfo("Bolt: " + hasBolt);
                logInfo("Careem: " + hasCareem);
                logInfo("inDrive: " + hasInDrive);
                logInfo("Swvl: " + hasSwvl);
                logInfo("DiDi: " + hasDidi);
                logInfo("Halan: " + hasHalan);
                
                latch.countDown();
            }
            
            @Override
            public void onError(String errorMessage) {
                logError("Failed to search rides: " + errorMessage);
                latch.countDown();
            }
        });
        
        try {
            latch.await(10, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            logError("Test interrupted: " + e.getMessage());
            return false;
        }
        
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
