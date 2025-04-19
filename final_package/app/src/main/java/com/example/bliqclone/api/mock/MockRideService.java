package com.example.bliqclone.api.mock;

import com.example.bliqclone.models.Ride;
import com.example.bliqclone.models.ServiceProvider;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;

/**
 * Mock implementation of ride service APIs for Egyptian ride-hailing services
 */
public class MockRideService {
    private static final Random random = new Random();
    
    /**
     * Get available rides from all services for a given route
     * @param startLat Starting latitude
     * @param startLng Starting longitude
     * @param endLat Destination latitude
     * @param endLng Destination longitude
     * @return List of available rides from different services
     */
    public static List<Ride> getAvailableRides(double startLat, double startLng, double endLat, double endLng) {
        List<Ride> rides = new ArrayList<>();
        
        // Calculate approximate distance and base price
        double distance = calculateDistance(startLat, startLng, endLat, endLng);
        double basePrice = calculateBasePrice(distance);
        
        // Add rides from each service provider
        addUberRides(rides, distance, basePrice);
        addBoltRides(rides, distance, basePrice);
        addCareemRides(rides, distance, basePrice);
        addInDriveRides(rides, distance, basePrice);
        addSwvlRides(rides, distance, basePrice);
        addDidiRides(rides, distance, basePrice);
        addHalanRides(rides, distance, basePrice);
        
        return rides;
    }
    
    /**
     * Add Uber ride options
     */
    private static void addUberRides(List<Ride> rides, double distance, double basePrice) {
        // Uber Economy
        Ride uberEconomy = new Ride(
                UUID.randomUUID().toString(),
                ServiceProvider.UBER,
                "Economy",
                basePrice * (1 + random.nextDouble() * 0.2),
                (int) (distance * 2 + random.nextInt(5)),
                distance,
                3.5f + random.nextFloat() * 1.5f
        );
        uberEconomy.setDriverName("Ahmed M.");
        uberEconomy.setVehicleModel("Toyota Corolla");
        uberEconomy.setVehiclePlate("ABC 123");
        rides.add(uberEconomy);
        
        // Uber Comfort
        Ride uberComfort = new Ride(
                UUID.randomUUID().toString(),
                ServiceProvider.UBER,
                "Comfort",
                basePrice * (1.3 + random.nextDouble() * 0.2),
                (int) (distance * 2 + random.nextInt(3)),
                distance,
                4.0f + random.nextFloat()
        );
        uberComfort.setDriverName("Mohamed K.");
        uberComfort.setVehicleModel("Honda Accord");
        uberComfort.setVehiclePlate("DEF 456");
        uberComfort.addFeature("Extra Legroom");
        uberComfort.addFeature("Temperature Control");
        rides.add(uberComfort);
        
        // Uber Black
        Ride uberBlack = new Ride(
                UUID.randomUUID().toString(),
                ServiceProvider.UBER,
                "Black",
                basePrice * (1.8 + random.nextDouble() * 0.3),
                (int) (distance * 1.8 + random.nextInt(3)),
                distance,
                4.5f + random.nextFloat() * 0.5f
        );
        uberBlack.setDriverName("Tarek S.");
        uberBlack.setVehicleModel("Mercedes E-Class");
        uberBlack.setVehiclePlate("GHI 789");
        uberBlack.addFeature("Premium Vehicle");
        uberBlack.addFeature("Professional Driver");
        uberBlack.addFeature("Top Rated");
        rides.add(uberBlack);
    }
    
    /**
     * Add Bolt ride options
     */
    private static void addBoltRides(List<Ride> rides, double distance, double basePrice) {
        // Bolt has competitive pricing in Egypt
        double boltDiscount = 0.9; // 10% discount compared to base
        
        // Bolt Standard
        Ride boltStandard = new Ride(
                UUID.randomUUID().toString(),
                ServiceProvider.BOLT,
                "Standard",
                basePrice * boltDiscount * (1 + random.nextDouble() * 0.15),
                (int) (distance * 2.1 + random.nextInt(6)),
                distance,
                3.7f + random.nextFloat() * 1.3f
        );
        boltStandard.setDriverName("Khaled F.");
        boltStandard.setVehicleModel("Hyundai Elantra");
        boltStandard.setVehiclePlate("JKL 012");
        rides.add(boltStandard);
        
        // Bolt Comfort
        Ride boltComfort = new Ride(
                UUID.randomUUID().toString(),
                ServiceProvider.BOLT,
                "Comfort",
                basePrice * boltDiscount * (1.25 + random.nextDouble() * 0.2),
                (int) (distance * 2 + random.nextInt(4)),
                distance,
                4.1f + random.nextFloat() * 0.8f
        );
        boltComfort.setDriverName("Amr H.");
        boltComfort.setVehicleModel("Kia Cerato");
        boltComfort.setVehiclePlate("MNO 345");
        boltComfort.addFeature("Newer Vehicle");
        boltComfort.addFeature("Higher Rated Drivers");
        rides.add(boltComfort);
    }
    
