//Adapter lets incompatible  interfaces work togethr withouyt modifying eitgher side
//critical when integrating 
interface Target {
    void request();
}
class Adaptee {
    void specificRequest() {
        System.out.println("Specific request");
    }
}
class Adapter implements Target {
    private Adaptee adaptee;
    public Adapter(Adaptee adaptee) {
        this.adaptee = adaptee;
    }
    @Override
    public void request() {
        adaptee.specificRequest();
    }
}
public class AdapterPattern {
    //adapter holding the instance of the adaptee and implementing the target interface
}
