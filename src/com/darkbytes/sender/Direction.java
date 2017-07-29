package com.darkbytes.sender;

import java.io.File;
import java.util.Arrays;

public class Direction {
    File path;
    String mask;
    String[] email;
    String subject;

    Direction(File path, String mask, String[] email, String subject) {
        this.path = path;
        this.mask = mask;
        this.email = email;
        this.subject = subject;
    }

    public String getMask() {
        return mask;
    }

    public File getPath() {
        return path;
    }

    @Override
    public boolean equals(Object object) {
        if (object == null) {
            return false;
        } else if (object == this) {
            return true;
        } else if (object instanceof Direction) {
            Direction obj = (Direction) object;

            boolean pathEqual = path.equals(obj.path);
            boolean maskEqual = mask.equals(obj.mask);
            boolean emailsEqual = Arrays.equals(email, obj.email);
            boolean subjectEqual = subject.equals(obj.subject);

            return pathEqual && maskEqual && emailsEqual && subjectEqual;

        } else {
            return false;
        }
    }

    @Override
    public String toString() {
        StringBuilder emailBuilder = new StringBuilder();
        for (int i = 0; i < email.length; i++) {
            emailBuilder.append(email[i]);
            if (i != (email.length - 1)) {
                emailBuilder.append(';');
            }
        }
        return String.format("d (%s | %s | %s | %s)", path, mask, emailBuilder.toString(), subject);
    }
}
