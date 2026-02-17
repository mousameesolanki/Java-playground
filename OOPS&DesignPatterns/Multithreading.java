//“Design a system that handles concurrent requests using threads, thread pools, and queueing.
//The system should be able to process multiple requests simultaneously while ensuring thread safety and efficient resource management.”
////Client → RequestQueue → ThreadPool → WorkerThreads → RequestProcessor
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;

class Request{
 private String  id;
}
class RequestQueue{
 private BlockingQueue<Request> queue;
 public RequestQueue(BlockingQueue<Request> queue){
   this.queue = queue;
 }
  public void put(Request request) throws InterruptedException {
    queue.put(request); // Add a request to the queue block if its full
  }
  public Request take() throws InterruptedException {
    return queue.take(); // Take a request from the queue 
  }

}
class Processor {
  public void process(Request request){
    // Process the request here

  
  try{
    // Simulate processing time
    Thread.sleep(1000);
  }
  catch(InterruptedException e){
    Thread.currentThread().interrupt(); // Handle interruption
  }
}
}
class WorkerThread implements Runnable{
  private RequestQueue requestQueue;
private Processor processor;
  public WorkerThread(RequestQueue requestQueue, Processor processor  ){
    this.requestQueue = requestQueue;
    this.processor = processor;
  }
  @Override
  public void run() {
    while (true) {
      try {
        Request request = requestQueue.take(); // Take a request from the queue
        processRequest(request); // Process the request
      } catch (InterruptedException e) {
        Thread.currentThread().interrupt(); // Handle interruption
        break; // Exit the loop if interrupted
      }
    }
  }
  private void processRequest(Request request){
    // Process the request here
    processor.process(request);
  }

}
class ClientSimulator{
  //thread pool
  //simulate clients sending requests to the system
  RequestQueue requestQueue;
  public ClientSimulator(int cap){
    this.requestQueue = new RequestQueue(new java.util.concurrent.LinkedBlockingQueue<>(cap)); // Initialize the request queue with a capacity
  }
  ExecutorService executorService = Executors.newFixedThreadPool(10); // Create a thread pool with 10 threads
 public void simulateClients(){
   for (int i = 0; i < 10; i++) {
     final int requestId = i;
     executorService.submit(() -> {
       Request request = new Request("Request " + requestId);
       try {
        
         requestQueue.put(request); // Add the request to the queue
       } catch (InterruptedException e) {
         Thread.currentThread().interrupt(); // Handle interruption
       }
     });
   }
   executorService.shutdown(); // Shutdown the executor service after submitting all tasks
 }
}
