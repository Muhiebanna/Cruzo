package com.example.bliqclone.models;

import java.io.Serializable;
import java.util.Date;

/**
 * Model class representing an earning record for drivers
 */
public class EarningRecord implements Serializable {
    private String id;
    private Date date;
    private String time;
    private ServiceProvider serviceProvider;
    private String rideType;
    private double amount;
    private String route;
    private double distance;
    private int durationMinutes;
    private String paymentMethod;
    
    public EarningRecord() {
    }
    
    public EarningRecord(String id, Date date, String time, ServiceProvider serviceProvider, 
                        String rideType, double amount, String route, double distance, 
                        int durationMinutes, String paymentMethod) {
        this.id = id;
        this.date = date;
        this.time = time;
        this.serviceProvider = serviceProvider;
        this.rideType = rideType;
        this.amount = amount;
        this.route = route;
        this.distance = distance;
        this.durationMinutes = durationMinutes;
        this.paymentMethod = paymentMethod;
    }
    
    // Getters and setters
    public String getId() {
        return id;
    }
    
    public void setId(String id) {
        this.id = id;
    }
    
    public Date getDate() {
        return date;
    }
    
    public void setDate(Date date) {
        this.date = date;
    }
    
    public String getTime() {
        return time;
    }
    
    public void setTime(String time) {
        this.time = time;
    }
    
    public ServiceProvider getServiceProvider() {
        return serviceProvider;
    }
    
    public void setServiceProvider(ServiceProvider serviceProvider) {
        this.serviceProvider = serviceProvider;
    }
    
    public String getRideType() {
        return rideType;
    }
    
    public void setRideType(String rideType) {
        this.rideType = rideType;
    }
    
    public double getAmount() {
        return amount;
    }
    
    public void setAmount(double amount) {
        this.amount = amount;
    }
    
    public String getRoute() {
        return route;
    }
    
    public void setRoute(String route) {
        this.route = route;
    }
    
    public double getDistance() {
        return distance;
    }
    
    public void setDistance(double distance) {
        this.distance = distance;
    }
    
    public int getDurationMinutes() {
        return durationMinutes;
    }
    
    public void setDurationMinutes(int durationMinutes) {
        this.durationMinutes = durationMinutes;
    }
    
    public String getPaymentMethod() {
        return paymentMethod;
    }
    
    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }
    
    /**
     * Get the service info (provider and ride type)
     * @return Formatted service info string
     */
    public String getServiceInfo() {
        return serviceProvider.getDisplayName() + " " + rideType;
    }
    
    /**
     * Format the distance in a human-readable format
     * @return Formatted distance string
     */
    public String getFormattedDistance() {
        return String.format("%.1f km", distance);
    }
}
