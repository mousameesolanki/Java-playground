package CoupanLLd;

import java.lang.classfile.ClassFile.Option;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantLock;

//Functional Requirements
// Create coupon
// Configure
// couponCode
// expiry
// max global redemption = 10000
// max redemption per user =1
// User applies coupon during checkout
// Validate coupon
// Reserve coupon
// Redeem coupon after payment success
// Release reservation if payment fails
//discountType strategy
enum CoupanStatus{
    ACTIVE,
    RESERVED,
    USED,
    EXPIRED
}
enum ReservationStatus{
    RESERVED,
    CONFIRMED,
    EXPIRED
}
class Coupan {
    private final String code;//pk
    private double discountPercentage;
    private boolean isActive;
    //we should keep this AtomicInteger for thread safety and atomicity of increment operation
    private final AtomicInteger currentUsed = new AtomicInteger(0);
//this global limit can be int onlu current usage is atomic as we incr decr on it
private final int globalCount;
    private CoupanStatus status;
    private final LocalDateTime expiry;
    //version we can use for optimistic locking 
//OPTION 1 db OPTIMISTIC LOCKING update coupan set currentUsed=currentUsed+1 where code=? and currentUsed<globalCount
//VERSIONING Update coupan set currentUsed=currentUsed+1,version=version+1 where code=? and version=?

//user coupan composite pk insert into UserCoupan (userId,coupan)
//Duplicate db rejects
//But users insertion and global limit update should be in transaction
//Begin transaction
//insert into UserCoupan (userId,coupan)
//update coupan set currentUsed=currentUsed+1 where code=? and currentUsed<globalCount
//Commit transaction
//but at f;ash sale db becomes bottlenec for 100k rps set fotr update and avpid fast validation
//ttl till coupan expiry in redis for fast validation
//lua script  for validation entire one go ,redis is sinle threaded but in distributed system we can use redis cluster and sharding for high throughput

//why lua script if redis single threaded?-
//single operttion atomicity via redis single thread but if we have multiple operations like check if user has used coupan and check global count and then update both we need to do in one go so lua script is used for atomicity of multiple operations
//use two phase commit for coupan reddem after validation as payemnet can take time at time of checkout
//we can use distributed lock for coupan code to avoid race condition (one  user only get assigned coupan code at a time) but it will be bottleneck for high throughput so we can use redis lua script for atomicity and fast validation
//2PC- for coupan redemption after validation as payment can take time at checkout
//Apply coupan-> reserve-> payment-confirmation-> redeem coupan
//reservation-confirmed or can expire after some time if payment not done
//background scheduler cam relaese reservation
//how to perform distributed locking?
// reservation keys or redlock-> //https://redis.io/docs/manual/patterns/distributed-locks/
//redis setnx for distributed locking
    public Coupan(String code, double discountPercentage,int globalCount, LocalDateTime expiry) {
        this.code = code;
        this.discountPercentage = discountPercentage;
        this.isActive = true;
        //in enitity classes  db oprations class should be there an in entity repo the actual storage right now we are using in memory storage for demo
        this.globalCount = globalCount;
        this.expiry = expiry;

    }

    public String getCode() {
        return code;
    }

    public double getDiscountPercentage() {
        return discountPercentage;
    }

    public boolean isActive() {
        return isActive;
    }

    public void deactivate() {
        this.isActive = false;
    }
    public void incrementCoupanUsage(){
        //increment currentUsed in coupan
        currentUsed.incrementAndGet();
        return;
    }

    public AtomicInteger getCurrentUsed() {
        // TODO Auto-generated method stub
        return currentUsed;
    }
    public int getGlobalCount() {
        return globalCount;
    }
}
//for fast validation we can user redis usedcoupan key and coupanCounter for global count
//validate fast if coupan used by a user and should be belwo global count also
//redis atomic operations single threaded lua script
//will fill afetr coupen used by user
class UserCoupan{
    private final String id;
    private final String coupanCode;
    private boolean isUsed;
    private LocalDateTime usedAt;
    public UserCoupan(String id, String coupanCode) {
        this.id = id;
        this.coupanCode = coupanCode;
    }
    public String getId() {
        return id;
    }

}
class CoupanReservation{
    private final String id;
    private final String userId;
    private final String coupanCode;
    private ReservationStatus status;

    private LocalDateTime expiresAt;
    public CoupanReservation(String userId, String coupanCode, ReservationStatus status, LocalDateTime expiresAt) {
        this.id = UUID.randomUUID().toString();
        this.userId = userId;
        this.coupanCode = coupanCode;
        this.status=status;
        this.expiresAt=expiresAt;
    }
    //reserve coupan for user
    public void reserveCoupan(){
        this.status=ReservationStatus.RESERVED;
    }
    //check if reservation is expired
    public boolean isReservationExpired(){
        return LocalDateTime.now().isAfter(expiresAt);
    }
    //get userId and coupanCode
    public String getUserId() {
        return userId;
    }
    public String getCoupanCode() {
        return coupanCode;
    }   

}
//Inmemory repositories  Coupan and UserCoupan
class CoupanRepository{
   //keep te datstructure for storage and save and find operations
   //concurent haspmap handles syncronisation
  private final  Map<String,Coupan> coupanMap = new ConcurrentHashMap<>();
  public void save(Coupan coupan){
    coupanMap.put(coupan.getCode(),coupan);
  }
    public Optional<Coupan> findByCode(String code){
        return Optional.ofNullable(coupanMap.get(code));
    }
    