    /**
     * Add Careem ride options
     */
    private static void addCareemRides(List<Ride> rides, double distance, double basePrice) {
        // Careem Go
        Ride careemGo = new Ride(
                UUID.randomUUID().toString(),
                ServiceProvider.CAREEM,
                "Go",
                basePrice * (1.05 + random.nextDouble() * 0.15),
                (int) (distance * 2 + random.nextInt(5)),
                distance,
                3.8f + random.nextFloat() * 1.2f
        );
        careemGo.setDriverName("Youssef A.");
        careemGo.setVehicleModel("Nissan Sunny");
        careemGo.setVehiclePlate("PQR 678");
        rides.add(careemGo);
        
        // Careem Go+
        Ride careemGoPlus = new Ride(
                UUID.randomUUID().toString(),
                ServiceProvider.CAREEM,
                "Go+",
                basePrice * (1.3 + random.nextDouble() * 0.2),
                (int) (distance * 1.9 + random.nextInt(4)),
                distance,
                4.2f + random.nextFloat() * 0.7f
        );
        careemGoPlus.setDriverName("Mahmoud B.");
        careemGoPlus.setVehicleModel("Toyota Camry");
        careemGoPlus.setVehiclePlate("STU 901");
        careemGoPlus.addFeature("Spacious");
        careemGoPlus.addFeature("High Rated");
        rides.add(careemGoPlus);
        
        // Careem Executive
        Ride careemExecutive = new Ride(
                UUID.randomUUID().toString(),
                ServiceProvider.CAREEM,
                "Executive",
                basePrice * (1.7 + random.nextDouble() * 0.3),
                (int) (distance * 1.8 + random.nextInt(3)),
                distance,
                4.6f + random.nextFloat() * 0.4f
        );
        careemExecutive.setDriverName("Omar N.");
        careemExecutive.setVehicleModel("BMW 5 Series");
        careemExecutive.setVehiclePlate("VWX 234");
        careemExecutive.addFeature("Luxury Vehicle");
        careemExecutive.addFeature("Professional Driver");
        careemExecutive.addFeature("Premium Service");
        rides.add(careemExecutive);
    }
    
    /**
     * Add inDrive ride options
     */
    private static void addInDriveRides(List<Ride> rides, double distance, double basePrice) {
        // inDrive has negotiable pricing
        double inDriveBasePrice = basePrice * (0.95 + random.nextDouble() * 0.1);
        
        // inDrive Economy
        Ride inDriveEconomy = new Ride(
                UUID.randomUUID().toString(),
                ServiceProvider.INDRIVE,
                "Economy",
                inDriveBasePrice,
                (int) (distance * 2.2 + random.nextInt(7)),
                distance,
                3.6f + random.nextFloat() * 1.3f
        );
        inDriveEconomy.setDriverName("Hassan M.");
        inDriveEconomy.setVehicleModel("Chevrolet Aveo");
        inDriveEconomy.setVehiclePlate("YZA 567");
        inDriveEconomy.setOfferBased(true);
        inDriveEconomy.setSuggestedPrice(inDriveBasePrice);
        rides.add(inDriveEconomy);
        
        // inDrive Comfort
        Ride inDriveComfort = new Ride(
                UUID.randomUUID().toString(),
                ServiceProvider.INDRIVE,
                "Comfort",
                inDriveBasePrice * 1.2,
                (int) (distance * 2.1 + random.nextInt(5)),
                distance,
                4.0f + random.nextFloat() * 0.9f
        );
        inDriveComfort.setDriverName("Ali K.");
        inDriveComfort.setVehicleModel("Hyundai Accent");
        inDriveComfort.setVehiclePlate("BCD 890");
        inDriveComfort.setOfferBased(true);
        inDriveComfort.setSuggestedPrice(inDriveBasePrice * 1.2);
        inDriveComfort.addFeature("Comfortable Ride");
        rides.add(inDriveComfort);
    }
    
    /**
     * Add Swvl ride options
     */
    private static void addSwvlRides(List<Ride> rides, double distance, double basePrice) {
        // Swvl offers shared rides at lower prices
        double swvlDiscount = 0.7; // 30% discount for shared rides
        
        // Swvl Shared
        Ride swvlShared = new Ride(
                UUID.randomUUID().toString(),
                ServiceProvider.SWVL,
                "Shared",
                basePrice * swvlDiscount * (1 + random.nextDouble() * 0.1),
                (int) (distance * 2.5 + random.nextInt(10)), // Longer ETA for shared rides
                distance,
                3.9f + random.nextFloat() * 1.0f
        );
        swvlShared.setDriverName("Mostafa S.");
        swvlShared.setVehicleModel("Toyota Hiace");
        swvlShared.setVehiclePlate("EFG 123");
        swvlShared.addFeature("Shared Ride");
        swvlShared.addFeature("Fixed Route");
        swvlShared.addFeature("Economical");
        rides.add(swvlShared);
        
        // Swvl Premium
        Ride swvlPremium = new Ride(
                UUID.randomUUID().toString(),
                ServiceProvider.SWVL,
                "Premium",
                basePrice * 0.85 * (1 + random.nextDouble() * 0.15),
                (int) (distance * 2.3 + random.nextInt(8)),
                distance,
                4.1f + random.nextFloat() * 0.8f
        );
        swvlPremium.setDriverName("Ayman R.");
        swvlPremium.setVehicleModel("Mercedes Sprinter");
        swvlPremium.setVehiclePlate("HIJ 456");
        swvlPremium.addFeature("Premium Shared");
        swvlPremium.addFeature("Air Conditioned");
        swvlPremium.addFeature("Comfortable Seating");
        rides.add(swvlPremium);
    }
    
