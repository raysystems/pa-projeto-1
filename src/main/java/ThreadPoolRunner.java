import java.util.concurrent.*;

/**
 * This class manages a thread pool to execute asynchronous tasks.
 */
public class ThreadPoolRunner {

    private final ThreadPoolExecutor threadPool;

    /**
     * Initializes the thread pool with a given amount of threads
     *
     * @param poolSize Number of given threads to be in pool.
     */
    public ThreadPoolRunner(int poolSize) {
        this.threadPool = (ThreadPoolExecutor) Executors.newFixedThreadPool(poolSize);
    }

    /**
     * Submits a task for execution by one of the threads available.
     *
     * @param task Task to be executed.
     */
    public void submit(Runnable task) {
        threadPool.submit(task);
    }

    /**
     * Returns the number of available threads in the thread pool.
     *
     * @return Number of available threads.
     */
    public int getFreeWorkers() {
        return threadPool.getMaximumPoolSize() - threadPool.getActiveCount();
    }

    /**
     * Returns the buffer size of pendent tasks to be executed.
     *
     * @return Buffer size of pendent tasks.
     */
    public String getQueueSize() {
        return String.valueOf(threadPool.getQueue().size());
    }

    /**
     * Returns the number of active threads in the thread pool.
     *
     * @return Number of active threads.
     */
    public String getActiveCount() {
        return String.valueOf(threadPool.getActiveCount());
    }
}