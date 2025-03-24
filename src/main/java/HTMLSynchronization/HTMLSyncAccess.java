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
                        String fileName = path.getFileName().toString();
                        String key = fileName.substring(0, fileName.lastIndexOf('.'));
                        AddValuesToMap(key, new ReentrantLock(true));
                    });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void LockFile(String fileName) {
        ReentrantLock lockFile = SyncLockMap.get(fileName);
        if (lockFile != null) {
            try {
                lockFile.lock();
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            throw new IllegalArgumentException("No lock for: " + fileName);
        }
    }

    public void UnlockFile(String fileName) {
        ReentrantLock lockFile = SyncLockMap.get(fileName);
        if (lockFile != null) {
            try {
                lockFile.unlock();
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            throw new IllegalArgumentException("No lock for: " + fileName);
        }
    }

    public Map<String, ReentrantLock> getSyncLockMap() {
        return SyncLockMap;
    }

    //Jorge Desenvolve um metodo lock que permita dar lock a uma tranca pelo file name
    // exemplo
    // HTMLSyncAccess mapa = new HTMLSyncAccess();
    // mapa.lock(index) para o ficheiro index.html


    // O mesmo para o unlock jorge


    //Jorge Produz todos os testes unitarios desta classe
}
