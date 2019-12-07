package ru.bmstu.zookeeper.lab6;

import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import scala.Array;
import scala.collection.immutable.List;

import java.util.ArrayList;
import java.util.HashMap;
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
        return null;
    }

    private void getRandomServer() {
        getSender().tell(
                new ReturnServerMsg(storage.get(randomServer.nextInt(storage.size()))),
                ActorRef.noSender()
        );
    }
}
