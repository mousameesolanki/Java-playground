package Uber.Observer;
import Uber.entities.Trip;
import Uber.entities.User;

public class Rider extends User {
    public Rider(String name) {
        super( name);
    }
    @Override
    public void onUpdate(Trip trip) {
        // Notify rider about trip updates
        System.out.println("Rider " + getName() + " notified about trip status: " + trip.getStatus());
        if(trip.getDriver() != null) {
         System.out.println("Rider " + getName() + " assigned driver: " + trip.getDriver().getName());
        }
    }
}
