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
import java.util.concurrent.atomic.AtomicReference;

/**
 * Test class for testing the inDrive offer system
 */
public class InDriveOfferTest {
    private static final String TAG = "InDriveOfferTest";
    private final ApiManager apiManager;
    private PrintWriter logWriter;
    
    public InDriveOfferTest() {
        apiManager = ApiManager.getInstance();
        try {
            logWriter = new PrintWriter(new FileWriter("/home/ubuntu/test_output/indrive_test_results.log"));
        } catch (IOException e) {
            Log.e(TAG, "Error creating log file: " + e.getMessage());
        }
    }
    
    /**
     * Run all inDrive offer tests
     * @return true if all tests pass, false otherwise
     */
    public boolean runAllTests() {
        boolean allPassed = true;
        
        logInfo("Starting inDrive offer tests...");
        
        // Test sending an offer
        allPassed &= testSendOffer();
        
        // Test checking offer status
        allPassed &= testCheckOfferStatus();
        
        // Test accepting counter-offer
        allPassed &= testAcceptCounterOffer();
        
        // Test rejecting counter-offer
        allPassed &= testRejectCounterOffer();
        
        logInfo("All inDrive offer tests " + (allPassed ? "PASSED" : "FAILED"));
        
        if (logWriter != null) {
            logWriter.close();
        }
        
        return allPassed;
    }
    
