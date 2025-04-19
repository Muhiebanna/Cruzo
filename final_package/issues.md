# Identified Issues in Cruzo App

## 1. Missing Classes
- `MockAuthServiceImpl.java` is referenced in ApiManager.java but is missing from the uploaded files
- `AuthService.java` interface is also missing but referenced in ApiManager.java

## 2. Method Discrepancies
### RideService Interface vs FunctionalityTest Usage
The following methods are called in FunctionalityTest.java but not defined in RideService.java:
- `searchRides(String startLocation, String endLocation, RideServiceCallback<List<Ride>> callback)`
- `sortRidesByPrice(List<Ride> rides)`
- `sortRidesByEta(List<Ride> rides)`
- `findBestRide(List<Ride> rides)`
- `sendInDriveOffer(String startLocation, String endLocation, double amount, RideServiceCallback<String> callback)`
- `checkInDriveOfferStatus(String offerId, RideServiceCallback<OfferStatus> callback)`
- `respondToCounterOffer(String offerId, boolean accept, RideServiceCallback<Boolean> callback)`

### RideService Interface vs MockRideServiceImpl Implementation
The MockRideServiceImpl implements:
- `getAvailableRides(double startLat, double startLng, double endLat, double endLng, RideServiceCallback<List<Ride>> callback)`
- `getAvailableRidesFromProvider(ServiceProvider provider, double startLat, double startLng, double endLat, double endLng, RideServiceCallback<List<Ride>> callback)`
- `bookRide(Ride ride, RideServiceCallback<Boolean> callback)`
- `sendOffer(Ride ride, double offerAmount, RideServiceCallback<Boolean> callback)`
- `getOfferStatus(Ride ride, RideServiceCallback<OfferStatus> callback)`
- `acceptCounterOffer(Ride ride, double counterOfferAmount, RideServiceCallback<Boolean> callback)`
- `rejectCounterOffer(Ride ride, RideServiceCallback<Boolean> callback)`

But FunctionalityTest expects different method signatures.

## 3. OfferStatus Implementation Differences
- In RideService.java, OfferStatus is an enum with values: PENDING, ACCEPTED, REJECTED, COUNTER_OFFERED, EXPIRED
- In FunctionalityTest.java, OfferStatus appears to be a class with a getStatus() method that returns a String

## 4. MockRideService vs MockRideServiceImpl
- MockRideService.java contains static methods for generating mock ride data
- MockRideServiceImpl.java implements the RideService interface and uses MockRideService
- There may be inconsistencies between these implementations

## 5. Location Format Inconsistencies
- RideService.java methods use latitude/longitude coordinates
- FunctionalityTest.java uses string location names like "Nasr City, Cairo"

## 6. Missing Methods in RideService Implementation
The following methods are missing from the implementation but are called in tests:
- Methods for searching rides by location name instead of coordinates
- Methods for sorting rides by price and ETA
- Method for finding the best ride

## 7. Build Script Issues
- The build script references a project directory at ~/android_bliq_app which may not exist
- The script assumes certain Android SDK and Gradle versions are available
