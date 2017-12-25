package com.darkbytes.sender;

import java.io.File;
import java.nio.file.FileSystems;
import java.nio.file.PathMatcher;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Archiver implements Runnable {

    private static Logger logger = Logger.getLogger(Main.class.getName());

    private final List<Client> clients;
    private final BlockingQueue<Task> taskQueue;
    private final Properties props;

    public Archiver(List<Client> clients, BlockingQueue<Task> taskQueue,
                    Properties props) {
        this.clients = clients;
        this.taskQueue = taskQueue;
        this.props = props;
    }

    List<File> getFiles(String directory, String mask) {
        List<File> files = new ArrayList<>();

        if (directory == null || !new File(directory).isDirectory()) {
            logger.log(Level.WARNING, "directory {0} is invalid", directory);
            return Collections.emptyList();
        }

        if (mask == null) {
            logger.log(Level.WARNING, "mask is null");
            return Collections.emptyList();
        }

        PathMatcher matcher =
                FileSystems.getDefault().getPathMatcher("glob:" + mask);

        File[] listFiles = new File(directory).listFiles();
        if (listFiles == null) {
            logger.log(Level.SEVERE, "error getting file list");
            return Collections.emptyList();
        }

        for (File f : listFiles) {
            if (matcher.matches(Paths.get(f.getName()))) {
                files.add(f);
            }
        }
        return files;
    }

    List<Task> formTasks() {
        List<Task> result = new ArrayList<>();

        for (Client client : clients) {
            List<File> files = getFiles(client.directory, client.mask);
            if (files.size() > 0 && client.email.size() > 0) {
                result.add(new Task(client.subject, files, client.email));
            }
        }

        return result;
    }

    @Override
    public void run() {
        try {
            while (!Thread.currentThread().isInterrupted()) {
                for (Task task : formTasks()) {
                    taskQueue.put(task);
                }
                TimeUnit.SECONDS.sleep(
                        Long.valueOf(props.getProperty("sleep_time")));
            }
        } catch (InterruptedException e) {
            logger.log(Level.WARNING, "Archiver interrupted", e);
        }

    }
}
