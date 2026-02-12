//Singleton Pattern is a creational design pattern that guarantees a class has only one instance and provides 
// a global point of access to it.
//It involves only one class which is responsible for instantiating itself, making sure it creates not more than one instance.
/* 
To implement the singleton pattern, we must prevent external objects from creating instances of the singleton class. Only the singleton class should be permitted to create its own objects.

Additionally, we need to provide a method for external objects to access the singleton object.*/
class LazySingleton {
  //single instance ->static
  private static LazySingleton instance;
  //private cons
  private LazySingleton(){}
  public static LazySingleton getInstance(){
    if(instance== null){
      instance=new LazySingleton();
    }
    return instance;
  }

}
class ThreadSafeSingleton{
  private static ThreadSafeSingleton instance;
  private ThreadSafeSingleton() {}
  public static synchronized ThreadSafeSingleton getInstance(){
    if(instance ==null){
      instance =new ThreadSafeSingleton();
    }
    return instance;
  }
}
//We check the same condition one more time because multiple threads may have passed the first check.
//The instance is created only if both checks pass.
class DoubleLockSingleton{
  private static volatile DoubleLockSingleton instance;
  private  DoubleLockSingleton() {}
  public static DoubleLockSingleton getInstance(){
    if(instance ==null){
      // // Synchronize on the class object
      synchronized (DoubleLockSingleton.class){
         // Second check (synchronized)
          if( instance==null){
            instance =new DoubleLockSingleton();
          }
      }
    }
    return instance;
  }
}
/*While it is inherently thread-safe, it could potentially waste resources if the singleton instance is never used by the client application.*/ */
class EagerSingleton{
  private static final EagerSingleton instance= new EagerSingleton();
  private EagerSingleton(){}
  public static EagerSingleton getInstance(){
    return instance;
  }
}
/*This implementation uses a static inner helper class to hold the singleton instance. The inner class is not loaded into memory until it's referenced for the first time in the getInstance() method.

It is thread-safe without requiring explicit synchronization. */
class BillPughSingleton{
  private BillPughSingleton(){}
  private static class SingletonHelper{
    private static final BillPughSingleton instance= new BillPughSingleton();
  }
  public static BillPughSingleton getInstance(){
    return SingletonHelper.instance;
  }
}
/*When the getInstance() method is called for the first time, it triggers the loading of the SingletonHelper class.
When the inner class is loaded, it creates the INSTANCE of BillPughSingleton.
The final keyword ensures that the INSTANCE cannot be reassigned.

The Bill Pugh Singleton implementation, while more complex than Eager Initialization provides a perfect balance of lazy
 initialization, thread safety, and performance, without the complexities of some 
other patterns like double-checked locking. */

/*Static Block Initialization
This is similar to eager initialization, but the instance is created in a static block.

It provides the ability to handle exceptions during instance creation, which is not possible with 
simple eager initialization. */
class StaticBlockSingleton{
  private static StaticBlockSingleton instance;
  private StaticBlockSingleton() {}
  static{
    /*The static block is executed when the class is loaded by the JVM.
If an exception occurs, it's wrapped in a RuntimeException. */
    try{
      instance =new StaticBlockSingleton();

    }
    catch(Exception e){

    }
  }
  /*It is thread safe but not lazy-loaded, which might be a drawback if the initialization is 
  resource-intensive or time-consuming. */
  public static StaticBlockSingleton getInstance(){
    return instance;
  }
}
/* Enum Singleton
In this method, the singleton is declared as an enum rather than a class.

Java ensures that only one instance of an enum value is created, even in a multithreaded environment.

The Enum Singleton pattern is the most robust and concise way to implement a singleton in Java. */
enum EnumSingleton{
  INSTANCE;
   public void doSomething(){

   }
}
/*It may not always be suitable especially if you need to extend a class or if lazy initialization is a strict requirement. */