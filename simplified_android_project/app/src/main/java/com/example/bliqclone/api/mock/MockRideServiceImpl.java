package com.example.bliqclone.api.mock;

import android.os.Handler;
import android.os.Looper;

import com.example.bliqclone.api.RideService;
import com.example.bliqclone.models.Ride;
import com.example.bliqclone.models.ServiceProvider;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Mock implementation of the RideService interface
 */
public class MockRideServiceImpl implements RideService {
    private static final Random random = new Random();
    private final Map<String, OfferInfo> activeOffers = new HashMap<>();
    private final Handler handler = new Handler(Looper.getMainLooper());
    
    @Override
    public void getAvailableRides(double startLat, double startLng, double endLat, double endLng, RideServiceCallback<List<Ride>> callback) {
        // Simulate network delay
        handler.postDelayed(() -> {
            try {
                List<Ride> rides = MockRideService.getAvailableRides(startLat, startLng, endLat, endLng);
                callback.onSuccess(rides);
            } catch (Exception e) {
                callback.onError("Failed to get available rides: " + e.getMessage());
            }
        }, 1000 + random.nextInt(1000));
    }
    
    @Override
    public void getAvailableRidesFromProvider(ServiceProvider provider, double startLat, double startLng, 
                                             double endLat, double endLng, RideServiceCallback<List<Ride>> callback) {
        // Simulate network delay
        handler.postDelayed(() -> {
            try {
                List<Ride> allRides = MockRideService.getAvailableRides(startLat, startLng, endLat, endLng);
                List<Ride> filteredRides = allRides.stream()
                        .filter(ride -> ride.getServiceProvider() == provider)
                        .collect(Collectors.toList());
                callback.onSuccess(filteredRides);
            } catch (Exception e) {
                callback.onError("Failed to get rides from " + provider.getDisplayName() + ": " + e.getMessage());
            }
        }, 800 + random.nextInt(700));
    }
    
    @Override
    public void bookRide(Ride ride, RideServiceCallback<Boolean> callback) {
        // Simulate network delay
        handler.postDelayed(() -> {
            try {
                // 95% success rate for booking
                boolean success = random.nextDouble() < 0.95;
                if (success) {
                    callback.onSuccess(true);
                } else {
                    callback.onError("Booking failed. Please try again.");
                }
            } catch (Exception e) {
                callback.onError("Failed to book ride: " + e.getMessage());
            }
        }, 1500 + random.nextInt(1000));
    }
    
    @Override
    public void sendOffer(Ride ride, double offerAmount, RideServiceCallback<Boolean> callback) {
        if (ride.getServiceProvider() != ServiceProvider.INDRIVE) {
            callback.onError("Offers are only supported for inDrive rides");
            return;
        }
        
        // Simulate network delay
        handler.postDelayed(() -> {
            try {
                // Create a new offer
                String offerId = UUID.randomUUID().toString();
                OfferInfo offerInfo = new OfferInfo(ride.getId(), offerAmount);
                activeOffers.put(offerId, offerInfo);
                
                // Update the ride with the offer amount
                ride.setUserOffer(offerAmount);
                
                callback.onSuccess(true);
                
                // Simulate driver responses after some time
                simulateDriverResponses(offerId, ride);
            } catch (Exception e) {
                callback.onError("Failed to send offer: " + e.getMessage());
            }
        }, 1000 + random.nextInt(800));
    }
    
    @Override
    public void getOfferStatus(Ride ride, RideServiceCallback<OfferStatus> callback) {
        // Find the offer for this ride
        String offerId = null;
        for (Map.Entry<String, OfferInfo> entry : activeOffers.entrySet()) {
            if (entry.getValue().rideId.equals(ride.getId())) {
                offerId = entry.getKey();
                break;
            }
        }
        
        if (offerId == null) {
            callback.onError("No active offer found for this ride");
            return;
        }
        
        final String finalOfferId = offerId;
        
        // Simulate network delay
        handler.postDelayed(() -> {
            try {
                OfferInfo offerInfo = activeOffers.get(finalOfferId);
                callback.onSuccess(offerInfo.status);
            } catch (Exception e) {
                callback.onError("Failed to get offer status: " + e.getMessage());
            }
        }, 500 + random.nextInt(500));
    }
    
