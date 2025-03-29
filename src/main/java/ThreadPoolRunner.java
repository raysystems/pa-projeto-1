import java.util.concurrent.*;

/**
 * ThreadPoolRunner is a utility class that manages a thread pool for executing tasks concurrently.
 * It provides methods to submit tasks, shutdown the pool, and retrieve information about the pool's state.
 */
public class ThreadPoolRunner {

    private final ThreadPoolExecutor threadPool;

    public ThreadPoolRunner(int poolSize) { this.threadPool = (ThreadPoolExecutor) Executors.newFixedThreadPool(poolSize); }

    public void submit(Runnable task) { threadPool.submit(task); }

    public void shutdown() { threadPool.shutdown(); }

    public int getFreeWorkers() { return threadPool.getMaximumPoolSize() - threadPool.getActiveCount(); }

    public String getQueueSize() { return String.valueOf(threadPool.getQueue().size()); }

    public String getActiveCount() {
        return String.valueOf(threadPool.getActiveCount());
    }
}