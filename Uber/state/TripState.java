package Uber.state;

import Uber.entities.Driver;
import Uber.entities.Trip;

public interface TripState {
    void requestTrip(Trip trip);
    void assignDriver(Trip trip, Driver driver);
    void startTrip(Trip trip);
    void completeTrip(Trip trip);
}
