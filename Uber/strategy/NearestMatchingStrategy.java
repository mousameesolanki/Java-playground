package Uber.strategy;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import Uber.entities.Driver;
import Uber.entities.DriverStatus;
import Uber.entities.Location;
import Uber.entities.RideType;

public class NearestMatchingStrategy implements DriverMatching {
    private static final double MAX_DISTANCE = 5.0; // Maximum distance in kilometers to consider for matching
    @Override
    public List<Driver> findDrivers(List<Driver> drivers, Location pickupLocation, RideType rideType) {
        // Implement logic to find nearest drivers based on pickup location and ride type
         // Placeholder return statement
         //for each drive see its distance from pickup and provide,consider max distance to consider 
      System.out.println("Finding nearest drivers for pickup location: " + pickupLocation + " and ride type: " + rideType);
      return drivers.stream()
      .filter(driver -> driver.getStatus()== DriverStatus.AVAILABLE)
      .filter(driver->driver.getVehicle().getRideType() == rideType)
       .filter(driver-> pickupLocation.distanceTo(driver.getCurrentLocation()) <= MAX_DISTANCE)
       .sorted(Comparator.comparingDouble(driver -> pickupLocation.distanceTo(driver.getCurrentLocation())))
       .collect(Collectors.toList());
    } 
}
