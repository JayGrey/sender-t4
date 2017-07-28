package com.darkbytes.sender;


import java.io.File;
import java.io.FilenameFilter;
import java.io.Reader;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileOutputStream;
import java.io.FileInputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.FileNotFoundException;

import java.io.IOException;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;


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
    
    private Sender() {
        senderSettings = loadSettings(SENDER_SETTINGS_FILE);
        clients = loadClients(senderSettings.getProperty("client_file", "clients.bin"));
    }
    
    static Sender getInstance() {
        if (instance == null) {
            instance = new Sender();
        }
        
        return instance;
    }
    
    List<Client> loadClients(String filename) {        
        List<Client> clients = null;
                
        try (ObjectInputStream stream = new ObjectInputStream(new FileInputStream(filename))) {
            clients = (ArrayList<Client>)stream.readObject();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return clients;
    }
    
    List<Client> importClients(String filename) {
        return null;
    }
    
    void saveClients(String filename, List<Client> clients) {
        if (clients == null || clients.size() == 0) {
            return;
        }
        
        try (ObjectOutputStream stream = new ObjectOutputStream(new FileOutputStream(filename))) {
            stream.writeObject(clients);
            stream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    private Properties loadSettings(String filename) {     
        Properties properties = new Properties();
        try (Reader reader = new BufferedReader(new FileReader(SENDER_SETTINGS_FILE))) {
            properties.load(reader);            
        } catch (FileNotFoundException e) {
            System.out.format("Settings file %s not found%n", filename);
        } catch (IOException e) {
            e.printStackTrace();
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
        
        if (properties.getProperty("sleep_time") == null) {
            properties.setProperty("sleep_time", "5");
        }
        
        return properties;
    }
    
    private void processDirection(Direction direction) {
        if (direction == null || !direction.isActive()) {
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
        SMTPServer smtp = SMTPServer.getInstance();        
        if ( smtp.send(direction.subject, direction.email, files) ) {            
            // delete sent files
            for (File file : files) {
                file.delete();
            }
        }
    }
    
    void start() {
        // all goes here
        System.out.println("start processing");
        for (Client client : clients) {
            for (Direction direction : client.getDirections()) {
                processDirection(direction);
            }
        }
        System.out.println("stop processing");
    }
    
    public static void main(String[] args) {        
        System.out.println("Sender t4");        
        getInstance().start();
    }
}
