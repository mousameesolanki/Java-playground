import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.function.Supplier;
class Connection{

}
public class BoundedConnectionPool {
    private final BlockingQueue<Connection> pool;
    public BoundedConnectionPool(int size,Supplier<Connection> factory){
        pool = new LinkedBlockingDeque<>(size);
        for(int i=0;i<size;i++)
            pool.offer(factory.get());
    }
    public Connection acquire() throws InterruptedException{
        return pool.take();//blocks if pool empty -natural backpressure
    }
    public Connection acquire(long timeout) throws InterruptedException, TimeoutException{
      Connection c= pool.poll(timeout,TimeUnit.MICROSECONDS);
      if(c==null) throw new TimeoutException("No connection");
      return c;
    }
    public void release(Connection c){
        pool.offer(c);//never blocks =bounded queue alreaddy sized top pool
    }
    public static void main(String[] args) {
        BoundedConnectionPool pool= new BoundedConnectionPool(5, new Supplier<Connection>() {
           return new factory; 
        });
        Connection c=pool.acquire();
        try{
            c.executeQuery(sql);
        }
        finally{
            pool.release(c);
        }
    }
}
