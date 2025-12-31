import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class ConcertBookingSystem {
    private static ConcertBookingSystem instacne;
    private final Map<String,Concert> concerts;
    private final Map<String,Booking> bookings;
    private final Object lock = new Object();

    private ConcertBookingSystem(){
        concerts  = new ConcurrentHashMap<>();
        bookings = new ConcurrentHashMap<>();
    }
    public static synchronized ConcertBookingSystem getInstacne(){
        if(instacne==null){
            instacne = new ConcertBookingSystem();
        }
        return instacne;
    }

    void addConcert(Concert concert){
        LocalDateTime startTime =LocalDateTime.now();
        LocalDateTime endTime =LocalDateTime.now().plusHours(3);
        List<Seat>seats= new ArrayList<>();
        Concert newConcert = new Concert("10001","Bhopal",1000.00,startTime,endTime,"A R RAHMAN",seats);
    }

    public  Concert getConcert(String concertId){
        return concerts.get(concertId);
    }
    public List<Concert> searchConcerts(String artist,String venue,LocalDateTime dateTime){
        return concerts.values().stream()
                .filter(concert -> concert.getArtist().equalsIgnoreCase(artist) &&
                        concert.getVenue().equalsIgnoreCase(venue) &&
                        concert.getStartTime().equals(dateTime))
                .collect(Collectors.toList());
    }
    public Booking bookTickets(User user,Concert concert,List<Seat> seats){
        synchronized (lock){
            //check seat availabiliaty
            for(Seat seat: seats)
            {
                if(seat.getSeatStatus()!= SeatStatus.AVAILABLE) {
                    throw new SeatNotAvailableException("Seat " + seat.getSeatNumber() + "not available");
                }
            }
            seats.forEach(Seat::book);

            //create booking

            String bookingId= generateBookingId();
            Booking booking = new Booking(bookingId,user,concert,1000.00,seats);
            bookings.put(bookingId,booking);
            System.out.println("Booking  "+ booking.getId()+"-"+booking.getSeats().size()+"seats booked");
            return booking;
        }
    }
     public boolean cancelBooking(String bookingId){
        Booking booking = bookings.get(bookingId);
        if(booking != null){
            booking.cancleBooking();
            bookings.remove(bookingId);
            return true;
        }
        return false;
     }

     public void MakePayment(){
        /*
        * we make write a code to make the payment and store it in the store system then
        * notificate the user and admin about the status of the tickets.
        * */
     }

    public String generateBookingId(){
        return "BKG" + UUID.randomUUID();
    }

}
