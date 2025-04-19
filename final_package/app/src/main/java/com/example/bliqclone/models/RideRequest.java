package com.example.bliqclone.models;

import java.io.Serializable;

/**
 * Model class representing a ride request for drivers
 */
public class RideRequest implements Serializable {
    private String id;
    private ServiceProvider serviceProvider;
    private String rideType;
    private double price;
    private String pickupLocation;
    private String destination;
    private double distance;
    private int estimatedTimeMinutes;
    private String paymentMethod;
    private String passengerName;
    private float passengerRating;
    private boolean isOfferBased; // True for inDrive requests

    public RideRequest() {
    }

    public RideRequest(String id, ServiceProvider serviceProvider, String rideType, double price,
                      String pickupLocation, String destination, double distance, 
                      int estimatedTimeMinutes, String paymentMethod) {
        this.id = id;
        this.serviceProvider = serviceProvider;
        this.rideType = rideType;
        this.price = price;
        this.pickupLocation = pickupLocation;
        this.destination = destination;
        this.distance = distance;
        this.estimatedTimeMinutes = estimatedTimeMinutes;
        this.paymentMethod = paymentMethod;
        this.isOfferBased = serviceProvider == ServiceProvider.INDRIVE;
    }

    // Getters and setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public ServiceProvider getServiceProvider() {
        return serviceProvider;
    }

    public void setServiceProvider(ServiceProvider serviceProvider) {
        this.serviceProvider = serviceProvider;
        this.isOfferBased = serviceProvider == ServiceProvider.INDRIVE;
    }

    public String getRideType() {
        return rideType;
    }

    public void setRideType(String rideType) {
        this.rideType = rideType;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getPickupLocation() {
        return pickupLocation;
    }

    public void setPickupLocation(String pickupLocation) {
        this.pickupLocation = pickupLocation;
    }

    public String getDestination() {
        return destination;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }

    public double getDistance() {
        return distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }

    public int getEstimatedTimeMinutes() {
        return estimatedTimeMinutes;
    }

    public void setEstimatedTimeMinutes(int estimatedTimeMinutes) {
        this.estimatedTimeMinutes = estimatedTimeMinutes;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public String getPassengerName() {
        return passengerName;
    }

    public void setPassengerName(String passengerName) {
        this.passengerName = passengerName;
    }

    public float getPassengerRating() {
        return passengerRating;
    }

    public void setPassengerRating(float passengerRating) {
        this.passengerRating = passengerRating;
    }

    public boolean isOfferBased() {
        return isOfferBased;
    }

    public void setOfferBased(boolean offerBased) {
        isOfferBased = offerBased;
    }

    /**
     * Format the estimated time in a human-readable format
     * @return Formatted time string
     */
    public String getFormattedEstimatedTime() {
        if (estimatedTimeMinutes < 1) {
            return "Less than a minute";
        } else if (estimatedTimeMinutes == 1) {
            return "1 minute";
        } else if (estimatedTimeMinutes < 60) {
            return estimatedTimeMinutes + " minutes";
        } else {
            int hours = estimatedTimeMinutes / 60;
            int minutes = estimatedTimeMinutes % 60;
            if (minutes == 0) {
                return hours + " hour" + (hours > 1 ? "s" : "");
            } else {
                return hours + " hour" + (hours > 1 ? "s" : "") + " " + minutes + " min";
            }
        }
    }

    /**
     * Format the distance in a human-readable format
     * @return Formatted distance string
     */
    public String getFormattedDistance() {
        return String.format("%.1f km", distance);
    }
}
