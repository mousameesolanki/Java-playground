package Uber.strategy;

import Uber.entities.Driver;
import Uber.entities.RideType;
import Uber.entities.Location;
import java.util.List;

public interface DriverMatching {
    List<Driver> findDrivers(List<Driver> drivers, Location pickupLocation, RideType rideType);
}
