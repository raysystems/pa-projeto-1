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

    

}