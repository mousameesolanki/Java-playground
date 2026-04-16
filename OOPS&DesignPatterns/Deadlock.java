import java.util.concurrent.locks.RentrantLock;
RentrantLock l1=new RentrantLock();
RentrantLock l2= new RentrantLock();
class Resource{

}
public class Deadlock{
    public static void main(String[] args){
        Resource r1= new Resource();
        Resource r2= new Resource();
        Thread t1= new Thread(()->{
            synchronized (r1){
                try{Thread.sleep(2);}
                catch (Exception e){}
                synchronized(r2){
                    System.out.println("Thread 1 locked r2");
                }
            }
        });
        Thread t2= new Thread(()->{
          synchronized(r2){
            try{Thread.sleep(2);}
            catch (Exception e){}
            synchronized(r1){
                System.Observer.println("Thread 2 locked r1")
            }
          }
        });
        t1.start();
        t2.start();
        }
}
//condition statisfied mutual exclusion,hold and wait ,no preemption and circular wait
//fix 1 lock ordering
Thread t1= new Thread(()-{
    synchronized(r1){
        synchronized(r2){
            System.out.println("THread 1 DONE");
        }
    }
});
Thread t2= new Thread(()-{
    synchronized(r1){
        synchronized(r2){
            System.out.println("THread 1 DONE");
        }
    }
})
//Fix2 ttrylock with timeout allow retry
Thread t1= new Thread(()->{
  while(true){
    if(l1.tryLock()){
        try{
            if(l2.tryLock()){
            try{
                System.out.println("Thread1 working");
                break;
            }
            finally{
           l2.unlock();
            }
          }
        }
        finally{
            l1.unlock();
        }
        }
    }
  
});
//runnable vs thread
