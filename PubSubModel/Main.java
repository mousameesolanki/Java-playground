package MovieUploadpProcessing;

import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Main {
  public static void main(String[] args) throws Exception {
    UploadPipeline pipeline = new UploadPipeline(50,4);
    ExecutorService sExecutorService = Executors.newFixedThreadPool(10);
   for( int i=1;i<=20;i++){
    int id=i;
    sExecutorService.submit(()->{
      try{
       pipeline.upload(new MovieMetada("Movie"+id,"Director"+id,2020+id),"/path/movie"+id+".mp4");
      }
      catch(InterruptedException ignored ){}
    });
   }
   sExecutorService.shutdown();
  }
}
