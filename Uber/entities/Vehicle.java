package Uber.entities;

public class Vehicle {
    private final String vehicleId;
    private final String model;
    private final RideType rideType;
    public Vehicle(String vehicleId, String model, RideType rideType) {
        this.vehicleId = vehicleId;
        this.model = model;
        this.rideType = rideType;
    }
    public String getVehicleId() {
        return vehicleId;
    }
    public String getModel() {
        return model;
    }
    public RideType getRideType() {
        return rideType;
    }
}
