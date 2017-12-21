package com.darkbytes.sender;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.rules.TemporaryFolder;
import org.mockito.Mockito;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

public class SenderTest {

    @Rule
    public TemporaryFolder tempFolder = new TemporaryFolder();

    @Rule
    public ExpectedException thrown = ExpectedException.none();

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
    public void formTasksNullTest() {
        thrown.expect(IllegalArgumentException.class);

        new Sender(null);
    }

    @Test
    public void formEmptyTasksTest() {

        List<Client> clients = Arrays.asList(
                new Client("", "", Collections.EMPTY_LIST, "", ""));
        Sender sender = new Sender(clients);
        List<Task> tasks = sender.formTasks();

        assertEquals(0, tasks.size());
    }

    @Test
    public void formNotEmptyTasksTest() throws IOException {
        // prepare files
        tempFolder.newFile("test1.txt");
        tempFolder.newFile("test2.txt");
        tempFolder.newFile("test3.bin");

        List<Client> clients = Arrays.asList(
                new Client(
                        "test client",
                        "test subject",
                        Arrays.asList("abc@example.org"),
                        tempFolder.getRoot().getCanonicalPath(),
                        "*.txt")
        );

        Sender sender = new Sender(clients);
        List<Task> tasks = sender.formTasks();

        assertEquals(1, tasks.size());
        assertEquals(2, tasks.get(0).files.size());
    }

    @Test
    public void processTaskTest() throws IOException {
        // prepare files
        tempFolder.newFile("test1.txt");
        tempFolder.newFile("test2.txt");
        tempFolder.newFile("test3.bin");

        List<Client> clients = Arrays.asList(
                new Client(
                        "test client 1",
                        "test subject",
                        Arrays.asList("abc@example.org"),
                        tempFolder.getRoot().getCanonicalPath(),
                        "*.txt"),
                new Client(
                        "test client 2",
                        "test subject",
                        Arrays.asList("abc@example.org"),
                        tempFolder.getRoot().getCanonicalPath(),
                        "*.bin")
        );

        Sender sender = new Sender(clients);
        List<Task> tasks = sender.formTasks();


        SMTPServer mockServer = mock(SMTPServer.class);

        sender.processTasks(mockServer);
        verify(mockServer, times(2)).send(Mockito.any(Task.class));
    }

    @Test
    public void deleteFilesTest() throws IOException {
        assertEquals(0, tempFolder.getRoot().listFiles().length);

        List<File> files = new ArrayList<>();
        files.add(tempFolder.newFile());
        files.add(tempFolder.newFile());
        assertEquals(2, tempFolder.getRoot().listFiles().length);

        Sender sender = new Sender(Collections.emptyList());
        sender.deleteFiles(files);
        assertEquals(0, tempFolder.getRoot().listFiles().length);
    }

}