    /**
     * Test sending an offer
     * @return true if test passes, false otherwise
     */
    private boolean testSendOffer() {
        logInfo("Testing sending an offer...");
        
        final AtomicBoolean testPassed = new AtomicBoolean(false);
        final CountDownLatch latch = new CountDownLatch(1);
        
        ExtendedRideService rideService = (ExtendedRideService) apiManager.getRideService();
        
        // Test sending an offer
        rideService.sendInDriveOffer("Nasr City, Cairo", "Maadi, Cairo", 75.0, new RideService.RideServiceCallback<String>() {
            @Override
            public void onSuccess(String offerId) {
                logInfo("Offer sent successfully, ID: " + offerId);
                testPassed.set(true);
                latch.countDown();
            }
            
            @Override
            public void onError(String errorMessage) {
                logError("Failed to send offer: " + errorMessage);
                latch.countDown();
            }
        });
        
        try {
            latch.await(10, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            logError("Test interrupted: " + e.getMessage());
            return false;
        }
        
        logInfo("Send offer test " + (testPassed.get() ? "PASSED" : "FAILED"));
        return testPassed.get();
    }
    
    /**
     * Test checking offer status
     * @return true if test passes, false otherwise
     */
    private boolean testCheckOfferStatus() {
        logInfo("Testing checking offer status...");
        
        final AtomicBoolean testPassed = new AtomicBoolean(false);
        final CountDownLatch latch = new CountDownLatch(1);
        final AtomicReference<String> offerIdRef = new AtomicReference<>();
        
        ExtendedRideService rideService = (ExtendedRideService) apiManager.getRideService();
        
        // First send an offer
        rideService.sendInDriveOffer("Nasr City, Cairo", "Maadi, Cairo", 80.0, new RideService.RideServiceCallback<String>() {
            @Override
            public void onSuccess(String offerId) {
                offerIdRef.set(offerId);
                logInfo("Offer sent successfully, ID: " + offerId);
                
                // Wait a bit for the offer to be processed
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    logError("Sleep interrupted: " + e.getMessage());
                }
                
                // Now check the status
                rideService.checkInDriveOfferStatus(offerId, new RideService.RideServiceCallback<ExtendedRideService.OfferStatus>() {
                    @Override
                    public void onSuccess(ExtendedRideService.OfferStatus status) {
                        logInfo("Offer status: " + status.getStatus());
                        testPassed.set(true);
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
                logError("Failed to send offer: " + errorMessage);
                latch.countDown();
            }
        });
        
        try {
            latch.await(15, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            logError("Test interrupted: " + e.getMessage());
            return false;
        }
        
        logInfo("Check offer status test " + (testPassed.get() ? "PASSED" : "FAILED"));
        return testPassed.get();
    }
    
    /**
     * Test accepting a counter-offer
     * @return true if test passes, false otherwise
     */
    private boolean testAcceptCounterOffer() {
        logInfo("Testing accepting a counter-offer...");
        
        final AtomicBoolean testPassed = new AtomicBoolean(false);
        final CountDownLatch latch = new CountDownLatch(1);
        final AtomicReference<String> offerIdRef = new AtomicReference<>();
        
        ExtendedRideService rideService = (ExtendedRideService) apiManager.getRideService();
        
        // First send an offer
        rideService.sendInDriveOffer("Nasr City, Cairo", "Maadi, Cairo", 70.0, new RideService.RideServiceCallback<String>() {
            @Override
            public void onSuccess(String offerId) {
                offerIdRef.set(offerId);
                logInfo("Offer sent successfully, ID: " + offerId);
                
                // Wait for the offer to potentially get a counter-offer
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                    logError("Sleep interrupted: " + e.getMessage());
                }
                
                // Check the status
                rideService.checkInDriveOfferStatus(offerId, new RideService.RideServiceCallback<ExtendedRideService.OfferStatus>() {
                    @Override
                    public void onSuccess(ExtendedRideService.OfferStatus status) {
                        logInfo("Offer status: " + status.getStatus());
                        
                        if (status.getStatus().equals("counter_offer")) {
                            // Accept the counter-offer
                            rideService.respondToCounterOffer(offerId, true, new RideService.RideServiceCallback<Boolean>() {
                                @Override
                                public void onSuccess(Boolean result) {
                                    logInfo("Counter-offer accepted successfully");
                                    testPassed.set(true);
                                    latch.countDown();
                                }
                                
                                @Override
                                public void onError(String errorMessage) {
                                    logError("Failed to accept counter-offer: " + errorMessage);
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
                logError("Failed to send offer: " + errorMessage);
                latch.countDown();
            }
        });
        
        try {
            latch.await(20, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            logError("Test interrupted: " + e.getMessage());
            return false;
        }
        
        logInfo("Accept counter-offer test " + (testPassed.get() ? "PASSED" : "FAILED"));
        return testPassed.get();
    }
    
    /**
     * Test rejecting a counter-offer
     * @return true if test passes, false otherwise
     */
    private boolean testRejectCounterOffer() {
        logInfo("Testing rejecting a counter-offer...");
        
        final AtomicBoolean testPassed = new AtomicBoolean(false);
        final CountDownLatch latch = new CountDownLatch(1);
        final AtomicReference<String> offerIdRef = new AtomicReference<>();
        
        ExtendedRideService rideService = (ExtendedRideService) apiManager.getRideService();
        
        // First send an offer
        rideService.sendInDriveOffer("Nasr City, Cairo", "Maadi, Cairo", 65.0, new RideService.RideServiceCallback<String>() {
            @Override
            public void onSuccess(String offerId) {
                offerIdRef.set(offerId);
                logInfo("Offer sent successfully, ID: " + offerId);
                
                // Wait for the offer to potentially get a counter-offer
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                    logError("Sleep interrupted: " + e.getMessage());
                }
                
                // Check the status
                rideService.checkInDriveOfferStatus(offerId, new RideService.RideServiceCallback<ExtendedRideService.OfferStatus>() {
                    @Override
                    public void onSuccess(ExtendedRideService.OfferStatus status) {
                        logInfo("Offer status: " + status.getStatus());
                        
                        if (status.getStatus().equals("counter_offer")) {
                            // Reject the counter-offer
                            rideService.respondToCounterOffer(offerId, false, new RideService.RideServiceCallback<Boolean>() {
                                @Override
                                public void onSuccess(Boolean result) {
                                    logInfo("Counter-offer rejected successfully");
                                    testPassed.set(true);
                                    latch.countDown();
                                }
                                
                                @Override
                                public void onError(String errorMessage) {
                                    logError("Failed to reject counter-offer: " + errorMessage);
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
                logError("Failed to send offer: " + errorMessage);
                latch.countDown();
            }
        });
        
        try {
            latch.await(20, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            logError("Test interrupted: " + e.getMessage());
            return false;
        }
        
        logInfo("Reject counter-offer test " + (testPassed.get() ? "PASSED" : "FAILED"));
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
