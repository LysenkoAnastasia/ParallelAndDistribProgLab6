package ru.bmstu.zookeeper.lab6;

import java.util.ArrayList;

public class PutServer {
    private ArrayList<String> server;

    private PutServer(ArrayList<String> server) {
        this.server = server;
    }

    public ArrayList<String> getServer() {
        return server;
    }
}
