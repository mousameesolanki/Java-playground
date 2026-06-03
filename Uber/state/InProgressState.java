package Uber.state;

import Uber.entities.Driver;
import Uber.entities.Trip;
import Uber.entities.TripStatus;

public class InProgressState implements TripState {
    @Override
    public void requestTrip(Trip trip) {
        System.out.println("Trip is already in progress. Cannot request a new trip.");
    }

    @Override
    public void assignDriver(Trip trip, Driver driver) {
        System.out.println("Trip is already in progress. Driver is already assigned.");
    }

    @Override
    public void startTrip(Trip trip) {
        System.out.println("Trip is already in progress.");
    }

    @Override
    public void completeTrip(Trip trip) {
       trip.setStatus(TripStatus.COMPLETED);
        trip.setState(new CompletedState());
      
    }
    
}
