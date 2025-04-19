package com.example.bliqclone.models;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Model class representing a ride option from a service provider
 */
public class Ride implements Serializable {
    private String id;
    private ServiceProvider serviceProvider;
    private String rideType; // e.g., Economy, Comfort, Premium
    private double price;
    private int etaMinutes;
    private double distance;
    private float driverRating;
    private String driverName;
    private String vehicleModel;
    private String vehiclePlate;
    private boolean isBestValue;
    private double priceComparison; // Difference from average price (negative means cheaper)
    private int etaComparison; // Difference from average ETA (negative means faster)
    private boolean isOfferBased; // True for inDrive where price is negotiable
    private double suggestedPrice; // For inDrive, the suggested price
    private double userOffer; // For inDrive, the user's offer
    private List<String> features; // Special features of this ride

    public Ride() {
        this.features = new ArrayList<>();
    }

    public Ride(String id, ServiceProvider serviceProvider, String rideType, double price, 
                int etaMinutes, double distance, float driverRating) {
        this.id = id;
        this.serviceProvider = serviceProvider;
        this.rideType = rideType;
        this.price = price;
        this.etaMinutes = etaMinutes;
        this.distance = distance;
        this.driverRating = driverRating;
        this.features = new ArrayList<>();
        this.isOfferBased = serviceProvider == ServiceProvider.INDRIVE;
        if (this.isOfferBased) {
            this.suggestedPrice = price;
        }
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
        if (this.isOfferBased && this.suggestedPrice == 0) {
            this.suggestedPrice = price;
        }
    }

    public int getEtaMinutes() {
        return etaMinutes;
    }

    public void setEtaMinutes(int etaMinutes) {
        this.etaMinutes = etaMinutes;
    }

    public double getDistance() {
        return distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }

    public float getDriverRating() {
        return driverRating;
    }

    public void setDriverRating(float driverRating) {
        this.driverRating = driverRating;
    }

    public String getDriverName() {
        return driverName;
    }

    public void setDriverName(String driverName) {
        this.driverName = driverName;
    }

    public String getVehicleModel() {
        return vehicleModel;
    }

    public void setVehicleModel(String vehicleModel) {
        this.vehicleModel = vehicleModel;
    }

    public String getVehiclePlate() {
        return vehiclePlate;
    }

    public void setVehiclePlate(String vehiclePlate) {
        this.vehiclePlate = vehiclePlate;
    }

    public boolean isBestValue() {
        return isBestValue;
    }

    public void setBestValue(boolean bestValue) {
        isBestValue = bestValue;
    }

    public double getPriceComparison() {
        return priceComparison;
    }

    public void setPriceComparison(double priceComparison) {
        this.priceComparison = priceComparison;
    }

    public int getEtaComparison() {
        return etaComparison;
    }

    public void setEtaComparison(int etaComparison) {
        this.etaComparison = etaComparison;
    }

    public boolean isOfferBased() {
        return isOfferBased;
    }

    public void setOfferBased(boolean offerBased) {
        isOfferBased = offerBased;
    }

    public double getSuggestedPrice() {
        return suggestedPrice;
    }

    public void setSuggestedPrice(double suggestedPrice) {
        this.suggestedPrice = suggestedPrice;
    }

    public double getUserOffer() {
        return userOffer;
    }

    public void setUserOffer(double userOffer) {
        this.userOffer = userOffer;
    }

    public List<String> getFeatures() {
        return features;
    }

    public void setFeatures(List<String> features) {
        this.features = features;
    }

    public void addFeature(String feature) {
        this.features.add(feature);
    }

    /**
     * Format the ETA in a human-readable format
     * @return Formatted ETA string
     */
    public String getFormattedEta() {
        if (etaMinutes < 1) {
            return "Less than a minute";
        } else if (etaMinutes == 1) {
            return "1 minute";
        } else if (etaMinutes < 60) {
            return etaMinutes + " minutes";
        } else {
            int hours = etaMinutes / 60;
            int minutes = etaMinutes % 60;
            if (minutes == 0) {
                return hours + " hour" + (hours > 1 ? "s" : "");
            } else {
                return hours + " hour" + (hours > 1 ? "s" : "") + " " + minutes + " min";
            }
        }
    }

    /**
     * Format the price comparison in a human-readable format
     * @return Formatted price comparison string
     */
    public String getFormattedPriceComparison() {
        if (priceComparison == 0) {
            return "Average price";
        } else if (priceComparison < 0) {
            return String.format("%.0f%% cheaper", Math.abs(priceComparison * 100));
        } else {
            return String.format("%.0f%% more expensive", priceComparison * 100);
        }
    }

    /**
     * Format the ETA comparison in a human-readable format
     * @return Formatted ETA comparison string
     */
    public String getFormattedEtaComparison() {
        if (etaComparison == 0) {
            return "Average time";
        } else if (etaComparison < 0) {
            int mins = Math.abs(etaComparison);
            return mins + " min" + (mins > 1 ? "s" : "") + " faster";
        } else {
            return etaComparison + " min" + (etaComparison > 1 ? "s" : "") + " slower";
        }
    }
}
