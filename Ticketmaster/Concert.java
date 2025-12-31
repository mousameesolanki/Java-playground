import java.time.LocalDateTime;
import java.util.List;

public class Concert {
    private final String concertId;
    private final String venue;
    private  final double price;
    private  final LocalDateTime startTime;
    private  final LocalDateTime EndTime;
    private  final String artist;
    private  final List<Seat> seats;

    public Concert(String concertId, String venue, double price, LocalDateTime startTime, LocalDateTime endTime, String artist, List<Seat> seats) {
        this.concertId = concertId;
        this.venue = venue;
        this.price = price;
        this.startTime = startTime;
        EndTime = endTime;//
        this.artist = artist;
        this.seats = seats;
    }

    public String getVenue() {
        return venue;
    }

    public double getPrice() {
        return price;
    }

    public String getConcertId() {
        return concertId;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public LocalDateTime getEndTime() {
        return EndTime;
    }

    public String getArtist() {
        return artist;
    }

    public List<Seat> getSeats() {
        return seats;
    }
}
