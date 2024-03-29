package ru.bmstu.zookeeper.lab6;

import akka.actor.ActorRef;
import org.apache.zookeeper.*;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

public class Server {
    private ZooKeeper zoo;
    private ActorRef storage;

    public Server(ZooKeeper zoo, ActorRef storage) throws IOException, Exception, InterruptedException {
        this.zoo = zoo;
        this.storage = storage;
        watchChildren(null);
    }

    public void close() throws KeeperException, InterruptedException {
        zoo.removeAllWatches("/servers", Watcher.WatcherType.Any, true);
    }

    public void createServer(String host, int port, String name) throws Exception {
        zoo.create(
                "/servers/" + name, (host + ":" + port).getBytes(),
                ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL
        );
        System.out.println("Create server");
    }

    private void watchChildren(WatchedEvent watchedEvent) {
        if (watchedEvent != null){
            System.out.println(watchedEvent.toString());
        }
        try {
            saveServer(zoo.getChildren("/servers", this::watchChildren).stream()
            .map(s -> "/servers/" + s)
            .collect(Collectors.toList()));
        } catch (Exception e) {
            throw  new RuntimeException(e);
        }
    }

    public void saveServer(List<String> servers) {
        this.storage.tell(new PutServer(servers), ActorRef.noSender());
    }
}
