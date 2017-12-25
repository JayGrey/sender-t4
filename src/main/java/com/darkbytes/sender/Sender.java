package com.darkbytes.sender;

import com.darkbytes.sender.exceptions.SenderException;

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


    public void processTasks(Server server) {
        try {
            while (!Thread.currentThread().isInterrupted()) {
                Task task = taskQueue.take();
                List<File> processedFiles = server.send(task);
                deleteFiles(processedFiles);
            }
        } catch (InterruptedException e) {
            logger.log(Level.WARNING, "Sender interrupted", e);
        }
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
            processTasks(server);
        } catch (SenderException e) {
            logger.log(Level.SEVERE, "Exception", e);
        } catch (IllegalArgumentException e) {
            logger.log(Level.SEVERE, "Illegal arguments", e);
        }
    }
}
