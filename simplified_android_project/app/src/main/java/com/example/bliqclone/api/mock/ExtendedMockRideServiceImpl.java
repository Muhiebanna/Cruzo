package com.example.bliqclone.api.mock;

import android.os.Handler;
import android.os.Looper;

import com.example.bliqclone.api.ExtendedRideService;
import com.example.bliqclone.models.Ride;
import com.example.bliqclone.models.ServiceProvider;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Extended mock implementation of the RideService interface
 */
public class ExtendedMockRideServiceImpl extends MockRideServiceImpl implements ExtendedRideService {
    private static final Random random = new Random();
    private final Handler handler = new Handler(Looper.getMainLooper());
    private final Map<String, OfferInfo> activeOffersByOfferId = new HashMap<>();
    
    @Override
    public void searchRides(String startLocation, String endLocation, RideServiceCallback<List<Ride>> callback) {
        // Convert location names to coordinates (mock implementation)
        double[] startCoords = convertLocationToCoordinates(startLocation);
        double[] endCoords = convertLocationToCoordinates(endLocation);
        
        // Call the original method with coordinates
        getAvailableRides(startCoords[0], startCoords[1], endCoords[0], endCoords[1], callback);
    }
    
    @Override
    public List<Ride> sortRidesByPrice(List<Ride> rides) {
        List<Ride> sortedRides = new ArrayList<>(rides);
        Collections.sort(sortedRides, Comparator.comparingDouble(Ride::getPrice));
        return sortedRides;
    }
    
    @Override
    public List<Ride> sortRidesByEta(List<Ride> rides) {
        List<Ride> sortedRides = new ArrayList<>(rides);
        Collections.sort(sortedRides, Comparator.comparingInt(Ride::getEtaMinutes));
        return sortedRides;
    }
    
    @Override
    public Ride findBestRide(List<Ride> rides) {
        if (rides == null || rides.isEmpty()) {
            return null;
        }
        
        // Calculate a score for each ride based on price, ETA, and rating
        Ride bestRide = null;
        double bestScore = Double.MAX_VALUE;
        
        // Find average price and ETA
        double totalPrice = 0;
        int totalEta = 0;
        for (Ride ride : rides) {
            totalPrice += ride.getPrice();
            totalEta += ride.getEtaMinutes();
        }
        double avgPrice = totalPrice / rides.size();
        double avgEta = totalEta / rides.size();
        
        for (Ride ride : rides) {
            // Normalize price and ETA relative to average
            double priceScore = ride.getPrice() / avgPrice;
            double etaScore = ride.getEtaMinutes() / avgEta;
            
            // Invert rating so lower is better (like price and ETA)
            double ratingScore = (5.0 - ride.getDriverRating()) / 5.0;
            
            // Calculate weighted score (lower is better)
            double score = 0.5 * priceScore + 0.3 * etaScore + 0.2 * ratingScore;
            
            if (score < bestScore) {
                bestScore = score;
                bestRide = ride;
            }
        }
        
        // Mark the best ride
        if (bestRide != null) {
            bestRide.setBestValue(true);
        }
        
        return bestRide;
    }
    
    @Override
    public void sendInDriveOffer(String startLocation, String endLocation, double offerAmount, 
                               RideServiceCallback<String> callback) {
        // Convert location names to coordinates (mock implementation)
        double[] startCoords = convertLocationToCoordinates(startLocation);
        double[] endCoords = convertLocationToCoordinates(endLocation);
        
        // Simulate network delay
        handler.postDelayed(() -> {
            try {
                // Create a mock inDrive ride
                Ride inDriveRide = new Ride(
                        UUID.randomUUID().toString(),
                        ServiceProvider.INDRIVE,
                        "Economy",
                        offerAmount,
                        (int) (10 + random.nextInt(10)),
                        calculateDistance(startCoords[0], startCoords[1], endCoords[0], endCoords[1]),
                        3.5f + random.nextFloat() * 1.5f
                );
                inDriveRide.setDriverName("Hassan M.");
                inDriveRide.setVehicleModel("Chevrolet Aveo");
                inDriveRide.setVehiclePlate("YZA 567");
                inDriveRide.setOfferBased(true);
                inDriveRide.setSuggestedPrice(offerAmount);
                inDriveRide.setUserOffer(offerAmount);
                
                // Create a new offer
                String offerId = UUID.randomUUID().toString();
                OfferInfo offerInfo = new OfferInfo(inDriveRide.getId(), offerAmount);
                activeOffersByOfferId.put(offerId, offerInfo);
                
                // Simulate driver responses after some time
                simulateDriverResponses(offerId, inDriveRide);
                
                callback.onSuccess(offerId);
            } catch (Exception e) {
                callback.onError("Failed to send inDrive offer: " + e.getMessage());
            }
        }, 1000 + random.nextInt(800));
    }
    
