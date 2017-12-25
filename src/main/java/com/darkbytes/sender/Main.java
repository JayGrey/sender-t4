package com.darkbytes.sender;


import com.darkbytes.sender.exceptions.LoadClientsException;
import com.darkbytes.sender.exceptions.LoadSettingsException;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.*;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;


public final class Main {

    private static Logger logger = Logger.getLogger(Main.class.getName());

    private static final String SENDER_SETTINGS_FILE = "sender.properties";
    private static final String SENDER_LOG_SETTINGS_FILE = "log.properties";
    private final BlockingQueue<Task> taskQueue =
            new ArrayBlockingQueue<>(10);

    private Properties settings;

    private ExecutorService threadPool;

    public static void main(String[] args) {
        new Main().start();
    }

    void initLog(String fileName) {
        try (InputStream stream = new FileInputStream(fileName)) {
            LogManager.getLogManager().readConfiguration(stream);
        } catch (IOException e) {
            System.out.println("Can't read log settings from configuration " +
                    "file");
        }
    }

    Properties loadSettingsFromFile(String filename) {
        Properties props = new Properties();
        try (Reader reader = new BufferedReader(new FileReader(filename))) {
            props.load(reader);
            if (props.containsKey("email.debug")) {
                if (props.getProperty("email.debug").equalsIgnoreCase("true")) {
                    props.setProperty("email.debug", "true");
                } else {
                    props.setProperty("email.debug", "false");
                }
            }
        } catch (Exception e) {
            throw new LoadSettingsException(e);
        }

        return props;
    }

    Properties loadDefaultSettings(Properties props) {
        if (props.getProperty("client_file") == null) {
            props.setProperty("client_file", "clients.json");
        }

        if (props.getProperty("smtp.host") == null) {
            props.setProperty("smtp.host", "127.0.0.1");
        }

        if (props.getProperty("smtp.port") == null) {
            props.setProperty("smtp.port", "25");
        }

        if (props.getProperty("smtp.from") == null) {
            props.setProperty("smtp.from", "sender@example.org");
        }

        if (props.getProperty("email.debug") == null ||
                !props.getProperty("email.debug").equalsIgnoreCase("true")) {
            props.setProperty("email.debug", "false");
        } else {
            props.setProperty("email.debug", "true");
        }

        if (props.getProperty("email.max_attachments") == null) {
            props.setProperty("email.max_attachments", "20");
        }

        if (props.getProperty("sleep_time") == null) {
            props.setProperty("sleep_time", "5");
        }
        return props;
    }

    List<Client> loadClients(String filename) {
        Gson gson = new Gson();
        Type collectionType = new TypeToken<List<Client>>() {
        }.getType();

        try (BufferedReader reader
                     = new BufferedReader(new InputStreamReader(
                new FileInputStream(filename), "utf-8"))) {
            return gson.fromJson(reader, collectionType);
        } catch (Exception e) {
            logger.log(Level.SEVERE, "error reading clients file");
            throw new LoadClientsException();
        }
    }

    private void start() {
        initLog(SENDER_LOG_SETTINGS_FILE);

        threadPool = Executors.newFixedThreadPool(2);

        Runtime.getRuntime().addShutdownHook(new ShutdownHook());

        try {
            settings = loadSettingsFromFile(SENDER_SETTINGS_FILE);
        } catch (LoadSettingsException e) {
            logger.log(Level.WARNING, "Error loading setting, use defaults", e);
            settings = loadDefaultSettings(settings);

        }
        List<Client> clients = loadClients(settings.getProperty("client_file"));
        SMTPServer smtpServer = new SMTPServer(settings);

        Archiver archiver = new Archiver(clients, taskQueue, settings);
        Sender sender = new Sender(smtpServer, taskQueue);

        logger.info("Sender t4");
        logger.info("start processing");

        threadPool.submit(archiver);
        threadPool.submit(sender);
    }

    private class ShutdownHook extends Thread {
        @Override
        public void run() {
            logger.log(Level.INFO, "shutdown in progress");
            System.out.println("shutdown in progress");
            threadPool.shutdown();
            logger.info("stop processing");
            System.out.println("stop processing");
        }
    }
}
