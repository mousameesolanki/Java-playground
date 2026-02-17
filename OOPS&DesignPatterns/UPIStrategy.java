//setter injection
//Dependency Injection is a design pattern that allows us to inject dependencies into a class rather than creating them within the class. This promotes loose coupling and makes the code more testable and maintainable.
//  In setter injection, we use setter methods to inject the dependencies into the class. The @Autowired annotation is used to indicate that the dependency should be injected by Spring.
//Dependency inversion is a principle that states that high-level modules should not depend on low-level modules, but both should depend on abstractions. In other words, the details of
//  how a class works should be hidden from the user, and the user should only interact with the class through its public interface. This promotes loose coupling and makes the code more flexible and maintainable.
class Car{
  Engine engine= new Engine();
}
class Car{
  //we cant use final keyword here because we are using setter injection and the engine variable will be initialized after the object is created
  private Engine engine;
  @Autowired
  public void setEngine(Engine engine){
    this.engine = engine;
  }
}
//class contructor injection
class Car{
  //final keyword is used to make sure that the engine variable is not changed after it is initialized in the constructor
  private final Engine engine;
  @Autowired
  public Car(Engine engine){
    this.engine = engine;
  }
}
  //Field injection
  //chance of null pointer exception because the engine variable is not initialized until the object is created and the dependency is injected, so if we try to access the engine variable
  //  before it is initialized, we will get a null pointer exception.
  class Car{  
  @Autowired
  private Engine engine;  
  void start(){  
    engine.start();//NullPointerException

  }
 }
 //factory pattern
 class SimpleNotificationFactory {
    public static Notification createNotification(String type) {
        return switch (type) {
            case "EMAIL" -> new EmailNotification();
            case "SMS" -> new SMSNotification();
            case "PUSH" -> new PushNotification();
            default -> throw new IllegalArgumentException("Unknown type");
        };
    }
}

class NotificationService {
    public void sendNotification(String type, String message) {
        Notification notification = SimpleNotificationFactory.createNotification(type);
        notification.send(message);
    }
}
interface Notification {
    void send(String message);
}
class EmailNotification implements Notification {
    @Override
    public void send(String message) {
        System.out.println("Sending email: " + message);
    }
}

//strategy pattern
interface TextFormatter {
    String format(String text);
}
class PlainTextFormatter implements TextFormatter {
    @Override
    public String format(String text) {
        return text;
    }
}
//context class
class TextEditor {
    private TextFormatter formatter;

    public TextEditor(TextFormatter formatter) {
        this.formatter = formatter;
    }

    public void setFormatter(TextFormatter formatter) {
        this.formatter = formatter;
    }
    public void publishText(String text) {
        String formattedText = formatter.format(text);
        System.out.println(formattedText);
    }
}
class Main {
    public static void main(String[] args) {
        TextEditor editor = new TextEditor(new PlainTextFormatter());
        editor.publishText("Hello, World!");

        // Change the formatter at runtime
        editor.setFormatter(new UpperCaseFormatter());
        editor.publishText("Hello, World!");
    }
}
//Observer pattern
interface Observer {
    void update(String message);
}
interface SubjectObservable {
    void addObserver(Observer observer);
    void removeObserver(Observer observer);
    void notifyObservers(String message);
}
class Subject implements SubjectObservable {
    private List<Observer> observers = new ArrayList<>();

    public void addObserver(Observer observer) {
        observers.add(observer);
    }

    public void removeObserver(Observer observer) {
        observers.remove(observer);
    }

    public void notifyObservers(String message) {
        for (Observer observer : observers) {
            observer.update(message);
        }
    }
}
class ConcreteObserver implements Observer {
    private String name;

    public ConcreteObserver(String name) {
        this.name = name;
    }

    @Override
    public void update(String message) {
        System.out.println(name + " received: " + message);
    }
}
//simulate observer pattern
class Main {  
    public static void main(String[] args) {  
        Subject subject = new Subject();  
        Observer observer1 = new ConcreteObserver("Observer 1");  
        Observer observer2 = new ConcreteObserver("Observer 2");  
        subject.addObserver(observer1);  
        subject.addObserver(observer2);  
        subject.notifyObservers("Hello, Observers!");  
    }  
}
//Notification observer pattern
interface NotificationObserver {
    void update(String message);
} 
interface NotificationSubject {
    void addObserver(NotificationObserver observer);
    void removeObserver(NotificationObserver observer);
    void notifyObservers(String message);
} 
class NotificationService implements NotificationSubject {
    private List<NotificationObserver> observers = new ArrayList<>();

    public void addObserver(NotificationObserver observer) {
        observers.add(observer);
    }

    public void removeObserver(NotificationObserver observer) {
        observers.remove(observer);
    }

    public void notifyObservers(String message) {
        for (NotificationObserver observer : observers) {
            observer.update(message);
        }
    }
}
class User implements NotificationObserver {
    private String name;

    public User(String name) {
        this.name = name;
    }

    @Override
    public void update(String message) {
        System.out.println(name + " received notification: " + message);
    }
}
interface StrategyPattern{
  void pay();
}
 class UPIStrategy implements StrategyPattern{
    @Override
    public void pay(){
        System.out.println("UPI");
    }
}
class StrategyContext{
    StrategyPattern strategy;
    StrategyContext(StrategyPattern pattern){
    this.strategy=pattern;
    }
    public void setStrategy(StrategyPattern strategy){
     this.strategy=strategy;
    }
    public void doPayment(){
        strategy.pay();
        
    }
}
class CreditCardStrategy implements StrategyPattern{
    @Override
    public void pay(){
        System.out.println("CreditCard");
    }
}
class Main{
     public static void main(String[] args) {  
    
        StrategyContext sc = new StrategyContext(new UPIStrategy());
       sc.doPayment();
       sc.setStrategy(new CreditCardStrategy());
       sc.doPayment(); 
     }
}