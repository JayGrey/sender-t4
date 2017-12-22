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

import static java.util.Objects.requireNonNull;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

public class SenderTest {

    @Rule
    public TemporaryFolder tempFolder = new TemporaryFolder();

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Test
    public void getFilesTest() throws IOException {
        SMTPServer mockServer = mock(SMTPServer.class);

        Sender sender = new Sender(mockServer, Collections.emptyList());
        tempFolder.newFile("test.txt");
        tempFolder.newFile("test.bin");
        List<File> files = sender.getFiles(tempFolder.getRoot()
                .getCanonicalPath(), "*.txt");
        assertEquals(1, files.size());
    }

    @Test
    public void getFilesFromEmptyDirTest() throws IOException {
        SMTPServer mockServer = mock(SMTPServer.class);
        Sender sender = new Sender(mockServer, Collections.emptyList());
        List<File> files = sender.getFiles(tempFolder.getRoot()
                .getCanonicalPath(), "*.txt");
        assertEquals(0, files.size());
    }

    @Test
    public void getFilesNullMaskTest() throws IOException {
        SMTPServer mockServer = mock(SMTPServer.class);
        Sender sender = new Sender(mockServer, Collections.emptyList());
        List<File> files = sender.getFiles(tempFolder.getRoot()
                .getCanonicalPath(), null);
        assertEquals(0, files.size());
    }

    @Test
    public void formTasksNullTest() {
        thrown.expect(IllegalArgumentException.class);
        SMTPServer mockServer = mock(SMTPServer.class);
        new Sender(mockServer, null);
    }

    @Test
    public void formEmptyTasksTest() {
        List<Client> clients = Collections.singletonList(
                new Client("", "", Collections.emptyList(), "", ""));

        SMTPServer mockServer = mock(SMTPServer.class);
        Sender sender = new Sender(mockServer, clients);
        List<Task> tasks = sender.formTasks();

        assertEquals(0, tasks.size());
    }

    @Test
    public void formNotEmptyTasksTest() throws IOException {
        // prepare files
        tempFolder.newFile("test1.txt");
        tempFolder.newFile("test2.txt");
        tempFolder.newFile("test3.bin");

        List<Client> clients = Collections.singletonList(
                new Client(
                        "test client",
                        "test subject",
                        Collections.singletonList("abc@example.org"),
                        tempFolder.getRoot().getCanonicalPath(),
                        "*.txt")
        );

        SMTPServer mockServer = mock(SMTPServer.class);
        Sender sender = new Sender(mockServer, clients);
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
                        Collections.singletonList("abc@example.org"),
                        tempFolder.getRoot().getCanonicalPath(),
                        "*.txt"),
                new Client(
                        "test client 2",
                        "test subject",
                        Collections.singletonList("abc@example.org"),
                        tempFolder.getRoot().getCanonicalPath(),
                        "*.bin")
        );
        SMTPServer mockServer = mock(SMTPServer.class);
        Sender sender = new Sender(mockServer, clients);

        sender.processTasks(mockServer);
        verify(mockServer, times(2)).send(Mockito.any(Task.class));
    }

    @Test
    public void deleteFilesTest() throws IOException {

        assertEquals(0, requireNonNull(tempFolder.getRoot().listFiles()).length);

        List<File> files = new ArrayList<>();
        files.add(tempFolder.newFile());
        files.add(tempFolder.newFile());
        assertEquals(2, requireNonNull(tempFolder.getRoot().listFiles()).length);

        SMTPServer mockServer = mock(SMTPServer.class);
        Sender sender = new Sender(mockServer, Collections.emptyList());
        sender.deleteFiles(files);
        assertEquals(0, requireNonNull(tempFolder.getRoot().listFiles()).length);
    }

}