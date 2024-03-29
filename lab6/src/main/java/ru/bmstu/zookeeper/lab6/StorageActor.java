package ru.bmstu.zookeeper.lab6;

import akka.actor.AbstractActor;
import akka.actor.ActorRef;

import java.util.ArrayList;
import java.util.Random;

public class StorageActor extends AbstractActor {
    private ArrayList<String> storage ;
    private Random randomServer;


    public StorageActor() {
        this.storage = new ArrayList<>();
        this.randomServer = new Random();

    }

    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(PutServer.class, this::putServer)
                .match(GetRandom.class, this::getRandomServer)
                .match(DeleteServer.class, this::deleteServer)
                .build();
    }

    private void getRandomServer(GetRandom getRandom) {
        getSender().tell(
                new ReturnServerMsg(storage.get(randomServer.nextInt(storage.size()))),
                ActorRef.noSender()
        );
    }

    private void putServer(PutServer putServer) {
        this.storage.clear();
        this.storage.addAll(putServer.getServer());
    }


    private void deleteServer(DeleteServer deleteServer) {
        this.storage.remove(deleteServer.getServer());
    }
}
