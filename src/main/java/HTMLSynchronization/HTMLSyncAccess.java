package HTMLSynchronization;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.ReentrantLock;

public class HTMLSyncAccess {

    private Map<String, ReentrantLock> SyncLockMap;
    private String DocumentRootpath;

    public HTMLSyncAccess(String DocumentRootpath) {
        this.SyncLockMap = new HashMap<>();
        this.DocumentRootpath = DocumentRootpath;
        ParseAllHtmlFiles();
    }

    private void AddValuesToMap(String key, ReentrantLock lock) {
        this.SyncLockMap.put(key, lock);
    }

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

    public Map<String, ReentrantLock> getSyncLockMap() {
        return SyncLockMap;
    }

}
