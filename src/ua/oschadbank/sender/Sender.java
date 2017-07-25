package ua.oschadbank.sender;


import java.io.File;
import java.io.FilenameFilter;
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
