import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class ConditionLock {
    private final ReentrantLock lock = new ReentrantLock();
    private final Condition condition = lock.newCondition();

    public void awaitSignal() throws InterruptedException {
        lock.lock();
        try {
            condition.await();
        } finally {
            lock.unlock();
        }
    }

    public void signal() {
        lock.lock();
        try {
            condition.signal();
        } finally {
            lock.unlock();
        }
    }

    public static void main(String[] args) throws InterruptedException {
        ConditionLock conditionLock = new ConditionLock();

        Thread waitingThread = new Thread(() -> {
            try {
                System.out.println("Waiting thread is about to wait...");
                conditionLock.awaitSignal();
                System.out.println("Waiting thread resumed");
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                System.err.println("Waiting thread was interrupted");
            }
        });

        waitingThread.start();
        Thread.sleep(1000);
        System.out.println("Main thread signaling...");
        conditionLock.signal();
        waitingThread.join();
        System.out.println("Main thread finished");
    }
}

