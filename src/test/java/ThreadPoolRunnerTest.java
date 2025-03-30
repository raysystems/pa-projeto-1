import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ThreadPoolRunnerTest {
    private ThreadPoolRunner threadPoolRunner;

    @BeforeEach
    public void setUp() {
        threadPoolRunner = new ThreadPoolRunner(5);
    }

    @AfterEach
    public void tearDown() {
        threadPoolRunner.shutdown();
    }

    @Test
    public void testSubmit() {
        Runnable task = () -> {
            try {
                //Added delay to ensure that the task still executes when the test checks the active count
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        };
        threadPoolRunner.submit(task);
        assertTrue(threadPoolRunner.getActiveCount().equals("1"));

    }

    @Test
    public void testShutdown() {
        threadPoolRunner.shutdown();
        assertTrue(threadPoolRunner.getQueueSize().equals("0"));
    }

    @Test
    public void testGetFreeWorkers() {
        assertEquals(5, threadPoolRunner.getFreeWorkers());
        Runnable task = () -> {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        };
        threadPoolRunner.submit(task);
        assertEquals(4, threadPoolRunner.getFreeWorkers());
    }

    @Test
    public void testGetQueueSize() {
        for (int i = 0; i < 10; i++) {
            Runnable task = () -> {
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            };
            threadPoolRunner.submit(task);
        }



        assertTrue(threadPoolRunner.getQueueSize().equals("5"));
    }

    @Test
    public void testGetActiveCount() {
        assertEquals("0", threadPoolRunner.getActiveCount());
        Runnable task = () -> {
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        };
        threadPoolRunner.submit(task);
        assertTrue(threadPoolRunner.getActiveCount().equals("1"));
    }
}