package ua.oschadbank.sender;


import java.util.List;
import java.util.ArrayList;
import java.util.Properties;
import java.io.File;

public final class Sender {
    
    private List<Client> clients;
    private Properties senderSettings;
    private final String SENDER_SETTINGS_FILE = "sender.properties";
    
    private Sender() {
        senderSettings = loadSettings(SENDER_SETTINGS_FILE);
        clients = loadClients(senderSettings.getProperty("client_file", "clients.sqlite"));
    }
    
    private List<Client> loadClients(String filename) {
        // load clients settings from clients.sqlite
        return new ArrayList<Client>();
    }
    
    private Properties loadSettings(String filename) {
        // load main settings from sender.properties
        return new Properties();
    }
    
    private void processDirection(Direction direction) {
        if (direction == null) {
            return;
        }
        // check path existance
        // get all files by extinsion
        // send all files over email
        // delete sent files
    }
    
    private void start() {
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
        new Sender().start();
    }
}
