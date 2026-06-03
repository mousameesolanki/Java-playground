package Uber.strategy;

import Uber.entities.Location;
import Uber.entities.RideType;

public interface Pricing {
    double calculateFare(Location pickupLocation, Location dropoffLocation, RideType rideType);
}
