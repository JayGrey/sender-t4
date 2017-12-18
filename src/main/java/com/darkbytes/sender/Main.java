package com.darkbytes.sender;


import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.*;
import java.lang.reflect.Type;
import java.nio.file.*;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;
import java.util.stream.Collectors;


public final class Main {

    private static Logger logger = Logger.getLogger(Main.class.getName());

    private final String SENDER_SETTINGS_FILE = "sender.properties";
    private final String SENDER_LOG_SETTINGS_FILE = "log.properties";

    private Properties settings;
    private MailSender sender;
    private List<Client> clients;


    public static void main(String[] args) {
        logger.info("Sender t4");
        new Main().start();
    }

    private void initLog(String fileName) {
        try (InputStream stream = new FileInputStream(fileName)) {
            LogManager.getLogManager().readConfiguration(stream);
        } catch (IOException e) {
            System.out.println("Can't read log settings from configuration " +
                    "file");
        }
    }

    private Properties loadSettings(String filename) {
        //todo: add debug mail flag
        //todo: add max file size flag
        //todo: add max amount of files flag
        Properties properties = new Properties();
        try (Reader reader = new BufferedReader(
                new FileReader(SENDER_SETTINGS_FILE))) {
            properties.load(reader);
        } catch (FileNotFoundException e) {
            logger.log(Level.SEVERE, "Settings file {0} not found%n", filename);
        } catch (IOException e) {
            logger.log(Level.SEVERE, "IO Exception", e);
        }

        if (properties.getProperty("client_file") == null) {
            properties.setProperty("client_file", "clients.json");
        }

        if (properties.getProperty("smtp.host") == null) {
            properties.setProperty("smtp.host", "127.0.0.1");
        }

        if (properties.getProperty("smtp.port") == null) {
            properties.setProperty("smtp.port", "21");
        }

        if (properties.getProperty("smtp.from") == null) {
            properties.setProperty("smtp.from", "sender@example.org");
        }

        if (properties.getProperty("sleep_time") == null) {
            properties.setProperty("sleep_time", "5");
        }

        return properties;
    }

    private List<Client> loadClients() {
        Gson gson = new Gson();
        Type collectionType = new TypeToken<List<Client>>() {
        }.getType();

        List<Client> result = Collections.emptyList();

        try (BufferedReader reader
                     = new BufferedReader(new FileReader(
                settings.getProperty("client_file")))) {
            result = gson.fromJson(reader, collectionType);
        } catch (IOException e) {
            logger.log(Level.SEVERE, "error reading clients file");
        }

        return result;
    }

    private void start() {
        initLog(SENDER_LOG_SETTINGS_FILE);
        settings = loadSettings(SENDER_SETTINGS_FILE);
        clients = loadClients();
        sender = new MailSender(settings);

        logger.info("start processing");
        sender.processTasks(formTasks());
        logger.info("stop processing");
    }

    private List<Task> formTasks() {
        List<Task> result = new ArrayList<>();

        for (Client client : clients) {
            result.add(new Task(client.subject,
                    getFiles(client.directory, client.mask), client.email));
        }

        return result;
    }

    List<File> getFiles(String directory, String mask) {
        List<File> files = new ArrayList<>();

        if (directory == null || mask == null || !new File(directory)
                .isDirectory()) {
            return Collections.emptyList();
        }

        PathMatcher matcher =
                FileSystems.getDefault().getPathMatcher("glob:" + mask);

/*        try {
            files = Files.find(Paths.get(directory), 0,
                    (p, a) -> matcher.matches(p.getFileName()))
                    .map(Path::toFile)
                    .collect(Collectors.toList());
        } catch (IOException e) {
            logger.log(Level.SEVERE, "error finding files", e);
        }*/

        for (File f : new File(directory).listFiles()) {
            if (matcher.matches(Paths.get(f.getName()))) {
                files.add(f);
            }
        }

        return files;
    }
}
