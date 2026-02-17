 import java.util.ArrayList;
import java.util.List;
 interface ObserverPattern {
void update(String message);
}
class EmailNotifyObserver implements ObserverPattern{
    @Override
    public void update(String message){
        System.out.println("Email notification sent"+message);
    }
}
class SMSNotifyObserver implements ObserverPattern{
    @Override
    public void update(String message){
        System.out.println("SMS notification sent"+message);
    }
}
interface SubjectObserver{
    void registerObserver(ObserverPattern observer);
    void removeObserver(ObserverPattern observer);
    void notifyObservers(String message);
}
class ObserverSubject implements SubjectObserver{
  private List< ObserverPattern> observers = new ArrayList<>();
@Override
  public void registerObserver(ObserverPattern observer){
    observers.add(observer);
  }
  @Override
  public void removeObserver(ObserverPattern observer){
    observers.remove(observer);
  }
  @Override
  public void notifyObservers(String message){
    for(ObserverPattern observer: observers){
        observer.update(message);
    }
  }
}
class Main{
    public static void main(String[] args){
        ObserverSubject subject = new ObserverSubject();
        EmailNotifyObserver emailObserver = new EmailNotifyObserver();
        SMSNotifyObserver smsObserver = new SMSNotifyObserver();
        subject.registerObserver(emailObserver);
        subject.registerObserver(smsObserver);
        subject.notifyObservers("New notification");
    }
}