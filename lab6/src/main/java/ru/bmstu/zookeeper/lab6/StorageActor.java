package ru.bmstu.zookeeper.lab6;

import akka.actor.AbstractActor;
import scala.Array;
import scala.collection.immutable.List;

import java.util.ArrayList;
import java.util.HashMap;

public class StorageActor extends AbstractActor {
    private ArrayList<String> storage ;


    public StorageActor() {
        this.storage = new ArrayList<>();

    }

    @Override
    public Receive createReceive() {
        return null;
    }
}