    @Override
    public void checkInDriveOfferStatus(String offerId, RideServiceCallback<OfferStatus> callback) {
        // Simulate network delay
        handler.postDelayed(() -> {
            try {
                OfferInfo offerInfo = activeOffersByOfferId.get(offerId);
                
                if (offerInfo == null) {
                    callback.onError("No active offer found with ID: " + offerId);
                    return;
                }
                
                // Convert internal enum to the class expected by the test
                String statusStr;
                switch (offerInfo.status) {
                    case PENDING:
                        statusStr = "pending";
                        break;
                    case ACCEPTED:
                        statusStr = "accepted";
                        break;
                    case REJECTED:
                        statusStr = "rejected";
                        break;
                    case COUNTER_OFFERED:
                        statusStr = "counter_offer";
                        break;
                    case EXPIRED:
                        statusStr = "expired";
                        break;
                    default:
                        statusStr = "unknown";
                }
                
                OfferStatus status = new OfferStatus(
                        statusStr,
                        offerInfo.offerAmount,
                        offerInfo.counterOfferAmount
                );
                
                callback.onSuccess(status);
            } catch (Exception e) {
                callback.onError("Failed to check offer status: " + e.getMessage());
            }
        }, 500 + random.nextInt(500));
    }
    
    @Override
    public void respondToCounterOffer(String offerId, boolean accept, RideServiceCallback<Boolean> callback) {
        // Simulate network delay
        handler.postDelayed(() -> {
            try {
                OfferInfo offerInfo = activeOffersByOfferId.get(offerId);
                
                if (offerInfo == null) {
                    callback.onError("No active offer found with ID: " + offerId);
                    return;
                }
                
                if (offerInfo.status != RideService.OfferStatus.COUNTER_OFFERED) {
                    callback.onError("This offer is not in counter-offered state");
                    return;
                }
                
                if (accept) {
                    // Update the offer
                    offerInfo.offerAmount = offerInfo.counterOfferAmount;
                    offerInfo.status = RideService.OfferStatus.ACCEPTED;
                } else {
                    // Update the offer
                    offerInfo.status = RideService.OfferStatus.REJECTED;
                    
                    // Remove the offer after some time
                    handler.postDelayed(() -> activeOffersByOfferId.remove(offerId), 5000);
                }
                
                callback.onSuccess(true);
            } catch (Exception e) {
                callback.onError("Failed to respond to counter-offer: " + e.getMessage());
            }
        }, 1000 + random.nextInt(800));
    }
    
    /**
     * Convert a location name to coordinates (mock implementation)
     */
    private double[] convertLocationToCoordinates(String location) {
        // In a real app, we would use Google Maps Geocoding API
        // For this mock implementation, we'll use fixed coordinates for known locations
        switch (location.toLowerCase()) {
            case "nasr city, cairo":
                return new double[]{30.0582, 31.3467};
            case "maadi, cairo":
                return new double[]{29.9602, 31.2569};
            case "downtown cairo":
                return new double[]{30.0444, 31.2357};
            case "giza":
                return new double[]{30.0131, 31.2089};
            case "heliopolis, cairo":
                return new double[]{30.0875, 31.3278};
            default:
                // Generate random coordinates in Cairo area
                return new double[]{
                        30.0 + (random.nextDouble() * 0.1),
                        31.2 + (random.nextDouble() * 0.2)
                };
        }
    }
    
    /**
     * Calculate approximate distance between two points
     */
    private double calculateDistance(double startLat, double startLng, double endLat, double endLng) {
        // Simple Euclidean distance calculation (not accurate for real-world use)
        // In a real app, we would use Google Maps Distance Matrix API or similar
        double latDiff = endLat - startLat;
        double lngDiff = endLng - startLng;
        return Math.sqrt(latDiff * latDiff + lngDiff * lngDiff) * 111; // Rough conversion to km
    }
    
    /**
     * Simulate driver responses to an offer
     */
    private void simulateDriverResponses(String offerId, Ride ride) {
        // Wait for 3-8 seconds before driver response
        handler.postDelayed(() -> {
            OfferInfo offerInfo = activeOffersByOfferId.get(offerId);
            if (offerInfo == null) return;
            
            // Determine driver response
            double responseType = random.nextDouble();
            
            if (responseType < 0.4) {
                // 40% chance: Driver accepts the offer
                offerInfo.status = RideService.OfferStatus.ACCEPTED;
                ride.setPrice(offerInfo.offerAmount);
            } else if (responseType < 0.8) {
                // 40% chance: Driver counter-offers
                double counterOffer = offerInfo.offerAmount * (1.1 + random.nextDouble() * 0.2); // 10-30% higher
                offerInfo.counterOfferAmount = counterOffer;
                offerInfo.status = RideService.OfferStatus.COUNTER_OFFERED;
            } else {
                // 20% chance: Driver rejects the offer
                offerInfo.status = RideService.OfferStatus.REJECTED;
                
                // Remove the offer after some time
                handler.postDelayed(() -> activeOffersByOfferId.remove(offerId), 5000);
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
        RideService.OfferStatus status;
        
        OfferInfo(String rideId, double offerAmount) {
            this.rideId = rideId;
            this.offerAmount = offerAmount;
            this.status = RideService.OfferStatus.PENDING;
        }
    }
}
