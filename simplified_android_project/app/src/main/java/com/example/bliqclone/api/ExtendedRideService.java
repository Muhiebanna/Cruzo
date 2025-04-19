package com.example.bliqclone.api;

import com.example.bliqclone.models.Ride;
import com.example.bliqclone.models.ServiceProvider;

import java.util.List;

/**
 * Extended interface for ride service API with additional methods needed by tests
 */
public interface ExtendedRideService extends RideService {
    /**
     * Get available rides from all services for a given route using location names
     * @param startLocation Starting location name (e.g., "Nasr City, Cairo")
     * @param endLocation Destination location name (e.g., "Maadi, Cairo")
     * @param callback Callback to handle the result
     */
    void searchRides(String startLocation, String endLocation, RideServiceCallback<List<Ride>> callback);
    
    /**
     * Sort rides by price (ascending)
     * @param rides List of rides to sort
     * @return Sorted list of rides
     */
    List<Ride> sortRidesByPrice(List<Ride> rides);
    
    /**
     * Sort rides by ETA (ascending)
     * @param rides List of rides to sort
     * @return Sorted list of rides
     */
    List<Ride> sortRidesByEta(List<Ride> rides);
    
    /**
     * Find the best ride based on price, ETA, and rating
     * @param rides List of rides to evaluate
     * @return The best ride
     */
    Ride findBestRide(List<Ride> rides);
    
    /**
     * Send an offer for an inDrive ride using location names
     * @param startLocation Starting location name (e.g., "Nasr City, Cairo")
     * @param endLocation Destination location name (e.g., "Maadi, Cairo")
     * @param offerAmount The amount to offer
     * @param callback Callback to handle the result
     */
    void sendInDriveOffer(String startLocation, String endLocation, double offerAmount, 
                         RideServiceCallback<String> callback);
    
    /**
     * Check the status of an offer
     * @param offerId The ID of the offer
     * @param callback Callback to handle the result
     */
    void checkInDriveOfferStatus(String offerId, RideServiceCallback<OfferStatus> callback);
    
    /**
     * Respond to a counter-offer
     * @param offerId The ID of the offer
     * @param accept Whether to accept the counter-offer
     * @param callback Callback to handle the result
     */
    void respondToCounterOffer(String offerId, boolean accept, RideServiceCallback<Boolean> callback);
    
    /**
     * Class representing the status of an offer with additional information
     */
    class OfferStatus {
        private final String status;
        private final double amount;
        private final double counterOfferAmount;
        
        public OfferStatus(String status, double amount, double counterOfferAmount) {
            this.status = status;
            this.amount = amount;
            this.counterOfferAmount = counterOfferAmount;
        }
        
        public String getStatus() {
            return status;
        }
        
        public double getAmount() {
            return amount;
        }
        
        public double getCounterOfferAmount() {
            return counterOfferAmount;
        }
    }
}