    public boolean existsByCode(String code){
        return coupanMap.containsKey(code);
    }
    //redeem coupan
    public boolean redeemCoupan(String code){
        Coupan coupan = coupanMap.get(code);
        if(coupan==null){
            return false;
        }
        //does concurrent haspmap provide both thread safe read and write?
        //yes concurrent hashmap provides thread safe read and write operations but we need to handle atomicity of multiple operations like check if user has used coupan and check global count and then update both we need to do in one go so lua script is used for atomicity of multiple operations
        //how in core java in ithis inmemory lld we will do that?
        //we can use atomic integer for currentUsed in coupan and check if user has used coupan in usercoupan repository and then increment currentUsed in coupan in one go
        //is this operation atomic? yes we are using atomic integer for currentUsed so it is thread safe and atomic operation
        //
        //check if coupan is active and not expired and currentUsed<globalCount
        if(!coupan.isActive() || coupan.getCurrentUsed().get()>=coupan.getGlobalCount() || coupan.getCurrentUsed().get()<0){
            return false;
        }
        //after hld in lld keep in mind the contraints and design and try to implememt via core java concepts
        //such as usercoupan unique insert
        //redeem should be thread safe and atomic operation as we are checking if user has already used and then incrementing the global count
        //increment currentUsed in coupan
        //is this increment thread safe? yes we are using atomic integer for currentUsed so it is thread safe and atomic operation
        //does compare and set needed here? no we are using atomic integer for currentUsed so it is thread safe and atomic operation
        //how CAS way wiil be?->
        coupan.incrementCoupanUsage();
        return true;
    }
    public void releaseCoupan(String code){
        Coupan coupan = coupanMap.get(code);
        if(coupan==null){
            return;
        }
        //decrement currentUsed in coupan
        coupan.getCurrentUsed().decrementAndGet();
    }
    
    //is this should be here not right it should be in Coupan entity class as we are using atomic integer for currentUsed and globalCount is final so we can use atomic increment operation on currentUsed in entity class itself
    
    // class UserCoupanRepository{
    
}
class UserCoupanRepository{
    //as the composite key of userid and code should only be unique we can use set of strings to store the composite key of userId and coupanCode
  private final Set<String> userCoupanSet = new ConcurrentHashMap<String,Boolean>().newKeySet();
  private String key(String userId,String coupanCode){
    return userId+"_"+coupanCode;
  }
  //methods redeemcoupan,add user used coupan,find user used coupan
  //redeem coupan should be atomic operation as we are checking if user has already used
  //in Concurrent hashmap keys set redeem operation is thread safe as it is atomic operation on set and we are using concurrent hashmap for thread safety
  public boolean redeemCoupan(String userId,String coupanCode){
    String key = key(userId,coupanCode);
    //if the key already exists in the set then return false else add the key to the set and return true
    return userCoupanSet.add(key);
  }
  public boolean hasUserUsedCoupan(String userId,String coupanCode){
    String key = key(userId,coupanCode);
    return userCoupanSet.contains(key);
  }
  public void removeUserCoupan(String userId,String coupanCode){
    String key = key(userId,coupanCode);
    userCoupanSet.remove(key);
  }
}
class ReservationRepository{
   //create reservation
   //find reservation by userId and coupanCode
   //confirm reservation
    //expire reservation
    private final Map<String,CoupanReservation> reservationMap = new ConcurrentHashMap<>();
    private String key(String userId,String coupanCode){
        return userId+"_"+coupanCode;
    }
    public void save(CoupanReservation reservation){
        String key = key(reservation.getUserId(),reservation.getCoupanCode());
        reservationMap.put(key,reservation);
    }
    //find reservation by userId and coupanCode
    public Optional<CoupanReservation> findByUserIdAndCoupanCode(String userId,String coupanCode){
        String key = key(userId,coupanCode);
        return Optional.ofNullable(reservationMap.get(key));
    }
    //confirm reservation

    

}
class LockManager{
    //for distributed locking we can use redis setnx for distributed locking
    //for this lld we will use in memory lock manager for demo
    //ensure instance generation is singleton 
   private final ConcurrentHashMap<String,ReentrantLock> locks = new ConcurrentHashMap<>();
   public ReentrantLock getLock(String code){
    return locks.computeIfAbsent(code,key->new ReentrantLock() );
   }
   //lock per coupan code
}
//

class CoupanService{
    //functional requirements here
    //validate coupan
    //reserve coupan
    //apply and decerment
    //user repository class/db mock
    private final UserCoupanRepository userCoupanRepository = new UserCoupanRepository();
    private final CoupanRepository coupanRepository = new CoupanRepository();
    private final LockManager lockManager = new LockManager();
  
  
    
   
    //appplycoupan bool 
    public boolean applyCoupan(String userid,String code){
        ReentrantLock lock = lockManager.getLock(code);
        lock.lock();
        try{
         //cjeck if coupan exists
         Optional<Coupan> coupan= coupanRepository.findByCode(code);
         if(coupan ==null)
            return false;
        //try to reddem userCoupna if true then increent current use and true oterwise in execption 
        //remove userCoupan
        }
        finally{
            lock.unlock();
        }
        return true;
    }
}


public class Main {
   //demo run from here
    public static void main(String[] args) {
        System.out.println("Hello, World!");
    }
    
}
