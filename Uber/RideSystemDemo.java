package Uber;

//Functional Requirements
//Design and implement a Ride Sharing Service that allows riders to request rides, drivers to accept trips, and the system to manage trip assignments, payments, and trip status.

import Uber.Observer.Rider;
import Uber.entities.Driver;
import Uber.entities.DriverStatus;
import Uber.entities.Location;
import Uber.entities.RideType;
import Uber.entities.Trip;
import Uber.strategy.NearestMatchingStrategy;
import Uber.strategy.VehicleBasedPricing;

public class RideSystemDemo {
    public static void main(String[] args) {
        RideSystem rideSystem = RideSystem.getInstance();
        //
        // Set up pricing and driver matching strategies
        rideSystem.setPricingStrategy(new VehicleBasedPricing());
        rideSystem.setDriverMatchingStrategy(new NearestMatchingStrategy());
        // Register drivers
        Driver driver1 = rideSystem.registerDriver("Alice");    
        Driver driver2 = rideSystem.registerDriver("Bob");
        // Register riders
        Rider rider1 = rideSystem.registerRider("Charlie");
        Rider rider2 = rideSystem.registerRider("David");
        //drivers go online and update their location
        driver1.setStatus(DriverStatus.AVAILABLE);
        driver1.setCurrentLocation(new Location(12.9716, 77.5946)); // Bangalore
        driver2.setStatus(DriverStatus.AVAILABLE);
        driver2.setCurrentLocation(new Location(12.9716, 77.5946)); // Bangalore
        //rider1 requests a ride
        Location pickupLocation = new Location(12.9716, 77.5946); // Bangalore
        Location dropoffLocation = new Location(13.0827, 80.2707); // Chennai
        //rider1 wants auto
        Trip trip1 = rideSystem.requestRide(rider1, pickupLocation, dropoffLocation, RideType.AUTO);
        if(trip1 != null) {
            
            System.out.println("Trip requested successfully for rider: " + rider1.getName());
        } else {
            System.out.println("No drivers available for rider: " + rider1.getName());
        }

      
    }
}