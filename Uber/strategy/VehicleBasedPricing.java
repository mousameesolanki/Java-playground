package Uber.strategy;

import java.util.Map;

import Uber.entities.Location;
import Uber.entities.RideType;

public class VehicleBasedPricing implements Pricing {
    private static final double BASE_FARE = 5.0;
   private static final Map<RideType, Double> PER_KM_RATE = Map.of(
        RideType.AUTO, 1.0,
        RideType.BIKE, 0.5,
        RideType.CAR, 2.0
    );

    @Override
    public double calculateFare(Location pickupLocation, Location dropoffLocation, RideType rideType) {
        double distance = pickupLocation.distanceTo(dropoffLocation);
       return BASE_FARE + (distance * PER_KM_RATE.get(rideType));
    }
    
}
