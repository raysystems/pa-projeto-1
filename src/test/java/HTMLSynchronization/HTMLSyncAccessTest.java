package HTMLSynchronization;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class HTMLSyncAccessTest {

    @Test
    void testHtmlParsingFunction() {
        HTMLSyncAccess htmlSyncAccess = new HTMLSyncAccess("src/test/java/HTMLSynchronization/testshtml");

        assertNotNull(htmlSyncAccess.getSyncLockMap());

        htmlSyncAccess.getSyncLockMap().forEach((k, v) -> System.out.println("Key: " + k + " Value: " + v));

        assertAll(
                () -> assertEquals(2, htmlSyncAccess.getSyncLockMap().size()),
                () -> assertTrue(htmlSyncAccess.getSyncLockMap().containsKey("src/test/java/HTMLSynchronization/testshtml/test2/index3.html")),
                () -> assertTrue(htmlSyncAccess.getSyncLockMap().containsKey("src/test/java/HTMLSynchronization/testshtml/index.html"))
        );
    }

}