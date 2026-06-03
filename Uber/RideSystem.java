package Uber;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import Uber.strategy.DriverMatching;
import Uber.strategy.Pricing;
import Uber.entities.*;
import Uber.Observer.*;
import Uber.Observer.*;


public class RideSystem {
    private static volatile RideSystem instance;
private Pricing pricing;
private final Map<String, Trip> trips = new ConcurrentHashMap<>();
private final Map<String, Driver> drivers = new ConcurrentHashMap<>();
private final Map<String, Rider> riders = new ConcurrentHashMap<>();
private DriverMatching driverMatchingStrategy;
    RideSystem() {
    }

    public static RideSystem getInstance() {
        if (instance == null) {
            synchronized (RideSystem.class) {
                if (instance == null) {
                    instance = new RideSystem();
                }
            }
        }
        return instance;
    }
    public void setPricingStrategy(Pricing pricing) {
        // Set the pricing strategy
        this.pricing = pricing;
    }
    public void setDriverMatchingStrategy(DriverMatching driverMatching) {
        // Set the driver matching strategy
        this.driverMatchingStrategy = driverMatching;
    }
    public Rider registerRider(String name) {
        Rider rider = new Rider(name);
        riders.put(rider.getUserId(), rider);
        return rider;
    }
    public Driver registerDriver(String name) {
        Driver driver = new Driver(name, null, null);
        drivers.put(driver.getUserId(), driver);
        return driver;
    }
    public Trip requestRide(Rider rider, Location pickupLocation, Location dropoffLocation, RideType rideType) {
      Rider matchingRider = riders.get(rider.getUserId());
        if (matchingRider == null) {
            throw new IllegalArgumentException("Rider not found");
        }
        //find available drivers using the driver matching strategy
        List<Driver> availableDrivers = driverMatchingStrategy.findDrivers(new ArrayList<>(drivers.values()), pickupLocation, rideType);
        if (availableDrivers.isEmpty()) {
            throw new IllegalArgumentException("No available drivers found");
        }
        // Select the first available driver (in a real implementation, you might want to select the best one based on some criteria)
        Driver selectedDriver = availableDrivers.get(0);
        double fare = pricing.calculateFare(pickupLocation, dropoffLocation, rideType);
        // Create a new trip request
        Trip trip = new Trip.TripBuilder()
                .withRider(rider)
                .withPickupLocation(pickupLocation)
                .withDropoffLocation(dropoffLocation)
                .withFare(fare)
                .build();
        trips.put(trip.getId(), trip);
        //notify nearby drivers about the new trip request
        for(Driver driver : availableDrivers) {
            driver.onUpdate(trip);
        }
        return trip;
    }
}
