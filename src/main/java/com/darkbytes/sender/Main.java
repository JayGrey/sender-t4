package com.darkbytes.sender;


import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.*;
import java.lang.reflect.Type;
import java.nio.file.FileSystems;
import java.nio.file.PathMatcher;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;


public final class Main {

    private static Logger logger = Logger.getLogger(Main.class.getName());

    private static final String SENDER_SETTINGS_FILE = "sender.properties";
    private static final String SENDER_LOG_SETTINGS_FILE = "log.properties";

    private Properties settings;

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

    Properties loadSettings(String filename) {
        //todo: add max file size flag
        //todo: add max amount of files flag
        //todo: add base dir settings
        Properties props = new Properties();
        try (Reader reader = new BufferedReader(
                new FileReader(filename))) {
            props.load(reader);
        } catch (FileNotFoundException e) {
            logger.log(Level.SEVERE, "Settings file {0} not found%n", filename);
        } catch (IOException e) {
            logger.log(Level.SEVERE, "IO Exception", e);
        } catch (NullPointerException e) {
            logger.log(Level.SEVERE, "Filename is undefined", e);
        }

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

        if (props.getProperty("sleep_time") == null) {
            props.setProperty("sleep_time", "5");
        }

        return props;
    }

    List<Client> loadClients(String filename) {
        Gson gson = new Gson();
        Type collectionType = new TypeToken<List<Client>>() {
        }.getType();

        List<Client> result = Collections.emptyList();

        try (BufferedReader reader
                     = new BufferedReader(new InputStreamReader(new FileInputStream(filename), "utf-8"))) {
            result = gson.fromJson(reader, collectionType);
        } catch (IOException e) {
            logger.log(Level.SEVERE, "error reading clients file");
        }

        return result;
    }

    private void start() {
        initLog(SENDER_LOG_SETTINGS_FILE);
        settings = loadSettings(SENDER_SETTINGS_FILE);
        List<Client> clients = loadClients(settings.getProperty("client_file"));
        Sender sender = new Sender(clients);
        SMTPServer smtpServer = new SMTPServer(settings);

        logger.info("Sender t4");
        logger.info("start processing");
        sender.processTasks(smtpServer);
        logger.info("stop processing");
    }

}
