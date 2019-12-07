package ru.bmstu.zookeeper.lab6;

import akka.actor.AbstractActor;
import scala.collection.immutable.List;

import java.util.HashMap;

public class StorageActor extends AbstractActor {
    private List<String> storage ; 


    public StorageActor() {

    }

    @Override
    public Receive createReceive() {
        return null;
    }
}
