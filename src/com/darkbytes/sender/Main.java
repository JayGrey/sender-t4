package com.darkbytes.sender;


import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.*;
import java.lang.reflect.Type;
import java.util.Collections;
import java.util.List;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;


public final class Main {

    private static Main instance;
    private static Logger logger = Logger.getLogger(Main.class.getName());

    private final String SENDER_SETTINGS_FILE = "sender.properties";
    private final String SENDER_LOG_SETTINGS_FILE = "log.properties";

    private Properties settings;
    private MailSender sender;


    public static void main(String[] args) {
        logger.info("Sender t4");
        new Main().start();
    }

    private Main() {
        try (InputStream stream =
                     new FileInputStream(SENDER_LOG_SETTINGS_FILE)) {
            LogManager.getLogManager().readConfiguration(stream);
        } catch (IOException e) {
            System.out.println("Can't read log settings from configuration " +
                    "file");
        }

        settings = loadSettings(SENDER_SETTINGS_FILE);

        sender = new MailSender(settings);

    }

    private Properties loadSettings(String filename) {
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

    List<Client> loadClients() {
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

    void start() {
        logger.info("start processing");
        sender.processTasks(formTasks());
        logger.info("stop processing");
    }

    private List<Task> formTasks() {
        return Collections.emptyList();
    }

}
