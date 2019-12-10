package ru.bmstu.zookeeper.lab6;

import org.apache.zookeeper.ZooKeeper;

import java.io.IOException;
import java.util.logging.Logger;

public class Server {
    private ZooKeeper zoo;
    private Logger log = Logger.getLogger(Server.class.getCanonicalName());

    public Server(String connectString) throws IOException {
        this.zoo = new ZooKeeper(connectString, 3000, e -> log.info(e.toString()));

        zoo.create(
                
        )

    }
}
