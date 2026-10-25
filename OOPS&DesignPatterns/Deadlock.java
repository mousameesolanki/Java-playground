

class Resource {
}

public class Deadlock {
    public static void main(String[] args) {
        Resource r1 = new Resource();
        Resource r2 = new Resource();

        Thread t1 = new Thread(() -> {
            synchronized (r1) {
                try {
                    Thread.sleep(2);
                } catch (InterruptedException e) {
                }
                synchronized (r2) {
                    System.out.println("Thread 1 locked r2");
                }
            }
        });

        Thread t2 = new Thread(() -> {
            synchronized (r2) {
                try {
                    Thread.sleep(2);
                } catch (InterruptedException e) {
                }
                synchronized (r1) {
                    System.out.println("Thread 2 locked r1");
                }
            }
        });

        t1.start();
        t2.start();
    }
}
