package com.example.bliqclone.api;

import com.example.bliqclone.models.EarningRecord;
import com.example.bliqclone.models.RideRequest;

import java.util.List;

/**
 * Interface for driver service API
 */
public interface DriverService {
    /**
     * Get available ride requests
     * @param callback Callback to handle the result
     */
    void getAvailableRequests(DriverServiceCallback<List<RideRequest>> callback);
    
    /**
     * Accept a ride request
     * @param request The request to accept
     * @param callback Callback to handle the result
     */
    void acceptRideRequest(RideRequest request, DriverServiceCallback<Boolean> callback);
    
    /**
     * Reject a ride request
     * @param request The request to reject
     * @param callback Callback to handle the result
     */
    void rejectRideRequest(RideRequest request, DriverServiceCallback<Boolean> callback);
    
    /**
     * Complete a ride
     * @param rideId The ID of the ride to complete
     * @param callback Callback to handle the result
     */
    void completeRide(String rideId, DriverServiceCallback<Boolean> callback);
    
    /**
     * Cancel a ride
     * @param rideId The ID of the ride to cancel
     * @param reason The reason for cancellation
     * @param callback Callback to handle the result
     */
    void cancelRide(String rideId, String reason, DriverServiceCallback<Boolean> callback);
    
    /**
     * Get earnings data for a specific period
     * @param period The period to get earnings for (day, week, month)
     * @param callback Callback to handle the result
     */
    void getEarnings(String period, DriverServiceCallback<EarningsData> callback);
    
    /**
     * Callback interface for driver service API calls
     * @param <T> The type of the result
     */
    interface DriverServiceCallback<T> {
        void onSuccess(T result);
        void onError(String errorMessage);
    }
    
    /**
     * Class representing earnings data
     */
    class EarningsData {
        private final double totalEarnings;
        private final int totalRides;
        private final double averageRating;
        private final List<EarningRecord> records;
        
        public EarningsData(double totalEarnings, int totalRides, double averageRating, List<EarningRecord> records) {
            this.totalEarnings = totalEarnings;
            this.totalRides = totalRides;
            this.averageRating = averageRating;
            this.records = records;
        }
        
        public double getTotalEarnings() {
            return totalEarnings;
        }
        
        public int getTotalRides() {
            return totalRides;
        }
        
        public double getAverageRating() {
            return averageRating;
        }
        
        public List<EarningRecord> getRecords() {
            return records;
        }
    }
}
