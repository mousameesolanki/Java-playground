package Uber.entities;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import Uber.Observer.TripObserver;

public abstract class User implements TripObserver {
    private final String userId;
    private final String name;
    private final List<Trip> tripHistory;

    public  User ( String name) {
        this.userId = UUID.randomUUID().toString();
        this.name = name;
        this.tripHistory = new ArrayList<>();
    }
//add trip to history
    public void addTripToHistory(Trip trip) {
        tripHistory.add(trip);
    }
    //get trip history
    public List<Trip> getTripHistory() {
        return tripHistory;
    }
    public String getUserId() {
        return userId;
    }

    public String getName() {
        return name;
    }
}
