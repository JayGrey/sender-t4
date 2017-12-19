package com.darkbytes.sender;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.File;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.Properties;

public class SMTPServerTest {

    @Rule
    public TemporaryFolder tempDir = new TemporaryFolder();

    @Test
    public void sendTest() throws Exception {
        Properties props = new Properties();
        props.setProperty("smtp.host", "localhost");
        props.setProperty("smtp.port", "6666");
        props.setProperty("email.debug", "true");

        // prepare files
        File file1 = tempDir.newFile("file1.txt");
        PrintWriter writer = new PrintWriter(file1);
        writer.write("some text in file1");
        writer.flush();
        writer.close();

        File file2 = tempDir.newFile("file2.txt");
        writer = new PrintWriter(file2);
        writer.write("some text in file2");
        writer.flush();
        writer.close();

        SMTPServer server = new SMTPServer(props);

        Task task = new Task(
                "test subject",
                Arrays.asList(file1, file2),
                Arrays.asList("abc@example.org")
        );

        server.send(task);
    }

}