    /**
     * Add DiDi ride options
     */
    private static void addDidiRides(List<Ride> rides, double distance, double basePrice) {
        // DiDi Express
        Ride didiExpress = new Ride(
                UUID.randomUUID().toString(),
                ServiceProvider.DIDI,
                "Express",
                basePrice * (1 + random.nextDouble() * 0.15),
                (int) (distance * 2 + random.nextInt(5)),
                distance,
                3.7f + random.nextFloat() * 1.2f
        );
        didiExpress.setDriverName("Waleed T.");
        didiExpress.setVehicleModel("Renault Logan");
        didiExpress.setVehiclePlate("KLM 789");
        rides.add(didiExpress);
        
        // DiDi Select
        Ride didiSelect = new Ride(
                UUID.randomUUID().toString(),
                ServiceProvider.DIDI,
                "Select",
                basePrice * (1.25 + random.nextDouble() * 0.2),
                (int) (distance * 1.9 + random.nextInt(4)),
                distance,
                4.2f + random.nextFloat() * 0.7f
        );
        didiSelect.setDriverName("Hany G.");
        didiSelect.setVehicleModel("Kia Sportage");
        didiSelect.setVehiclePlate("NOP 012");
        didiSelect.addFeature("Newer Vehicle");
        didiSelect.addFeature("Top Rated Driver");
        rides.add(didiSelect);
    }
    
    /**
     * Add Halan ride options
     */
    private static void addHalanRides(List<Ride> rides, double distance, double basePrice) {
        // Halan offers more economical options
        
        // Halan Tuk-Tuk (for shorter distances)
        if (distance < 5) {
            Ride halanTukTuk = new Ride(
                    UUID.randomUUID().toString(),
                    ServiceProvider.HALAN,
                    "Tuk-Tuk",
                    basePrice * 0.6 * (1 + random.nextDouble() * 0.1),
                    (int) (distance * 2.2 + random.nextInt(5)),
                    distance,
                    3.5f + random.nextFloat() * 1.0f
            );
            halanTukTuk.setDriverName("Sayed M.");
            halanTukTuk.setVehicleModel("Bajaj Tuk-Tuk");
            halanTukTuk.setVehiclePlate("QRS 345");
            halanTukTuk.addFeature("Economical");
            halanTukTuk.addFeature("Quick for Short Distances");
            rides.add(halanTukTuk);
        }
        
        // Halan Motorbike (for shorter distances)
        if (distance < 7) {
            Ride halanMotorbike = new Ride(
                    UUID.randomUUID().toString(),
                    ServiceProvider.HALAN,
                    "Motorbike",
                    basePrice * 0.5 * (1 + random.nextDouble() * 0.1),
                    (int) (distance * 1.5 + random.nextInt(3)), // Faster for short distances
                    distance,
                    3.6f + random.nextFloat() * 1.0f
            );
            halanMotorbike.setDriverName("Fathy K.");
            halanMotorbike.setVehicleModel("Honda CB");
            halanMotorbike.setVehiclePlate("TUV 678");
            halanMotorbike.addFeature("Fastest Option");
            halanMotorbike.addFeature("Beat Traffic");
            halanMotorbike.addFeature("Most Economical");
            rides.add(halanMotorbike);
        }
        
        // Halan Car
        Ride halanCar = new Ride(
                UUID.randomUUID().toString(),
                ServiceProvider.HALAN,
                "Car",
                basePrice * 0.95 * (1 + random.nextDouble() * 0.15),
                (int) (distance * 2.1 + random.nextInt(6)),
                distance,
                3.8f + random.nextFloat() * 1.1f
        );
        halanCar.setDriverName("Ibrahim S.");
        halanCar.setVehicleModel("Fiat Tipo");
        halanCar.setVehiclePlate("WXY 901");
        rides.add(halanCar);
    }
    
    /**
     * Calculate approximate distance between two points
     */
    private static double calculateDistance(double startLat, double startLng, double endLat, double endLng) {
        // Simple Euclidean distance calculation (not accurate for real-world use)
        // In a real app, we would use Google Maps Distance Matrix API or similar
        double latDiff = endLat - startLat;
        double lngDiff = endLng - startLng;
        return Math.sqrt(latDiff * latDiff + lngDiff * lngDiff) * 111; // Rough conversion to km
    }
    
    /**
     * Calculate base price based on distance
     */
    private static double calculateBasePrice(double distance) {
        // Base price in Egyptian Pounds
        // Starting fare + per km rate
        return 15 + distance * 5;
    }
}
