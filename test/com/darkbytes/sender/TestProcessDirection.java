package com.darkbytes.sender;

import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.util.Properties;

import static junit.framework.TestCase.assertEquals;

public class TestProcessDirection {
    private static Sender sender;
    private static Properties senderProperties;

    @Rule
    public TemporaryFolder tempFolder= new TemporaryFolder();

    @BeforeClass
    public static void setUp() {
        sender = Sender.getInstance();
        senderProperties = new Properties();
        senderProperties.put("smtp.host", "127.0.0.1");
        senderProperties.put("smtp.port", 6666);
    }

    @Test
    public void test() throws IOException {
        Direction direction = new Direction(tempFolder.getRoot(), ".txt", new String[]{"user@example.org"},
                "test subject");

        File rootFolder = tempFolder.getRoot();
        File file1 = tempFolder.newFile("file1.txt");
        File file2 = tempFolder.newFile("file2.txt");
        File file3 = tempFolder.newFile("file3.bin");

        assertEquals(3, rootFolder.list().length);

        sender.setSettings(senderProperties);
        sender.processDirection(direction);

        assertEquals(1, rootFolder.list().length);

    }
}
