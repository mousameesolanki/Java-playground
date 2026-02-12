package MovieUploadpProcessing;
//using blocking queue for backpressure and worker count for cpu intensive task and concurrent hashmap for thread safe storage

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;

public class UploadPipeline {
  private final  BlockingQueue<UploadEvent> uploadQueue;
  private final ExecutorService workerPool;
  public UploadPipeline(int queuecap,int workerCount) {
    this.uploadQueue = new LinkedBlockingQueue<>(queuecap);
    this.workerPool = Executors.newFixedThreadPool(workerCount);
    startConsumers();
  
  }
   
  public void upload(MovieMetada metadata,String filepath) throws InterruptedException{
   UploadEvent event= new UploadEvent(metadata, filepath);
   uploadQueue.put(event);//blocks if full->prevent OOM
   System.out.println("Upload accepted"+ metadata.getTitle());
  }
  public void startConsumers(){
    Runnable dispatcher =()->{
      while(!Thread.currentThread().isInterrupted()){
        try{
          UploadEvent event= uploadQueue.take();//wait safely
          workerPool.submit(new Processor(event));
        }
        catch(InterruptedException e){
          Thread.currentThread().interrupt();
        }
      }
    };
    Thread dispatcherThread = new Thread(dispatcher);
    dispatcherThread.start();
  }
  public void shutdown(){
    workerPool.shutdown();
  }

}
