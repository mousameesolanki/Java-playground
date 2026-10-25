// sliding window algorithm for rate limiting
// we can use a queue to store the timestamps of the requests and check if the number of requests in the last minute exceeds the limit

import java.util.Deque;
import java.util.LinkedList;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantLock;

interface RateLimiterInterface {
  boolean isAllowed(String userId, Long timestamp);
}

class RequestInfo {
  private Long timestamp;
  private String userId;

  public RequestInfo(Long timestamp, String userId) {
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

class InMemoryStorage {
  private final ConcurrentHashMap<String, Deque<Long>> userRequestMap = new ConcurrentHashMap<>();

  public void addRequest(String userId, Long timestamp) {
    Deque<Long> list = userRequestMap.computeIfAbsent(userId, k -> new LinkedList<>());
    list.add(timestamp);
  }

  public Deque<Long> getRequestTimestamps(String userId) {
    return userRequestMap.get(userId);
  }

  public void removeOldRequests(String userId, Long currentTime) {
    Deque<Long> timestamps = userRequestMap.get(userId);
    if (timestamps != null) {
      while (!timestamps.isEmpty() && currentTime - timestamps.peek() > 60000) {
        timestamps.poll();
      }
    }
  }
}

public class RateLimiter implements RateLimiterInterface {
  private final Config config;
  private final InMemoryStorage storage;
  private final ReentrantLock lock = new ReentrantLock();

  public RateLimiter(Config config, InMemoryStorage storage) {
    this.config = config;
    this.storage = storage;
  }

  @Override
  public boolean isAllowed(String userId, Long timestamp) {
    lock.lock();
    try {
      Deque<Long> queue = storage.getRequestTimestamps(userId);
      storage.removeOldRequests(userId, timestamp);
      int currentSize = (queue == null) ? 0 : queue.size();
      System.out.println(Thread.currentThread().getName() + " - Current requests: " + currentSize + " / " + config.getLimit());

      if (queue == null || currentSize < config.getLimit()) {
        storage.addRequest(userId, timestamp);
        return true;
      }
      return false;
    } finally {
      lock.unlock();
    }
  }

  public static void main(String[] args) throws InterruptedException {
    Config config = new Config(2); // 2 requests per minute
    InMemoryStorage storage = new InMemoryStorage();
    RateLimiter rateLimiter = new RateLimiter(config, storage);
    String userId = "user1";
    Long timestamp = System.currentTimeMillis();

    if (rateLimiter.isAllowed(userId, timestamp)) {
      System.out.println("Request allowed");
    } else {
      System.out.println("Request denied");
    }

    Runnable task = () -> {
      if (rateLimiter.isAllowed(userId, System.currentTimeMillis())) {
        System.out.println("Request allowed for " + Thread.currentThread().getName());
      } else {
        System.out.println("Request denied for " + Thread.currentThread().getName());
      }
    };

    Thread thread1 = new Thread(task);
    Thread thread2 = new Thread(task);
    Thread thread3 = new Thread(task);
    thread1.start();
    thread2.start();
    thread3.start();
    thread1.join();
    thread2.join();
    thread3.join();

    TokenBucketRateLimiter tokenBucketRateLimiter = new TokenBucketRateLimiter(2, 1);
    Runnable tokenBucketTask = () -> {
      if (tokenBucketRateLimiter.isAllowed(userId, System.currentTimeMillis())) {
        System.out.println("Request allowed for token bucket " + Thread.currentThread().getName());
      } else {
        System.out.println("Request denied for token bucket " + Thread.currentThread().getName());
      }
    };
//inside thread a rujnnable task we passsed
    Thread tokenBucketThread1 = new Thread(tokenBucketTask);
    Thread tokenBucketThread2 = new Thread(tokenBucketTask);
    Thread tokenBucketThread3 = new Thread(tokenBucketTask);
    tokenBucketThread1.start();
    tokenBucketThread2.start();
    tokenBucketThread3.start();
    tokenBucketThread1.join();
    tokenBucketThread2.join();
    tokenBucketThread3.join();
  }
}

class TokenBucketRateLimiter implements RateLimiterInterface {
  private final int maxTokens;
  private final int refillRate;

  static class Bucket {
    AtomicInteger tokens;
    long lastRefillTimestamp;

    Bucket(int tokens) {
      this.tokens = new AtomicInteger(tokens);
      this.lastRefillTimestamp = System.currentTimeMillis();
    }
  }

  private final ConcurrentHashMap<String, Bucket> userBuckets = new ConcurrentHashMap<>();
  private final ReentrantLock lock = new ReentrantLock();

  public TokenBucketRateLimiter(int maxTokens, int refillRate) {
    this.maxTokens = maxTokens;
    this.refillRate = refillRate;
  }

  private void refill(Bucket bucket) {
    long curr = System.currentTimeMillis();
    long timeElapsed = curr - bucket.lastRefillTimestamp;
    int tokensToAdd = (int) (timeElapsed * refillRate / 1000);
    bucket.tokens.set(Math.min(maxTokens, bucket.tokens.get() + tokensToAdd));
    bucket.lastRefillTimestamp = curr;
  }

  @Override
  public boolean isAllowed(String userId, Long timestamp) {
    userBuckets.putIfAbsent(userId, new Bucket(maxTokens));
    Bucket bucket = userBuckets.get(userId);
    lock.lock();
    try {
      refill(bucket);
      if (bucket.tokens.get() > 0) {
        bucket.tokens.decrementAndGet();
        return true;
      }
      return false;
    } finally {
      lock.unlock();
    }
  }
}
