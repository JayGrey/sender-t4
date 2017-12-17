package com.darkbytes.sender;

import java.io.File;
import java.util.List;

public class Task {
    public final String subject;
    public final List<File> files;
    public final List<String> emails;

    public Task(String subject, List<File> files, List<String> emails) {
        this.subject = subject;
        this.files = files;
        this.emails = emails;
    }
}
