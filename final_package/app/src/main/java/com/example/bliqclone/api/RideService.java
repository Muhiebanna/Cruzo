package com.example.bliqclone.api;

import com.example.bliqclone.models.Ride;
import com.example.bliqclone.models.ServiceProvider;

import java.util.List;

/**
 * Interface for ride service API
 */
public interface RideService {
    /**
     * Get available rides from all services for a given route
     * @param startLat Starting latitude
     * @param startLng Starting longitude
     * @param endLat Destination latitude
     * @param endLng Destination longitude
     * @param callback Callback to handle the result
     */
    void getAvailableRides(double startLat, double startLng, double endLat, double endLng, RideServiceCallback<List<Ride>> callback);
    
    /**
     * Get available rides from a specific service provider
     * @param provider The service provider to query
     * @param startLat Starting latitude
     * @param startLng Starting longitude
     * @param endLat Destination latitude
     * @param endLng Destination longitude
     * @param callback Callback to handle the result
     */
    void getAvailableRidesFromProvider(ServiceProvider provider, double startLat, double startLng, 
                                      double endLat, double endLng, RideServiceCallback<List<Ride>> callback);
    
    /**
     * Book a ride
     * @param ride The ride to book
     * @param callback Callback to handle the result
     */
    void bookRide(Ride ride, RideServiceCallback<Boolean> callback);
    
    /**
     * Send an offer for an inDrive ride
     * @param ride The ride to make an offer for
     * @param offerAmount The amount to offer
     * @param callback Callback to handle the result
     */
    void sendOffer(Ride ride, double offerAmount, RideServiceCallback<Boolean> callback);
    
    /**
     * Get the status of an offer
     * @param ride The ride with an active offer
     * @param callback Callback to handle the result
     */
    void getOfferStatus(Ride ride, RideServiceCallback<OfferStatus> callback);
    
    /**
     * Accept a counter-offer from a driver
     * @param ride The ride with the counter-offer
     * @param counterOfferAmount The counter-offer amount
     * @param callback Callback to handle the result
     */
    void acceptCounterOffer(Ride ride, double counterOfferAmount, RideServiceCallback<Boolean> callback);
    
    /**
     * Reject a counter-offer from a driver
     * @param ride The ride with the counter-offer
     * @param callback Callback to handle the result
     */
    void rejectCounterOffer(Ride ride, RideServiceCallback<Boolean> callback);
    
    /**
     * Callback interface for ride service API calls
     * @param <T> The type of the result
     */
    interface RideServiceCallback<T> {
        void onSuccess(T result);
        void onError(String errorMessage);
    }
    
    /**
     * Enum representing the status of an offer
     */
    enum OfferStatus {
        PENDING,
        ACCEPTED,
        REJECTED,
        COUNTER_OFFERED,
        EXPIRED
    }
}
