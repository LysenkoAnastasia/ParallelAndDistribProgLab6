package ru.bmstu.zookeeper.lab6;

import java.util.ArrayList;
import java.util.List;

public class PutServer {
    private List<String> server;

   public PutServer(List<String> server) {
        this.server = server;
    }

    public List<String> getServer() {
        return server;
    }
}
