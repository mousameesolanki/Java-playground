package Uber.state;

import Uber.entities.Driver;
import Uber.entities.Trip;
import Uber.entities.TripStatus;

public class RequestedState implements TripState {
    // Implementation for the requested state of a trip
    @Override
    public void requestTrip(Trip trip) {
        // Logic to handle trip request
        System.out.println("Trip already requested: " + trip.getId());
    }
    @Override
    public void assignDriver(Trip trip, Driver driver) {
        // Logic to assign driver to the trip
        trip.setDriver(driver);
        trip.setStatus(TripStatus.ASSIGNED);
        trip.setState(new AssignedState());
        System.out.println("Driver " + driver.getName() + " assigned to trip " + trip.getId());
    }
    @Override
    public void startTrip(Trip trip) {
    
        System.out.println("Cannot start trip. Driver not assigned yet for trip " + trip.getId());
    }
    @Override
    public void completeTrip(Trip trip) {
        // Logic to complete the trip
       
        System.out.println("Cannot complete trip. Trip not started yet for trip " + trip.getId());
    }

}
