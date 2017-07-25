package ua.oschadbank.sender;

import java.io.File;
import java.io.Serializable;

public class Direction implements Serializable {
    File path;
    String mask;
    String[] email;
    String subject;
    boolean active;
    
    Direction(File path, String mask, String[] email, String subject, boolean active) {
        this.path = path;
        this.mask = mask;
        this.email = email;
        this.subject = subject;
        this.active = active;
    }
    
    public String getMask() {
        return mask;
    }
    
    public File getPath() {
        return path;
    }
    
    public boolean isActive() {
        return active;
    }
}
