package com.darkbytes.sender;

import java.io.File;


public final class SMTPServer {
    
    private static SMTPServer instance;
    
    private SMTPServer() {
    }
    
    static SMTPServer getInstance() {
        if (instance == null) {
            instance = new SMTPServer();
        }
        
        return instance;
    }
    
    boolean send(String subject, String[] email, File[] files) {
        return false;
    }
    
    void close() {
        // release resources
    }
}
