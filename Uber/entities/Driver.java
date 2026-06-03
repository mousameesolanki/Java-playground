package Uber.entities;

public class Driver extends User {
    private DriverStatus status;
    private Vehicle vehicle;
    private Location currentLocation;

    public Driver(String name, Vehicle vehicle, Location currentLocation) {
        super( name);
        this.vehicle = vehicle;
        this.currentLocation = currentLocation;
        this.status = DriverStatus.OFFLINE; // Default status
    }
   
    public DriverStatus getStatus() {
        return status;
    }

    public void setStatus(DriverStatus status) {
        this.status = status;
    }

    public Vehicle getVehicle() {
        return vehicle;
    }
  public void setCurrentLocation(Location currentLocation) {
        this.currentLocation = currentLocation;
    }
    public Location getCurrentLocation() {
        return currentLocation;
    }
    @Override
    public void onUpdate(Trip trip) {
        // Notify driver about trip updates
        System.out.println("Driver " + getName() + " notified about trip status: " + trip.getStatus());
        if(trip.getStatus() == TripStatus.REQUESTED) {
         System.out.println("Driver " + getName() + " received a new trip request.");
        }
    }

}
