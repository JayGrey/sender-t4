package com.darkbytes.sender;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.util.Collections;

public class ClientTest {
    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Test
    public void nameIsNullTest() {
        thrown.expect(IllegalArgumentException.class);
        new Client(null, "", Collections.emptyList(), "", "");
    }

    @Test
    public void subjectIsNullTest() {
        thrown.expect(IllegalArgumentException.class);
        new Client("", null, Collections.emptyList(), "", "");
    }

    @Test
    public void emailIsNullTest() {
        thrown.expect(IllegalArgumentException.class);
        new Client("", "", null, "", "");
    }

    @Test
    public void maskIsNullTest() {
        thrown.expect(IllegalArgumentException.class);
        new Client("", "", Collections.emptyList(), null, "");
    }

    @Test
    public void directoryIsNullTest() {
        thrown.expect(IllegalArgumentException.class);
        new Client("", "", Collections.emptyList(), "", null);
    }
}