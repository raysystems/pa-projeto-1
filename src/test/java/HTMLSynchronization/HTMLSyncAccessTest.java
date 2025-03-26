package HTMLSynchronization;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class HTMLSyncAccessTest {
    private HTMLSyncAccess htmlSyncAccess;
    @BeforeEach
    void setUp() {
        htmlSyncAccess = new HTMLSyncAccess("src/test/java/HTMLSynchronization/testshtml");
    }

    @Test
    void testHtmlParsingFunction() {

        assertNotNull(htmlSyncAccess.getSyncLockMap());

        htmlSyncAccess.getSyncLockMap().forEach((k, v) -> System.out.println("Key: " + k + " Value: " + v));

        assertAll(
                () -> assertEquals(2, htmlSyncAccess.getSyncLockMap().size()),
                () -> assertTrue(htmlSyncAccess.getSyncLockMap().containsKey("src/test/java/HTMLSynchronization/testshtml/test2/index3.html")),
                () -> assertTrue(htmlSyncAccess.getSyncLockMap().containsKey("src/test/java/HTMLSynchronization/testshtml/index.html"))
        );

    }

    @Test
    void testLockUnlockByFile() {
        htmlSyncAccess.LockFile("src/test/java/HTMLSynchronization/testshtml/index.html");
        assertTrue(htmlSyncAccess.getSyncLockMap().get("src/test/java/HTMLSynchronization/testshtml/index.html").isLocked());
        htmlSyncAccess.UnlockFile("src/test/java/HTMLSynchronization/testshtml/index.html");
        assertFalse(htmlSyncAccess.getSyncLockMap().get("src/test/java/HTMLSynchronization/testshtml/test2/index3.html").isLocked());
    }

    @Test
    void testSimultaneousAccessToSameFile() throws InterruptedException {
        // Simulate 2 users
        Thread user1 = new Thread(() -> {
            try {
                htmlSyncAccess.LockFile("src/test/java/HTMLSynchronization/testshtml/index.html");
                assertTrue(htmlSyncAccess.getSyncLockMap().get("src/test/java/HTMLSynchronization/testshtml/index.html").isLocked());
                Thread.sleep(3500);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            } finally {
                htmlSyncAccess.UnlockFile("src/test/java/HTMLSynchronization/testshtml/index.html");
            }
        });

        Thread user2 = new Thread(() -> {
            try {
                Thread.sleep(1000);
                assertTrue(htmlSyncAccess.getSyncLockMap().get("src/test/java/HTMLSynchronization/testshtml/index.html").isLocked());
                Thread.sleep(3600);
                htmlSyncAccess.LockFile("src/test/java/HTMLSynchronization/testshtml/index.html");
                assertTrue(htmlSyncAccess.getSyncLockMap().get("src/test/java/HTMLSynchronization/testshtml/index.html").isLocked());
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            } finally {
                htmlSyncAccess.UnlockFile("src/test/java/HTMLSynchronization/testshtml/index.html");
            }
        });

        user1.start();
        user2.start();

        user1.join();
        user2.join();

        assertFalse(htmlSyncAccess.getSyncLockMap().get("src/test/java/HTMLSynchronization/testshtml/index.html").isLocked());

    }

    @Test
    void testSimultaneousAccessToDifferentFiles() throws InterruptedException {
        Thread user1 = new Thread(() -> {
            htmlSyncAccess.LockFile("src/test/java/HTMLSynchronization/testshtml/index.html");
            assertTrue(htmlSyncAccess.getSyncLockMap().get("src/test/java/HTMLSynchronization/testshtml/index.html").isLocked());
            htmlSyncAccess.UnlockFile("src/test/java/HTMLSynchronization/testshtml/index.html");
        });

        Thread user2 = new Thread(() -> {
            htmlSyncAccess.LockFile("src/test/java/HTMLSynchronization/testshtml/test2/index3.html");
            assertTrue(htmlSyncAccess.getSyncLockMap().get("src/test/java/HTMLSynchronization/testshtml/test2/index3.html").isLocked());
            htmlSyncAccess.UnlockFile("src/test/java/HTMLSynchronization/testshtml/test2/index3.html");
        });

        user1.start();
        user2.start();

        user1.join();
        user2.join();

        assertFalse(htmlSyncAccess.getSyncLockMap().get("src/test/java/HTMLSynchronization/testshtml/index.html").isLocked());
        assertFalse(htmlSyncAccess.getSyncLockMap().get("src/test/java/HTMLSynchronization/testshtml/test2/index3.html").isLocked());
    }


    @Test
    void testLockUnlockNonExistentFile() {
        String fakeFile = "src/test/java/HTMLSynchronization/testshtml/fake.html";
        assertThrows(IllegalArgumentException.class, () -> htmlSyncAccess.LockFile(fakeFile));
        assertThrows(IllegalArgumentException.class, () -> htmlSyncAccess.UnlockFile(fakeFile));
    }

}