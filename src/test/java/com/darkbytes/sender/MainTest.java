package com.darkbytes.sender;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class MainTest {

    @Rule
    public TemporaryFolder tempFolder = new TemporaryFolder();

    @Test
    public void getFilesTest() throws IOException {
        Sender sender = new Sender(Collections.emptyList());
        tempFolder.newFile("test.txt");
        tempFolder.newFile("test.bin");
        List<File> files = sender.getFiles(tempFolder.getRoot()
                .getCanonicalPath(), "*.txt");
        assertEquals(1, files.size());
    }

    @Test
    public void getFilesFromEmptyDirTest() throws IOException {
        Sender sender = new Sender(Collections.emptyList());
        List<File> files = sender.getFiles(tempFolder.getRoot()
                .getCanonicalPath(), "*.txt");
        assertEquals(0, files.size());
    }

    @Test
    public void getFilesNullMaskTest() throws IOException {
        Sender sender = new Sender(Collections.emptyList());
        List<File> files = sender.getFiles(tempFolder.getRoot()
                .getCanonicalPath(), null);
        assertEquals(0, files.size());
    }

    @Test
    public void loadClientsTest() throws IOException {
        Main main = new Main();
        File clientsFile = tempFolder.newFile();
        writeToFile(clientsFile, "[{\"name\": \"клиент 1\",\"subject\": " +
                "\"test subject\",\"email\": [\"mail@example.org\"],\"mask\":" +
                " \"*.txt\",\"directory\": " +
                "\"D:/projects/sender-t4/target/test\"}]");
        List<Client> clients = main.loadClients(clientsFile.getCanonicalPath());
        assertEquals(1, clients.size());
        assertEquals("клиент 1", clients.get(0).name);

    }

    private void writeToFile(File clientsFile, String s) throws IOException {
        try (BufferedWriter writer =
                     new BufferedWriter(new FileWriter(clientsFile))) {
            writer.write(s);
        }
    }
}
