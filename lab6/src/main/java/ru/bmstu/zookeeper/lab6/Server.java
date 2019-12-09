package ru.bmstu.zookeeper.lab6;

import org.apache.zookeeper.ZooKeeper;

import java.io.IOException;

public class Server {
    private ZooKeeper zoo;

    public Server(String connectString) throws IOException {
        this.zoo = new ZooKeeper(connectString, 3000, this);

    }
}
