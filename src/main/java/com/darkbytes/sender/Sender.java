package com.darkbytes.sender;

import java.io.File;
import java.nio.file.FileSystems;
import java.nio.file.PathMatcher;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Sender {

    private final List<Client> clients;

    public Sender(List<Client> clients) {
        if (clients == null) {
            throw new IllegalArgumentException();
        }
        this.clients = clients;
    }

    List<File> getFiles(String directory, String mask) {
        List<File> files = new ArrayList<>();

        if (directory == null || mask == null || !new File(directory)
                .isDirectory()) {
            return Collections.emptyList();
        }

        PathMatcher matcher =
                FileSystems.getDefault().getPathMatcher("glob:" + mask);

        for (File f : new File(directory).listFiles()) {
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

    public void processTasks(SMTPServer server) {

        for (Task task : formTasks()) {
            server.send(task);
        }
    }
}
