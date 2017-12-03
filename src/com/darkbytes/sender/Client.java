package com.darkbytes.sender;

import java.util.List;

public class Client {
    public final String name;
    public final String subject;
    public final List<String> email;
    public final String mask;
    public final String directory;

    public Client(String name, String subject, List<String> email,
                  String directory, String mask) {
        this.name = name;
        this.subject = subject;
        this.email = email;
        this.directory = directory;
        this.mask = mask;
    }
}
