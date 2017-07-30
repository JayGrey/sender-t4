package com.darkbytes.sender;


import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;


class Filter implements FilenameFilter {
    private String mask;

    Filter(String mask) {
        this.mask = mask;
    }

    public boolean accept(File dir, String name) {
        return name.endsWith(mask);
    }
}

public final class Sender {

    private List<Client> clients;
    private Properties senderSettings;
    private final String SENDER_SETTINGS_FILE = "sender.properties";
    private static Sender instance;
    private static Logger logger = Logger.getLogger(Sender.class.getName());

    private Sender() {
        senderSettings = loadSettings(SENDER_SETTINGS_FILE);

        try {
            clients = importClients(new BufferedReader(new FileReader(senderSettings.getProperty("client_file"))));
        } catch (FileNotFoundException e) {
            logger.log(Level.SEVERE, "Cant find clients file", e);
        } finally {
            if (clients == null) {
                clients = Collections.emptyList();
            }
        }
    }

    static Sender getInstance() {
        if (instance == null) {
            instance = new Sender();
        }

        return instance;
    }

    List<Client> importClients(Reader reader) {
        final String CLIENT_REGEX = "\\s*[c|C]\\s*\\([^\\(\\)]+\\)\\s*";
        final String DIRECTION_REGEX = "\\s*[d|D]\\s*\\([^\\(\\)]+\\)\\s*";

        List<Client> result = new ArrayList<>();
        try (LineNumberReader lineReader = new LineNumberReader(reader)) {

            String line;
            Client currentClient = null;
            while ((line = lineReader.readLine()) != null) {
                line = line.trim();
                if (line.startsWith("#")) {
                    continue;
                }

                if (line.matches(CLIENT_REGEX)) {
                    if (currentClient != null) {
                        result.add(currentClient);
                        currentClient = null;
                    }
                    int openParenthesis = line.indexOf('(');
                    int closeParenthesis = line.indexOf(')');
                    currentClient = new Client(line.substring(openParenthesis + 1, closeParenthesis).trim());

                } else if (line.matches(DIRECTION_REGEX)) {
                    // check direction without client
                    if (currentClient == null) {
                        logger.log(Level.SEVERE, "Found direction w/o client at line {0}, skip it",
                                lineReader.getLineNumber());
                        continue;
                    }

                    int openParenthesis = line.indexOf('(');
                    int closeParenthesis = line.indexOf(')');
                    String[] elements = line.substring(openParenthesis + 1, closeParenthesis).split("\\|");
                    if (elements.length != 4) {
                        logger.log(Level.SEVERE, "Wrong number of fields in direction, at line {0} ", lineReader.getLineNumber());
                        continue;
                    }

                    String path = elements[0].trim();
                    String mask = elements[1].trim();
                    String subject = elements[3].trim();

                    // check path
                    if (path.length() == 0) {
                        logger.log(Level.SEVERE, "path missing at line {0}", lineReader.getLineNumber());
                        continue;
                    }

                    // check email
                    List<String> emailList = new ArrayList<>();
                    for (String email : elements[2].trim().split(";")) {
                        if (email.trim().length() != 0) {
                            emailList.add(email.trim());
                        }
                    }
                    if (emailList.size() == 0) {
                        logger.log(Level.SEVERE, "email address missing at line {0}", lineReader.getLineNumber());
                        continue;
                    }

                    currentClient.addDirection(new Direction(new File(path), mask, emailList.toArray(new String[0]), subject));

                } else {
                    logger.log(Level.SEVERE, "error parsing string [{0}] at line {1}",
                            new Object[]{line, lineReader.getLineNumber()});
                }
            }

            if (currentClient != null) {
                result.add(currentClient);
            }
        } catch (IOException e) {
            logger.log(Level.SEVERE, "error reading clients file", e);
        }
        return result;
    }

    String exportClients() {
        StringBuilder result = new StringBuilder();
        for (Client client : clients) {
            result.append(client);
            result.append(System.lineSeparator());
            for (Direction direction : client.getDirections()) {
                result.append(direction);
                result.append(System.lineSeparator());
            }
        }
        return result.toString();
    }

    private Properties loadSettings(String filename) {
        Properties properties = new Properties();
        try (Reader reader = new BufferedReader(new FileReader(SENDER_SETTINGS_FILE))) {
            properties.load(reader);
        } catch (FileNotFoundException e) {
            logger.log(Level.SEVERE, "Settings file {0} not found%n", filename);
        } catch (IOException e) {
            logger.log(Level.SEVERE, "IO Exception", e);
        }

        if (properties.getProperty("client_file") == null) {
            properties.setProperty("client_file", "clients.bin");
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

    void setSettings(Properties settings) {
        senderSettings = settings;
    }

    void processDirection(Direction direction) {
        if (direction == null) {
            return;
        }

        // check path existance and path is dir
        if (direction.path == null || !direction.path.exists() || direction.path.isFile()) {
            return;
        }

        // get all files by extinsion
        File[] files = direction.path.listFiles(new Filter(direction.mask));
        if (files.length == 0) {
            return;
        }

        // send all files over email
        SMTPServer smtp = SMTPServer.getInstance(senderSettings);
        if (smtp.send(direction.subject, direction.email, files)) {
            // delete sent files
            for (File file : files) {
                logger.log(Level.INFO, "file {0} ==> sent to {1}", new Object[]{file.getName(), direction.email[0]});
                file.delete();
            }
        } else {
            logger.log(Level.SEVERE, "Error sendign file via smtp");
        }
    }

    void start() {
        // all goes here
        logger.info("start processing");
        for (Client client : clients) {
            for (Direction direction : client.getDirections()) {
                processDirection(direction);
            }
        }
        logger.info("stop processing");
    }

    public static void main(String[] args) {
        logger.info("Sender t4");
        getInstance().start();
    }
}
