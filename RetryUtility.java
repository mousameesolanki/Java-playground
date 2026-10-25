import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

public class RetryUtility {
   private int retryCnt;
   //exponential backoffs of 
   private static final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
   //execute an async supplier with retries and exponential backoff
   public static <T> CompletableFuture<T> executeWithRetry(Supplier<CompletableFuture<T>> supplier,int maxRetries,long initialDelayMillis){
    CompletableFuture<T> result =new CompletableFuture<>();
    retry(
        supplier,
        maxRetries,
        initialDelayMillis,
        1,
        result
    );
    return result;
   }
   private static <T> void retry(Supplier<CompletableFuture<T>> supplier,int maxRetries,long delay,int attempt,CompletableFuture<T> resFuture){
    supplier.get().whenComplete(value,throwable)->{
        if(throwable==null){
            resFuture.complete(value);
            return;
        }
        if(attempt>maxRetries){
            resFuture.completeExceptionally(throwable);
            return;
        }
        System.out.println("Retry"+attempt+"after"+delay+ "ms");
        scheduler.schedule(()->retry(supplier, maxRetries, delay*2, attempt+1, resFuture),delay,TimeUnit.MILLISECONDS);
    }
   } 
}
