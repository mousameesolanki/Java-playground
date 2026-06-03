package Uber.state;

import Uber.entities.Driver;
import Uber.entities.Trip;

public class CompletedState implements TripState {
    @Override
    public void requestTrip(Trip trip) {
        System.out.println("Trip is already completed. Cannot request a new trip.");
    }

    @Override
    public void assignDriver(Trip trip, Driver driver) {
        System.out.println("Trip is already completed. Cannot assign a driver.");
    }

    @Override
    public void startTrip(Trip trip) {
        System.out.println("Trip is already completed. Cannot start the trip.");
    }

    @Override
    public void completeTrip(Trip trip) {
        System.out.println("Trip is already completed.");
    }
    
}
