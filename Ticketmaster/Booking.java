import java.util.ArrayList;
import java.util.List;

public class Booking {
    private final String id;
    private BookingStatus bookingStatus;
    private final User user;
    private final Concert concert;
    private final double totalPrice;
    private List<Seat> seats;


    public Booking(String id, User user, Concert concert, double totalPrice, List<Seat> seats) {
        this.id = id;
        this.user = user;
        this.concert = concert;
        this.totalPrice = totalPrice;
        this.seats = seats;
    }

    public String getId() {
        return id;
    }

    public BookingStatus getBookingStatus() {
        return bookingStatus;
    }

    public User getUser() {
        return user;
    }

    public Concert getConcert() {
        return concert;
    }

    public double getTotalPrice() {
        return totalPrice;
    }

    public List<Seat> getSeats() {
        return seats;
    }
    public void cancleBooking(){
        bookingStatus = BookingStatus.CANCELLED;
        return;
    }
}
