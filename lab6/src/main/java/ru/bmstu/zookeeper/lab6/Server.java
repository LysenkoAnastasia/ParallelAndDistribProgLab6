package ru.bmstu.zookeeper.lab6;

import akka.actor.ActorRef;
import org.apache.zookeeper.*;

import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public class Server {
    private ZooKeeper zoo;
    private Logger log = Logger.getLogger(Server.class.getName());
    private ActorRef storage;

    public Server(String connectString, ActorRef storage) throws IOException, Exception, InterruptedException {
        this.zoo = new ZooKeeper(connectString, 3000, e -> log.info(e.toString()));
        this.storage = storage;

        this.zoo.create(
                "/servers", "".getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT
        );

    }

    public void createServer(String host, String port) throws Exception {
        zoo.create(
                "/servers", "".getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT
        );
        System.out.println("Ceate server");
    }

    private void watchChildren(WatchedEvent watchedEvent) {

        if (watchedEvent != null){
            System.out.println(watchedEvent.toString());
        }

        try {
            saveServer( zoo.getChildren("/servers", this::watchChildren));
        } catch (Exception e) {
            throw  new RuntimeException(e);
        }
    }

    public void saveServer(List<String> servers) {
        for (s : servers) {

        }
        this.storage.tell(new PutServer(servers), ActorRef.noSender());

    }
}
