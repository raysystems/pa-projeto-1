package HTMLSynchronization;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.ReentrantLock;

/**
 * This class provides synchronized access to HTML files using ReentrantLocks.
 * It initializes locks for all HTML files in the specified document root path.
 */
public class HTMLSyncAccess {

    private Map<String, ReentrantLock> SyncLockMap;
    private String DocumentRootpath;

    /**
     * Constructs an HTMLSyncAccess object and initializes locks for all HTML files
     * in the specified document root path.
     *
     * @param DocumentRootpath The root path where HTML files are located.
     */
    public HTMLSyncAccess(String DocumentRootpath) {
        this.SyncLockMap = new HashMap<>();
        this.DocumentRootpath = DocumentRootpath;
        ParseAllHtmlFiles();
    }

    /**
     * Adds a lock to the SyncLockMap for the specified key.
     *
     * @param key  The key representing the file path.
     * @param lock The ReentrantLock to be associated with the key.
     */
    private void AddValuesToMap(String key, ReentrantLock lock) {
        this.SyncLockMap.put(key, lock);
    }

    /**
     * Parses all HTML files in the document root path and initializes locks for them.
     */
    private void ParseAllHtmlFiles() {
        try {
            Files.walk(Paths.get(DocumentRootpath))
                    .filter(Files::isRegularFile)
                    .filter(path -> path.toString().endsWith(".html"))
                    .forEach(path -> {
                        String filePath = path.toString();
                        filePath = filePath.replace("\\", "/");
                        AddValuesToMap(filePath, new ReentrantLock(true));
                    });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Locks the specified file path.
     *
     * @param filePath The file path to be locked.
     * @throws IllegalArgumentException If no lock is found for the specified file path.
     */
    public void LockFile(String filePath) {
        ReentrantLock lockFile = SyncLockMap.get(filePath);
        if (lockFile != null) {
            try {
                lockFile.lock();
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            throw new IllegalArgumentException("No lock for this path: " + filePath);
        }
    }

    /**
     * Unlocks the specified file path.
     *
     * @param filePath The file path to be unlocked.
     * @throws IllegalArgumentException If no lock is found for the specified file path.
     */
    public void UnlockFile(String filePath) {
        ReentrantLock lockFile = SyncLockMap.get(filePath);
        if (lockFile != null) {
            try {
                lockFile.unlock();
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            throw new IllegalArgumentException("No lock for this path: " + filePath);
        }
    }

    /**
     * Returns the map of file paths and their associated locks.
     *
     * @return A map containing file paths and their associated ReentrantLocks.
     */
    public Map<String, ReentrantLock> getSyncLockMap() {
        return SyncLockMap;
    }

}