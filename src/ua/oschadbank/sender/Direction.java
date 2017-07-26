package ua.oschadbank.sender;

import java.io.File;
import java.io.Serializable;

public class Direction implements Serializable {
    File path;
    String mask;
    String[] email;
    String subject;
    boolean active;
    
    Direction(File path, String mask, String[] email, String subject) {
        this(path, mask, email, subject, true);
    }
    
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
    
    @Override
    public boolean equals(Object object) {
        if (object == null) {
            return false;
        } else if (object == this) {
            return true;
        } else if (object instanceof Direction) {
            Direction obj = (Direction) object;
            return path.equals(obj.path) && mask.equals(obj.mask) && email.equals(obj.email) && subject.equals(obj.subject) && active == obj.active;
        } else {
            return false;
        }
    }
}
