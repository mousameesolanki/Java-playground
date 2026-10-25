import java.util.concurrent.atomic.AtomicInteger;

public class RaceConditionDemo {
    static int counter = 0;
    static AtomicInteger atomicCounter = new AtomicInteger(0);
    public static void main(String[] args) throws InterruptedException {
        Thread t1 = new Thread(() -> {
            for (int i = 0; i < 10000; i++) {
                counter++; // Non-atomic operation
                atomicCounter.incrementAndGet(); // Atomic operation
            }
        });

        Thread t2 = new Thread(() -> {
            for (int i = 0; i < 10000; i++) {
                counter++; // Non-atomic operation
                atomicCounter.incrementAndGet(); // Atomic operation
            }
        });

        t1.start();
        t2.start();

        t1.join();
        t2.join();

        System.out.println("Final counter value (non-atomic): " + counter);
        System.out.println("Final counter value (atomic): " + atomicCounter.get());
    }
}