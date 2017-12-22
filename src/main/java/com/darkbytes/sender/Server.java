package com.darkbytes.sender;

import java.io.File;
import java.util.List;

public abstract class Server {
    public abstract List<File> send(Task task);
}