    @Override
    public void acceptCounterOffer(Ride ride, double counterOfferAmount, RideServiceCallback<Boolean> callback) {
        // Find the offer for this ride
        String offerId = null;
        for (Map.Entry<String, OfferInfo> entry : activeOffers.entrySet()) {
            if (entry.getValue().rideId.equals(ride.getId())) {
                offerId = entry.getKey();
                break;
            }
        }
        
        if (offerId == null) {
            callback.onError("No active offer found for this ride");
            return;
        }
        
        final String finalOfferId = offerId;
        
        // Simulate network delay
        handler.postDelayed(() -> {
            try {
                OfferInfo offerInfo = activeOffers.get(finalOfferId);
                
                if (offerInfo.status != OfferStatus.COUNTER_OFFERED) {
                    callback.onError("This offer is not in counter-offered state");
                    return;
                }
                
                // Update the offer
                offerInfo.offerAmount = counterOfferAmount;
                offerInfo.status = OfferStatus.ACCEPTED;
                
                // Update the ride price
                ride.setPrice(counterOfferAmount);
                
                callback.onSuccess(true);
            } catch (Exception e) {
                callback.onError("Failed to accept counter-offer: " + e.getMessage());
            }
        }, 1000 + random.nextInt(800));
    }
    
    @Override
    public void rejectCounterOffer(Ride ride, RideServiceCallback<Boolean> callback) {
        // Find the offer for this ride
        String offerId = null;
        for (Map.Entry<String, OfferInfo> entry : activeOffers.entrySet()) {
            if (entry.getValue().rideId.equals(ride.getId())) {
                offerId = entry.getKey();
                break;
            }
        }
        
        if (offerId == null) {
            callback.onError("No active offer found for this ride");
            return;
        }
        
        final String finalOfferId = offerId;
        
        // Simulate network delay
        handler.postDelayed(() -> {
            try {
                OfferInfo offerInfo = activeOffers.get(finalOfferId);
                
                if (offerInfo.status != OfferStatus.COUNTER_OFFERED) {
                    callback.onError("This offer is not in counter-offered state");
                    return;
                }
                
                // Update the offer
                offerInfo.status = OfferStatus.REJECTED;
                
                callback.onSuccess(true);
                
                // Remove the offer after some time
                handler.postDelayed(() -> activeOffers.remove(finalOfferId), 5000);
            } catch (Exception e) {
                callback.onError("Failed to reject counter-offer: " + e.getMessage());
            }
        }, 800 + random.nextInt(700));
    }
    
    /**
     * Simulate driver responses to an offer
     */
    private void simulateDriverResponses(String offerId, Ride ride) {
        // Wait for 3-8 seconds before driver response
        handler.postDelayed(() -> {
            OfferInfo offerInfo = activeOffers.get(offerId);
            if (offerInfo == null) return;
            
            // Determine driver response
            double responseType = random.nextDouble();
            
            if (responseType < 0.4) {
                // 40% chance: Driver accepts the offer
                offerInfo.status = OfferStatus.ACCEPTED;
                ride.setPrice(offerInfo.offerAmount);
            } else if (responseType < 0.8) {
                // 40% chance: Driver counter-offers
                double counterOffer = offerInfo.offerAmount * (1.1 + random.nextDouble() * 0.2); // 10-30% higher
                offerInfo.counterOfferAmount = counterOffer;
                offerInfo.status = OfferStatus.COUNTER_OFFERED;
            } else {
                // 20% chance: Driver rejects the offer
                offerInfo.status = OfferStatus.REJECTED;
                
                // Remove the offer after some time
                handler.postDelayed(() -> activeOffers.remove(offerId), 5000);
            }
        }, 3000 + random.nextInt(5000));
    }
    
    /**
     * Class to store information about an active offer
     */
    private static class OfferInfo {
        String rideId;
        double offerAmount;
        double counterOfferAmount;
        OfferStatus status;
        
        OfferInfo(String rideId, double offerAmount) {
            this.rideId = rideId;
            this.offerAmount = offerAmount;
            this.status = OfferStatus.PENDING;
        }
    }
}
