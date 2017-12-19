package com.darkbytes.sender;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.rules.TemporaryFolder;

import java.io.*;
import java.util.List;
import java.util.Properties;

import static org.junit.Assert.assertEquals;

public class MainTest {

    @Rule
    public TemporaryFolder tempFolder = new TemporaryFolder();

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Test
    public void initLogTest() {
        new Main().initLog("log.properties");
    }

    @Test
    public void loadDefaultSettings() {
        Properties properties = new Main().loadSettings(null);

        assertEquals("clients.json", properties.get("client_file"));
        assertEquals("127.0.0.1", properties.get("smtp.host"));
        assertEquals("25", properties.get("smtp.port"));
        assertEquals("sender@example.org", properties.get("smtp.from"));
        assertEquals("false", properties.get("email.debug"));
        assertEquals("5", properties.get("sleep_time"));
    }

    @Test
    public void loadSettingsFromFile() throws IOException {
        // prepare file
        File file = tempFolder.newFile();
        PrintWriter writer = new PrintWriter(file);
        writer.println("client_file = cl.json");
        writer.println("log_file = logger.log");
        writer.println("smtp.host = 192.168.0.1");
        writer.println("smtp.port = 6666");
        writer.println("smtp.from = 123@oschadbank.ua");
        writer.println("smtp.user = user");
        writer.println("smtp.password = password");
        writer.println("email.debug = true");
        writer.println("sleep_time = 35");
        writer.flush();
        writer.close();


        Properties properties =
                new Main().loadSettings(file.getCanonicalPath());

        assertEquals("cl.json", properties.get("client_file"));
        assertEquals("logger.log", properties.get("log_file"));
        assertEquals("192.168.0.1", properties.get("smtp.host"));
        assertEquals("6666", properties.get("smtp.port"));
        assertEquals("123@oschadbank.ua", properties.get("smtp.from"));
        assertEquals("true", properties.get("email.debug"));
        assertEquals("35", properties.get("sleep_time"));
    }

    @Test
    public void loadSettingsEmailDebugFlag() throws IOException {
        File file = tempFolder.newFile();
        PrintWriter writer = new PrintWriter(file);
        writer.println("email.debug = true");
        writer.flush();
        writer.close();

        Properties props = new Main().loadSettings(file.getCanonicalPath());
        assertEquals("true", props.get("email.debug"));

        //
        file = tempFolder.newFile();
        writer = new PrintWriter(file);
        writer.println("email.debug = True");
        writer.flush();
        writer.close();

        props = new Main().loadSettings(file.getCanonicalPath());
        assertEquals("true", props.get("email.debug"));

        //
        file = tempFolder.newFile();
        writer = new PrintWriter(file);
        writer.println("email.debug = False");
        writer.flush();
        writer.close();

        props = new Main().loadSettings(file.getCanonicalPath());
        assertEquals("false", props.get("email.debug"));

        //
        file = tempFolder.newFile();
        writer = new PrintWriter(file);
        writer.println("email.debug = no");
        writer.flush();
        writer.close();

        props = new Main().loadSettings(file.getCanonicalPath());
        assertEquals("false", props.get("email.debug"));

    }

    @Test
    public void loadClientsTest() throws IOException {
        Main main = new Main();
        File clientsFile = tempFolder.newFile();
        writelnToFile(clientsFile, "[{\"name\": \"клиент 1\",\"subject\": " +
                "\"test subject\",\"email\": [\"mail@example.org\"],\"mask\":" +
                " \"*.txt\",\"directory\": " +
                "\"D:/projects/sender-t4/target/test\"}]");
        List<Client> clients = main.loadClients(clientsFile.getCanonicalPath());
        assertEquals(1, clients.size());
        assertEquals("клиент 1", clients.get(0).name);

    }

    private void writelnToFile(File file, String s) throws IOException {
        try (BufferedWriter writer =
                     new BufferedWriter(new FileWriter(file))) {
            writer.write(s);
            writer.write(System.lineSeparator());
        }
    }
}
