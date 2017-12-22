package com.darkbytes.sender;

import com.darkbytes.sender.exceptions.SenderException;

import java.io.File;
import java.nio.file.FileSystems;
import java.nio.file.PathMatcher;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Sender implements Runnable {

    private static Logger logger = Logger.getLogger(Main.class.getName());

    private final List<Client> clients;
    private final Server server;

    public Sender(SMTPServer server, List<Client> clients) {
        if (clients == null || server == null) {
            throw new IllegalArgumentException();
        }
        this.clients = clients;
        this.server = server;
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

    public void processTasks(Server server) {
        if (Thread.currentThread().isInterrupted()) {
            return;
        }
        for (Task task : formTasks()) {
            List<File> processedFiles = server.send(task);
            deleteFiles(processedFiles);
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
