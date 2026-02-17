//
//sliding window algorithm for rate limiting
//we can use a queue to store the timestamps of the requests and check if the number of requests in the last minute exceeds the limit

import java.util.Deque;
import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;
interface RateLimiterInterface{
  boolean isAllowed(String userId,Long timestamp);
}

class RequestInfo{
  private Long timestamp;
  private String userId;
  public RequestInfo(Long timestamp, String userId){
    this.timestamp = timestamp;
    this.userId = userId;
  }

}
//separete config class for configurable limit and time window
class Config{
  private int limitPerMinute;
  public Config(int limit){
    this.limitPerMinute = limit;
  }
  public int getLimit(){
    return limitPerMinute;
  }
}
//simulating redis with in-memory storage
//concurrent hasmap why? because multiple threads can access the storage simultaneously and we need to ensure thread safety
class InMemoryStorage{
  private final ConcurrentHashMap<String, Deque<Long>> userRequestMap = new ConcurrentHashMap<>();
  public void addRequest(String userId,Long timestamp){
    Deque<Long> list = userRequestMap.computeIfAbsent(userId, k -> new LinkedList<>());
    list.add(timestamp);
  }
  public Deque<Long> getRequestTimestamps(String userId){
    return userRequestMap.get(userId);
  }
  public void removeOldRequests(String userId, Long currentTime){
    Deque<Long> timestamps = userRequestMap.get(userId);
    if (timestamps != null) {
      while(!timestamps.isEmpty() && currentTime - timestamps.peek() > 60000){
        timestamps.poll();
      }
    }
  }
}
class RateLimiter implements RateLimiterInterface{
 private Config config;
 private InMemoryStorage storage;
 private ReentrantLock lock = new ReentrantLock();
 RateLimiter(Config config, InMemoryStorage storage){
   this.config = config;
   this.storage = storage;
  }
  //need to make it thread safe because multiple threads can access the rate limiter simultaneously and we need to ensure that the requests are processed in a thread safe manner
  //can we put a synchronized block here? yes we can but it will reduce the performance of the system because it will block other threads from accessing the rate limiter while one thread is processing a request, 
  // so we can use a lock to ensure that only one thread can access the rate limiter at a time 
  // and other threads can wait for the lock to be released before accessing the rate limiter
  @Override
  public boolean isAllowed(String userId,Long timestamp){
   Deque<Long> queue = storage.getRequestTimestamps(userId);
    lock.lock();
    try{
      storage.removeOldRequests(userId, timestamp);
      int currentSize = (queue == null) ? 0 : queue.size();
    System.out.println(Thread.currentThread().getName() + " - Current requests: " + currentSize + " / " + config.getLimit());
      if(queue == null || queue.size() < config.getLimit()){
        storage.addRequest(userId, timestamp);
        return true;
      }
      return false;
    }
    finally{
      lock.unlock();
    }
  }
}
//static class as we want only single instance of the bucket for each user and we can use the bucket to store the tokens and refill the bucket at a fixed rate,
//  and when a request comes in, we can check if there are enough tokens in the bucket to allow the request, 
  // if yes then we can allow the request and remove the tokens from the bucket, if no then we can deny the request
class TokenBucketRateLimiter implements RateLimiterInterface{
  //token bucket algorithm for rate limiting
  //we can use a bucket to store the tokens and refill the bucket at a fixed rate, and when a request comes in, we can check if there are enough tokens in the bucket to allow the request, 
  // if yes then we can allow the request and remove the tokens from the bucket, if no then we can deny the request
  private final int maxTokens;
  private final int refillRate; 
  static class Bucket{
   int tokens;
   long lastRefillTimestamp;
   Bucket(int tokens){
     this.tokens = tokens;
     this.lastRefillTimestamp = System.currentTimeMillis();
   }
}
  // refill rate for tokens per second
  ConcurrentHashMap<String,Bucket> userBuckets = new ConcurrentHashMap<>();
  ReentrantLock lock =new ReentrantLock();
  public TokenBucketRateLimiter(int maxTokens, int refillRate){
    this.maxTokens = maxTokens;
    this.refillRate = refillRate;
  }
  private void refill(Bucket bucket){
    long curr = System.currentTimeMillis();
    long  timeelapsed= curr-bucket.lastRefillTimestamp;
    int tokensToAdd=(int) timeelapsed*refillRate/1000;//per second refill rate
    bucket.tokens= Math.min(maxTokens,bucket.tokens+tokensToAdd);
    bucket.lastRefillTimestamp = curr;
    
  }
  @Override
  public boolean isAllowed(String userId,Long timestamp){
     userBuckets.putIfAbsent(userId,new Bucket(maxTokens));
     Bucket bucket = userBuckets.get(userId);
   lock.lock();

   try{
    refill(bucket);
    if(bucket.tokens>0){
      bucket.tokens-=1;
      return true;
    }
    return false;
   }
   finally{
    lock.unlock();
   }
    
  }
}

class Main{
  public static void main(String[] args){
    //i minute window in milliseconds
    Config config = new Config(2); // 2 requests per minute
    InMemoryStorage storage = new InMemoryStorage();
    RateLimiter rateLimiter = new RateLimiter(config, storage);
    String userId = "user1";
    Long timestamp = System.currentTimeMillis();
    //CALL ratelimtre
    //lock
    
    if(rateLimiter.isAllowed(userId,timestamp)){
      System.out.println("Request allowed");
    }
    else{
      System.out.println("Request denied");
    } 
  
    //show thread safety by simulating multiple threads making request concurrently and avoid race conditions for a particular user
    Runnable task = () -> {
      if(rateLimiter.isAllowed(userId,System.currentTimeMillis())){
        System.out.println("Request allowed for "+Thread.currentThread().getName());
      }
      else{
        System.out.println("Request denied for "+Thread.currentThread().getName());
      }
    };
    Thread thread1 = new Thread(task);
    Thread thread2 = new Thread(task);  
    Thread thread3 = new Thread(task);
    thread1.start();
    thread2.start();
    thread3.start();
    //simulate for token bucket rate limiter
    TokenBucketRateLimiter tokenBucketRateLimiter = new TokenBucketRateLimiter(2,1);
    Runnable tokenBucketTask = () -> {
      if(tokenBucketRateLimiter.isAllowed(userId,System.currentTimeMillis())){
        System.out.println("Request allowed for token bucket "+Thread.currentThread().getName());
      }
      else{
        System.out.println("Request denied for token bucket "+Thread.currentThread().getName());
      }
    };
    Thread tokenBucketThread1 = new Thread(tokenBucketTask);
    Thread tokenBucketThread2 = new Thread(tokenBucketTask);
    Thread tokenBucketThread3 = new Thread(tokenBucketTask);    
    tokenBucketThread1.start();
    tokenBucketThread2.start();
    tokenBucketThread3.start();

  }
}