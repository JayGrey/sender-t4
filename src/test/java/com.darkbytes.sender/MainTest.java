package com.darkbytes.sender;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.File;
import java.io.IOException;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class MainTest {

    @Rule
    public TemporaryFolder tempFolder = new TemporaryFolder();

    @Test
    public void getFilesTest() throws IOException {
        Main main = new Main();
        tempFolder.newFile("test.txt");
        tempFolder.newFile("test.bin");
        List<File> files = main.getFiles(tempFolder.getRoot().getCanonicalPath(), "*.txt");
        assertEquals(1, files.size());
    }
}
