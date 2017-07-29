package com.darkbytes.sender;


import org.junit.BeforeClass;
import org.junit.Test;

import java.io.File;
import java.io.StringReader;
import java.util.List;

import static junit.framework.TestCase.assertEquals;
import static org.junit.Assert.assertTrue;


public class TestImport {

    private static Sender sender;

    @BeforeClass
    public static void setUp() {
        sender = Sender.getInstance();
    }

    @Test
    public void testLoadOneClient() {
        List<Client> clients = sender.importClients(new StringReader("c (client 1)"));

        assertEquals(1, clients.size());
        assertEquals("client 1", clients.get(0).getName());
    }

    @Test
    public void testLoadClientWithSpaces() {
        List<Client> clients = sender.importClients(new StringReader(" C (   client 1  )  "));

        assertEquals(1, clients.size());
        assertEquals("client 1", clients.get(0).getName());
    }

    @Test
    public void testLoadClientWithNationalCharacters() {
        String testString = " C (   Какой-то клиент  )  ";

        List<Client> clients = sender.importClients(new StringReader(testString));

        assertEquals(1, clients.size());
        assertEquals("Какой-то клиент", clients.get(0).getName());
    }

    @Test
    public void testLoadFewClients() {
        String testString = "c (client 1)\n C (client 2) ";
        List<Client> clients = sender.importClients(new StringReader(testString));

        assertEquals(2, clients.size());
        assertEquals("client 1", clients.get(0).getName());
        assertEquals("client 2", clients.get(1).getName());
    }

    @Test
    public void testClientWithDirection() {
        String testString = "c (client 1)\n d (d:\\test1_1 | .txt | cl1@example.org | subject for client 1) ";
        List<Client> clients = sender.importClients(new StringReader(testString));

        assertEquals(1, clients.size());
        assertEquals("client 1", clients.get(0).getName());
        assertEquals(1, clients.get(0).getDirections().size());

        Direction direction = clients.get(0).getDirections().get(0);
        assertTrue(clients.get(0).getDirections().get(0).equals(new Direction(new File("d:\\test1_1"), ".txt",
                new String[]{"cl1@example.org"}, "subject for client 1")));


    }

    @Test
    public void testClientWithMultiplyDirections() {
        String testString =
                "c (client 1)\n " +
                        "d (d:\\test1_1 | .txt | cl1@example.org | subject for client 1)\n" +
                        "D (c:\\test1_2 | .dbf | cl2@example.org | subject for client 2) ";

        List<Client> clients = sender.importClients(new StringReader(testString));

        assertEquals(1, clients.size());
        assertEquals("client 1", clients.get(0).getName());
        assertEquals(2, clients.get(0).getDirections().size());

        assertTrue(clients.get(0).getDirections().get(0).equals(new Direction(new File("d:\\test1_1"), ".txt",
                new String[]{"cl1@example.org"}, "subject for client 1")));
        assertTrue(clients.get(0).getDirections().get(1).equals(new Direction(new File("c:\\test1_2"), ".dbf",
                new String[]{"cl2@example.org"}, "subject for client 2")));
    }

    @Test
    public void testClientWithMultiplyEmails() {
        String testString =
                "c (client 1)\n " +
                        "d (d:\\test1_1 | .txt | cl1@example.org ; cl1@example.com | subject for client 1)\n";

        List<Client> clients = sender.importClients(new StringReader(testString));

        assertEquals(1, clients.size());
        assertEquals("client 1", clients.get(0).getName());

        assertTrue(clients.get(0).getDirections().get(0).equals(new Direction(new File("d:\\test1_1"), ".txt",
                new String[]{"cl1@example.org", "cl1@example.com"}, "subject for client 1")));
    }

    @Test
    public void testClientWithEmptyEmail() {
        String testString =
                "c (client 1)\n " +
                        "d (d:\\test1_1 | .txt |  | subject for client 1)\n";

        List<Client> clients = sender.importClients(new StringReader(testString));

        assertEquals(0, clients.get(0).getDirections().size());
    }

    @Test
    public void testClientWithEmptyPath() {
        String testString =
                "c (client 1)\n " +
                        "d (  | .txt | user@example.org | subject for client 1)\n";

        List<Client> clients = sender.importClients(new StringReader(testString));

        assertEquals(0, clients.get(0).getDirections().size());
    }


    @Test
    public void testDirectionWithoutClient() {
        String testString =
                "d ( d:\\path | .txt | user@example.org | subject for client 1)\n";

        List<Client> clients = sender.importClients(new StringReader(testString));

        assertEquals(0, clients.size());
    }

    @Test
    public void testMixedDirectionWithoutClient() {
        String testString =
                "d (  | .txt | user@example.org | subject for client 1)\n" +
                        "c (client 1)\n " +
                        "d ( d:\\path1 | .txt | user@example.org | subject for client 1)\n" +
                        "d ( d:\\path2 | .bin | user@example.com | subject for client 1)\n";

        List<Client> clients = sender.importClients(new StringReader(testString));

        assertEquals(1, clients.size());
        assertEquals(2, clients.get(0).getDirections().size());
    }

    @Test
    public void ImportFileWithComments() {
        String testString =
                "# d (  | .txt | user@example.org | subject for client 1)\n" +
                        "c (client 1)\n " +
                        "d ( d:\\path1 | .txt | user@example.org | subject for client 1)\n" +
                        "  #  d ( d:\\path2 | .bin | user@example.com | subject for client 1)\n";

        List<Client> clients = sender.importClients(new StringReader(testString));

        assertEquals(1, clients.size());
        assertEquals(1, clients.get(0).getDirections().size());
    }

}
