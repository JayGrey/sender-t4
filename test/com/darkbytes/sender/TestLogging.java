package com.darkbytes.sender;

import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintStream;
import java.io.StringWriter;
import java.util.logging.Logger;

import static junit.framework.TestCase.assertEquals;

public class TestLogging {

    private static Logger logger = Logger.getLogger(Sender.class.getName());

    @Test
    public void test() {
        Sender instance = Sender.getInstance();

        ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
        System.setErr(new PrintStream(byteStream));
        logger.info("test message");
        System.setErr(System.err);

        String result = new String(byteStream.toByteArray());
        System.out.println(result);
        assertEquals(1, result.split("\n").length);


    }
}
