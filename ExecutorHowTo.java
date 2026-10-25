import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class ExecutorHowTo {
    // newCachedThreadPool(): unbounded thread creation -> OOM under sustained load
ExecutorService risky = Executors.newCachedThreadPool();

// newFixedThreadPool(): unbounded LinkedBlockingQueue -> OOM from queued tasks,
// even though thread count is capped
ExecutorService alsoRisky = Executors.newFixedThreadPool(10);

// PRODUCTION-GRADE: explicit bounds + rejection policy
ExecutorService prod = new ThreadPoolExecutor(
    10,                                 // core pool size
    20,                                 // max pool size
    60L, TimeUnit.SECONDS,              // idle thread keep-alive
    new ArrayBlockingQueue<>(500),      // BOUNDED queue - caps memory
    new ThreadPoolExecutor.CallerRunsPolicy() // backpressure: caller thread runs task, slows producer
);
}
