//craete a payment sytem that can pay using different payment methods
//its should be consistent and easy to use
//payement strategy we can use  and simulate payment via payment service
//how to store audit logs for payment transactions
//how to ensure transactional integrity and consistency in the payment system
//how to handle concurrent payment requests and ensure thread safety
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
//core entities
//payemnt class having id and payment info
//Enum of payment methods
class Payment {
    //should keep unique id for each payment transaction
    private final String uuid;
    private final double amount;
    private PaymentMethod method;
    public Payment(String uuid, double amount, PaymentMethod method){
        this.uuid = uuid;
        this.amount = amount;
        this.method = method;
    }
    public String getUuid(){
        return uuid;
    }
    public double getAmount(){
        return amount;
    }
    public PaymentMethod getMethod(){
        return method;
     }
  }
enum PaymentMethod {
    CREDIT_CARD, DEBIT_CARD, PAYPAL, UPI
}
// payment strategy creation factory 
class PaymentStrategyFactory {
    public static PaymentStrategy getPaymentStrategy(PaymentMethod method){
       //switch case for different payment methods and return the corresponding strategy
        switch(method){
            case CREDIT_CARD:
                return new CreditCardPaymentStrategy();
            case DEBIT_CARD:
                return new DebitCardPaymentStrategy();
            case PAYPAL:
                return new PayPalPaymentStrategy();
            case UPI:
                return new UPIPaymentStrategy();
            default:
                throw new IllegalArgumentException("Invalid payment method");
        }
        
    }
}

interface PaymentStrategy{
  void pay();
}

class CreditCardPaymentStrategy implements PaymentStrategy{
    @Override
    public void pay(){
        System.out.println("Payment made using credit card");
    }
}
class DebitCardPaymentStrategy implements PaymentStrategy{
    @Override
    public void pay(){
        System.out.println("Payment made using debit card");
    }
}
//class for business logic PayemntService
class PaymentService {
private PaymentStrategy paymentStrategy;
public PaymentService(PaymentStrategy paymentStrategy){
    this.paymentStrategy = paymentStrategy;
}
//create payment first assign it uuid we will not get this from user
public Payment createPayment(double amount, PaymentMethod method){
    String uuid = java.util.UUID.randomUUID().toString();
    return new Payment(uuid, amount, method);
}
public void processPayment(Payment payment){
    //process payment using the payment strategy
    paymentStrategy.pay();
    //store payment transaction and audit log in the storage class
    InMemoryStorage storage = InMemoryStorage.getInstance();
    storage.storePayment(payment);
    storage.storeAuditLog(payment.getUuid(), "Payment of " + payment.getAmount() + " made using " + payment.getMethod());
  //create sstrategy using factory
  
}
}
//inmemory storage class to demo for storing payment transactions and audit logs
//how to ensure thread safety in the storage class when multiple threads are trying to store payment transactions and audit logs concurrently
 //how to make the storage class a singleton class to ensure that we have only one instance of the storage class to store payment transactions and audit logs
 class InMemoryStorage {
  //this should be a singleton class to ensure that we have only one instance of the storage class to store payment transactions and audit logs
  //which type of we should use for storing payment transactions and audit logs
  //we can use a concurrent hashmap to store payment transactions with uuid as key and payment object as value, and another concurrent hashmap to store audit logs with uuid as key and log message as value

  //holder class for singleton pattern
  private static class Holder {
    private static final InMemoryStorage INSTANCE = new InMemoryStorage();
  }
  public static InMemoryStorage getInstance(){
    return Holder.INSTANCE;
  }
  private final ConcurrentHashMap<String, Payment> paymentMap ;
  private final ConcurrentHashMap<String, String> auditLogMap ;
  //can this violate singleton
  private InMemoryStorage(){
    this.paymentMap = new ConcurrentHashMap<>();
    this.auditLogMap = new ConcurrentHashMap<>();
  }
 //we not instantiate  map in this class we can instantiate it in the main class and pass it to the payment service class to store payment transactions and audit logs
  public void storePayment(Payment payment){
    paymentMap.put(payment.getUuid(), payment);
  }
  public void storeAuditLog(String uuid, String logMessage){
    auditLogMap.put(uuid, logMessage);
  }
  //getter methods for payment transactions and audit logs
  public Payment getPayment(String uuid){
    return paymentMap.get(uuid);
  }
  public String getAuditLog(String uuid){
    return auditLogMap.get(uuid);
  }

}
//main class to demo the payment system
class Main {
    public static void main(String[] args) {
       //we will decide payment method when we create a payment transaction and then we will create a payment strategy using the factory and pass it to the payment service class to process the payment
        PaymentService paymentService = new PaymentService(PaymentStrategyFactory.getPaymentStrategy(PaymentMethod.CREDIT_CARD));
        InMemoryStorage storage = InMemoryStorage.getInstance();
        Payment payment = paymentService.createPayment(100.0, PaymentMethod.CREDIT_CARD);
        paymentService.processPayment(payment);
        //store after paymeent for fast process of payment and audit and storage will be transactional and consistent because we are using concurrent hashmap to store payment transactions and audit logs, so it will ensure thread safety when multiple threads are trying to store payment transactions and audit logs concurrently
        storage.storePayment(payment);
        storage.storeAuditLog(payment.getUuid(), "Payment of " + payment.getAmount() + " made using " + payment.getMethod());
        //get payment transaction and audit log from storage
        Payment storedPayment = storage.getPayment(payment.getUuid());
        String auditLog = storage.getAuditLog(payment.getUuid());

    }
}