package ua.oschadbank.sender;

import java.io.File;

public class Direction {        
    private File path;
    private String mask;
    private String[] email;
    private String subject;
    private boolean active;
    
    Direction(File path, String[] email, String subject, boolean active) {
        this.path = path;
        this.email = email;
        this.subject = subject;
        this.active = active;
    }
    
    public String getMask() {
        return mask;
    }
}
