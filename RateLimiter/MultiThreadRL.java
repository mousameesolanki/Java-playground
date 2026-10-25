import java.util.concurrent.TimeUnit;
 class Request{
    private final long timestamp;
    private final String userId;
    public Request(long timestamp, String userId) {
        this.timestamp = timestamp;
        this.userId = userId;
    }
}
class Config {
    private final int limitPerMinute;
    public Config(int limit) {
        this.limitPerMinute = limit;
    }
    public int getLimit() {
        return limitPerMinute;
    }
}
public class MultiThreadRL {
    private final long capacity;
    private final long refillRate;
    private final long tokens;
    private long lastRefillTime;
    public MultiThreadRL(long capacity, long refillRate) {
        this.capacity = capacity;
        this.refillRate = refillRate;
        this.tokens = capacity;
        this.lastRefillTime = System.currentTimeMillis();
    }

}
