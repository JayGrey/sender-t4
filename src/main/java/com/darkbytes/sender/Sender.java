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
            throw new IllegalArgumentException("clients is null");
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

    List<Task> formTasks(List<Client> clients) {
        List<Task> result = new ArrayList<>();

        for (Client client : clients) {
            result.add(new Task(client.subject,
                    getFiles(client.directory, client.mask), client.email));
        }

        return result;
    }

    public void processTasks(SMTPServer server) {

        for (Task task : formTasks(clients)) {
            server.send(task);
        }
    }
}