package com.example.bliqclone.api.mock;

import android.os.Handler;
import android.os.Looper;

import com.example.bliqclone.api.DriverService;
import com.example.bliqclone.models.EarningRecord;
import com.example.bliqclone.models.RideRequest;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.UUID;

/**
 * Extended mock implementation of the DriverService interface
 */
public class ExtendedMockDriverServiceImpl extends MockDriverServiceImpl {
    private static final Random random = new Random();
    private final Handler handler = new Handler(Looper.getMainLooper());
    
    /**
     * Get earnings data for a specific period
     * @param period The period to get earnings for (day, week, month)
     * @param callback Callback to handle the result
     */
    @Override
    public void getEarnings(String period, DriverServiceCallback<EarningsData> callback) {
        // Simulate network delay
        handler.postDelayed(() -> {
            try {
                List<EarningRecord> records = generateEarningRecords(period);
                double totalEarnings = calculateTotalEarnings(records);
                int totalRides = records.size();
                double averageRating = calculateAverageRating(records);
                
                EarningsData data = new EarningsData(totalEarnings, totalRides, averageRating, records);
                callback.onSuccess(data);
            } catch (Exception e) {
                callback.onError("Failed to get earnings data: " + e.getMessage());
            }
        }, 1000 + random.nextInt(500));
    }
    
    /**
     * Generate mock earning records for a specific period
     */
    private List<EarningRecord> generateEarningRecords(String period) {
        List<EarningRecord> records = new ArrayList<>();
        
        // Determine date range based on period
        Calendar calendar = Calendar.getInstance();
        Date endDate = calendar.getTime();
        
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        
        switch (period.toLowerCase()) {
            case "day":
                // Current day
                break;
            case "week":
                // Go back to beginning of week (Sunday)
                calendar.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);
                break;
            case "month":
                // Go back to beginning of month
                calendar.set(Calendar.DAY_OF_MONTH, 1);
                break;
            default:
                // Default to day
                break;
        }
        
        Date startDate = calendar.getTime();
        
        // Generate random number of records
        int numRecords = 0;
        switch (period.toLowerCase()) {
            case "day":
                numRecords = 2 + random.nextInt(5); // 2-6 rides per day
                break;
            case "week":
                numRecords = 10 + random.nextInt(15); // 10-24 rides per week
                break;
            case "month":
                numRecords = 30 + random.nextInt(30); // 30-59 rides per month
                break;
            default:
                numRecords = 2 + random.nextInt(5); // Default to day
                break;
        }
        
        // Generate records
        for (int i = 0; i < numRecords; i++) {
            // Random date within the period
            long randomTime = startDate.getTime() + (long) (random.nextDouble() * (endDate.getTime() - startDate.getTime()));
            Date recordDate = new Date(randomTime);
            
            // Random amount between 30 and 200 EGP
            double amount = 30 + random.nextDouble() * 170;
            
            // Random rating between 3 and 5
            float rating = 3.0f + random.nextFloat() * 2.0f;
            
            // Random pickup and dropoff locations
            String pickupLocation = getRandomLocation();
            String dropoffLocation = getRandomLocation();
            
            // Create record
            EarningRecord record = new EarningRecord(
                    UUID.randomUUID().toString(),
                    amount,
                    recordDate,
                    rating,
                    pickupLocation,
                    dropoffLocation
            );
            
            records.add(record);
        }
        
        return records;
    }
    
    /**
     * Calculate total earnings from a list of records
     */
    private double calculateTotalEarnings(List<EarningRecord> records) {
        double total = 0;
        for (EarningRecord record : records) {
            total += record.getAmount();
        }
        return total;
    }
    
    /**
     * Calculate average rating from a list of records
     */
    private double calculateAverageRating(List<EarningRecord> records) {
        if (records.isEmpty()) {
            return 0;
        }
        
        double totalRating = 0;
        for (EarningRecord record : records) {
            totalRating += record.getRating();
        }
        return totalRating / records.size();
    }
    
    /**
     * Get a random location in Cairo
     */
    private String getRandomLocation() {
        String[] locations = {
                "Nasr City, Cairo",
                "Maadi, Cairo",
                "Downtown Cairo",
                "Giza",
                "Heliopolis, Cairo",
                "6th of October City",
                "New Cairo",
                "Zamalek, Cairo",
                "Dokki, Cairo",
                "Mohandessin, Cairo"
        };
        
        return locations[random.nextInt(locations.length)];
    }
}
