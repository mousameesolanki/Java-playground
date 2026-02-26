import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.concurrent.*;;

class JavaFeatures {
    public static void main(String[] args) {
      Optional<String> optionalString = Optional.ofNullable(null);
      String result = optionalString.orElse("Default Value");
      System.out.println(result);
      //java 8 features
      LocalDate currentDate = LocalDate.now();
      System.out.println("Current date: " + currentDate);
      LocalTime currentTime = LocalTime.now();
      System.out.println("Current time: " + currentTime);
      LocalDateTime currentDateTime = LocalDateTime.now();
      System.out.println("Current date and time: " + currentDateTime);

      //streams and lambda expressions
      List<String> names = List.of("Alice", "Bob", "Charlie", "David", "Eve");
      Map<String,List<String>> grouped= names.stream().collect(Collectors.groupingBy(name -> name.substring(0, 1)));
      System.out.println("Grouped names: " + grouped);

      interface FunctionalInterfaceExample {
          void execute();
      }
      //lamba expression for functional interface
      FunctionalInterfaceExample example = () -> System.out.println("Executing functional interface");
      Runnable r= ()-> System.out.println("Running in a thread");
      new Thread(r).start();
      //default and static methods in interfaces
      interface DefaultStaticInterface {
          default void defaultMethod() {
              System.out.println("This is a default method");
          }
          static void staticMethod() {
              System.out.println("This is a static method");
          }
      }
      //stream API for collection processing
      List<Integer> numbers = List.of(1, 2, 3, 4, 5);
      //map filter and reduuce
      int sum= numbers.stream().filter(val-> val%2==0).mapToInt(Integer::intValue).sum();
      System.out.println("Sum of even numbers: " + sum);

      //method references and constructor references
      //method reference a short hand for lambda expression that calls a method
      //types
      //static method reference
      class StaticMethodReference {
          public static void print(Integer message) {
              System.out.println(message);
          }
      }
      //call static method reference
      numbers.forEach(StaticMethodReference::print);
      //instance method reference
      class InstanceMethodReference {
          public void print(Integer message) {
              System.out.println(message);
          }
      }
      numbers.forEach(new InstanceMethodReference()::print);
      //constructor reference
      class ConstructorReference {
          String name;
          ConstructorReference(String name) {
              this.name = name;
          }
          public void print() {
              System.out.println(name);
          }
      }
      java.util.function.Function<String, ConstructorReference> constructorRef = ConstructorReference::new;
      ConstructorReference instance = constructorRef.apply("Example");
      instance.print();

     
      //java 17 features
      //record class
      record Person(String name, int age) {}
      Person person = new Person("Alice", 30);
      System.out.println("Person: " + person.name() + ", Age: " + person.age());
      //sealed class
      // sealed interface Shape permits Circle, Rectangle {
      //     double area();
      // }
      // final class Circle implements Shape {
      //     private double radius;
      //     Circle(double radius) {
      //         this.radius = radius;
      //     }
      //     @Override
      //     public double area() {
      //         return Math.PI * radius * radius;
      //     }
      // }
      //virtual  threads java 21  simulation of virtual threads using executor service
      //java.util.concurrent.ExecutorService executor = java.util.concurrent.Executors.newVirtualThreadPerTaskExecutor();


      //normal exceutor for comparison
      java.util.concurrent.ExecutorService normalExecutor = java.util.concurrent.Executors.newFixedThreadPool(10);

     // List<String> name=
     //as concurrent hashmaps not allow null key what methods to use to get a key value pair if the key is not present
      // we can use the getOrDefault method to get a value for a key if the key is not present in the map, it will return the default value that we provide
        java.util.concurrent.ConcurrentHashMap<String, String> map = new java.util.concurrent.ConcurrentHashMap<>();
        String value = map.getOrDefault("key", "default value");
        System.out.println("Value: " + value);
        Map<String, String> unmodifiableMap = Map.of("key1", "value1", "key2", "value2");
        //what happens if we try to get a value for a key that is not present in the unmodifiable map
        Map<String,String> mp= new HashMap<>();
        mp.put("key1", "value1"); 
        mp.get(null);
        //what is Futures in java
        //Futures in java are used to represent the result of an asynchronous computation. It allows
        //it allows us to write non-blocking code by allowing us to retrieve the result of a computation at a later time when it is available. We can use the Future interface and the ExecutorService to submit tasks and retrieve their results asynchronously.
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Future<String> future = executor.submit(() -> {
            Thread.sleep(2000); // Simulate a long-running task
            return "Result of the asynchronous computation";
        });
        //wait method will block  main  thread but sleep method will block current thread only
        try {
            while (!future.isDone()) {
                Thread.sleep(100); // Simulate checking for completion every 100ms
            }
            String resultFuture = future.get(); // This will block until the result is available how toavoid blocking here we can use future.isDone() to check if the result is available before calling get() method
            System.out.println("Future result: " + resultFuture);
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        } finally {
            executor.shutdown();
        }
      //  unmodifiableMap.get(null);
      
    }
    //semaphore in java
    //A semaphore is a synchronization aid that restricts access to a shared resource by multiple threads. It maintains a set of permits, and threads can acquire or release permits to access the resource. Semaphores are useful for controlling access to resources in concurrent programming.
    //example of semaphore in java
    class SemaphoreExample {
        private final Semaphore semaphore = new Semaphore(3); // Allow up to 3 threads to access the resource

        public void accessResource() {
            try {
                semaphore.acquire();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
          }
      
        }
        //paging in os
        //is the way to manage memory in an operating system by dividing the memory into fixed-size pages and mapping them to physical memory. It allows the operating system to efficiently use memory and provides a way to handle memory fragmentation. When a process needs to access a page that is not currently in physical memory, 
        // a page fault occurs, and the operating system retrieves the page from disk and loads it into physical memory.
}//what is fragmentation in memory management
//Fragmentation in memory management refers to the condition where memory is used inefficiently, leading to wasted space. There are two types of fragmentation: external fragmentation and internal fragmentation. 
// External fragmentation occurs when there are small, unused blocks of memory scattered throughout the system, making it difficult to allocate large contiguous blocks of memory. Internal fragmentation occurs when allocated memory blocks are larger than the requested memory, resulting in unused space within the allocated block.
//  Both types of fragmentation can lead to performance issues and inefficient use of memory resources.
//segmentation in memory management
//Segmentation in memory management is a technique where the memory is divided into variable-sized segments based on the logical divisions of a program, such as code, data, and stack segments. 
// Each segment can grow or shrink independently, allowing for more efficient use of memory and better protection between different segments. Segmentation helps to manage memory more flexibly and can 
// reduce fragmentation compared to fixed-size paging.

