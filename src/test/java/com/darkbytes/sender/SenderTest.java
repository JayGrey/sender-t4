package com.darkbytes.sender;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.rules.TemporaryFolder;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertEquals;

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
    public void formTasksTest() {

        List<Client> clients = Arrays.asList(
                new Client("", "", Collections.EMPTY_LIST, "", ""));
        Sender sender = new Sender(clients);
        List<Task> tasks = sender.formTasks();

        assertEquals(1, tasks.size());
    }

}