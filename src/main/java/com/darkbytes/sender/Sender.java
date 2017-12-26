package com.darkbytes.sender;

import com.darkbytes.sender.exceptions.ThreadInterruptedException;

import java.io.File;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Sender implements Runnable {

    private static Logger logger = Logger.getLogger(Main.class.getName());

    private final BlockingQueue<Task> taskQueue;
    private final Server server;

    public Sender(SMTPServer server, BlockingQueue<Task> taskQueue) {
        if (taskQueue == null || server == null) {
            throw new IllegalArgumentException();
        }
        this.taskQueue = taskQueue;
        this.server = server;
    }

    void deleteFiles(List<File> files) {
        for (File file : files) {
            if (!file.delete()) {
                logger.log(Level.WARNING, "cant't delete file {0}", file);
            }
        }
    }

    @Override
    public void run() {
        try {
            while (!Thread.currentThread().isInterrupted()) {
                Task task = taskQueue.take();
                List<File> processedFiles = server.send(task);
                deleteFiles(processedFiles);
            }
        } catch (InterruptedException e) {
            throw new ThreadInterruptedException(e);
        }
    }
}
