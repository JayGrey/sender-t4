package ua.oschadbank.sender;


import java.io.File;

import java.util.ArrayList;
import java.util.List;


public class TestSender {
    public static void main(String [] args) {
        
        Sender sender = Sender.getInstance();
        
        
        // try to save clients
        List<Client> clients = new ArrayList<>();
        
        Client cl1 = new Client("Client 1");
        cl1.addDirection(new Direction(new File("test_dir"), ".txt", new String[]{"user1@example.org"}, "subject 1 txt"));
        cl1.addDirection(new Direction(new File("test_dir"), ".bin", new String[]{"user1@example.org"}, "subject 2 bin"));
        clients.add(cl1);
                
        Client cl2 = new Client("Клиент 2");
        cl2.addDirection(new Direction(new File("test_dir2"), ".txt", new String[]{"user2@example.org"}, "subject 1 txt"));
        cl2.addDirection(new Direction(new File("test_dir2"), ".bin", new String[]{"user2@example.org"}, "subject 2 bin"));
        clients.add(cl2);                
        
        sender.saveClients("clients.bin", clients);
        
        // try to load clients
        List<Client> restoredClients = sender.loadClients("clients.bin");
        System.out.println(restoredClients);
        
        System.out.println("Clients are identical? : " + clients.equals(restoredClients));

    }
}
