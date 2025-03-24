package HTMLSynchronization;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class HTMLSyncAccessTest {

    @Test
    void testHtmlParsingFunction() {
        HTMLSyncAccess htmlSyncAccess = new HTMLSyncAccess("src/test/java/HTMLSynchronization/testshtml");

        assertNotNull(htmlSyncAccess.getSyncLockMap());

        assertAll(
                () -> assertEquals(2, htmlSyncAccess.getSyncLockMap().size()),
                () -> assertTrue(htmlSyncAccess.getSyncLockMap().containsKey("index3")),
                () -> assertTrue(htmlSyncAccess.getSyncLockMap().containsKey("index"))
        );
    }

}