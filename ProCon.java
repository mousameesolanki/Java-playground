import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.RejectedExecutionException;

public class ProCon {
    private final BlockingQueue<Runnable> queue= new LinkedBlockingQueue<>(100);
    private final ExecutorService workers;
    private volatile boolean shutdown;//for gracefull shutdown
    public ProCon(int cnt){
        
        workers= Executors.newFixedThreadPool(cnt);
       for(int i=0;i<cnt;i++){
        workers.submit(this::runWorker);
       }
    }
    public void runWorker(){
        while(!shutdown || !queue.isEmpty()){
            try{
                Runnable task= queue.poll();
                if(task!=null)
                    task.run();
            }
            catch(InterruptedException e){
                Thread.currentThread().interrupt();
                return;
            }
        }
    }
    public void submit(Runnable task){
        if(shutdown) throw new RejectedExecutionException();
        queue.offer(task);
    }
    public void shutdown(){
        shutdown=true;
        workers.shutdown();//signal 
    }
}
