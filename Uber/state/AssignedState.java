package Uber.state;

import Uber.entities.Driver;
import Uber.entities.Trip;
import Uber.entities.TripStatus;

public class AssignedState implements TripState {
   @Override
    public void requestTrip(Trip trip) {
        System.out.println("Trip is already requested.");
    }

    @Override
    public void assignDriver(Trip trip, Driver driver) {
        System.out.println("Driver is already assigned.To reassign driver, cancel the trip and request again.");
    }

    @Override
    public void startTrip(Trip trip) {
        trip.setStatus(TripStatus.IN_PROGRESS);
        trip.setState(new InProgressState());
    }

    @Override
    public void completeTrip(Trip trip) {
        System.out.println("Cannot complete trip. Trip is not in progress.");
    } 
}
