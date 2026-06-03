package Uber.entities;

import java.nio.ReadOnlyBufferException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import Uber.Observer.*;
import Uber.state.RequestedState;
import Uber.state.TripState;

public class Trip {
   private final String tripId;
   private final Rider rider;
    private Driver driver; 
    private TripStatus status;
    private double fare;
    private Location pickupLocation;
    private Location dropoffLocation;
    private TripState currentState;
    private final List<TripObserver> observesers= new ArrayList<>();
   private Trip(TripBuilder builder) {
        this.tripId = builder.tripId;
        this.rider = builder.rider;

        this.status = TripStatus.REQUESTED; // Default status when trip is created
        this.fare = builder.fare;
        this.pickupLocation = builder.pickupLocation;
        this.dropoffLocation = builder.dropoffLocation;
        this.currentState= new RequestedState();
    }
    public void addObserver(TripObserver observer) {
        observesers.add(observer);
    }
    private void notifyObservers() {
      observesers.forEach(observer -> observer.onUpdate(this));
    }
    public void assignDriver(Driver driver) {
        currentState.assignDriver(this, driver);
        addObserver(driver);
        notifyObservers();
    }
    void startTrip() {
        currentState.startTrip(this);
        notifyObservers();
    }
    void completeTrip() {
        currentState.completeTrip(this);
        notifyObservers();
    }
    public void setState(TripState state) {
        this.currentState = state;
    }
    public void setStatus(TripStatus status) {
        this.status = status;
    }
    public void setDriver(Driver driver) {
        this.driver = driver;
    }
    //Builder pattern for creating Trip instances
    public static class TripBuilder {
        private final String tripId;
        private Rider rider;
        private double fare;
        private Location pickupLocation;
        private Location dropoffLocation;

        public TripBuilder() {
            this.tripId = UUID.randomUUID().toString();// Rider will be set later
          
        }

        public TripBuilder withRider(Rider rider) {
            this.rider = rider;
            return this;
        }

       

        public TripBuilder withFare(double fare) {
            this.fare = fare;
            return this;
        }

        public TripBuilder withPickupLocation(Location pickupLocation) {
            this.pickupLocation = pickupLocation;
            return this;
        }

        public TripBuilder withDropoffLocation(Location dropoffLocation) {
            this.dropoffLocation = dropoffLocation;
            return this;
        }

        public Trip build() {
            if(rider == null || pickupLocation == null || dropoffLocation == null) {
                throw new IllegalStateException("Rider, pickup location, and dropoff location must be provided");
            }
            return new Trip(this);
        }
    }
    @Override
    public String toString() {
        return "Trip{" +
                "tripId='" + tripId + '\'' +
                ", rider=" + rider.getName() +
                ", driver=" + (driver != null ? driver.getName() : "None") +
                ", status=" + status +
                ", fare=" + fare +
                ", pickupLocation=" + pickupLocation +
                ", dropoffLocation=" + dropoffLocation +
                '}';
    }
    public String getId() {
        return tripId;
    }
    public User getDriver() {
        return driver;
    }
    public TripStatus getStatus() {
        return status;
    }
    
  
}
