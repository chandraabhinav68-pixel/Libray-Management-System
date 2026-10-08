package com.library.service;

import com.library.model.LibraryItem;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.File;
import java.util.List;
import java.util.concurrent.Callable;

/**
 * Callable task executing catalog export/backup in a background worker thread.
 * Demonstrates Java Concurrency Callable<V> & Future usage.
 */
public class BackupTask implements Callable<Boolean> {
    private final LibraryService service;
    private final String backupFilePath;

    public BackupTask(LibraryService service, String backupFilePath) {
        this.service = service;
        this.backupFilePath = backupFilePath;
    }

    @Override
    public Boolean call() throws Exception {
        // Simulate asynchronous heavy operation
        Thread.sleep(1500);

        List<LibraryItem> items = service.getAllItems();
        File file = new File(backupFilePath);
        
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            writer.write("ITEM_ID,TITLE,AUTHOR,CATEGORY,TOTAL_COPIES,AVAILABLE_COPIES,TYPE\n");
            for (LibraryItem item : items) {
                writer.write(String.format("%s,\"%s\",\"%s\",\"%s\",%d,%d,%s\n",
                        item.getItemId(),
                        item.getTitle(),
                        item.getAuthor(),
                        item.getCategory(),
                        item.getTotalCopies(),
                        item.getAvailableCopies(),
                        item.getItemType()
                ));
            }
        }
        return true;
    }
}
