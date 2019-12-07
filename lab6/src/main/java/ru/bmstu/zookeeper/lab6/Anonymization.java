package ru.bmstu.zookeeper.lab6;

import akka.NotUsed;
import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.http.javadsl.marshallers.jackson.Jackson;
import akka.http.javadsl.model.HttpRequest;
import akka.http.javadsl.model.HttpResponse;
import akka.pattern.Patterns;
import akka.stream.ActorMaterializer;
import akka.stream.Materializer;
import akka.stream.javadsl.Flow;
import org.apache.zookeeper.ZooKeeper;
import org.asynchttpclient.AsyncHttpClient;
import scala.concurrent.Future;

import static akka.http.javadsl.server.Directives.*;

public class Anonymization {
    AsyncHttpClient asyncHttpClient;
    Materializer materializer;
    ActorRef storage;
    ZooKeeper zoo;

    public Anonymization(AsyncHttpClient asyncHttpClient, ActorSystem system, ActorMaterializer materializer, ZooKeeper zoo) {
        this.materializer = materializer;
        this.asyncHttpClient = asyncHttpClient;
        this.storage = system.actorOf(Props.create(StorageActor.class));
        this.zoo = zoo;
    }

    public Flow<HttpRequest, HttpResponse, NotUsed> createRoute() {
        ActorSystem system = ActorSystem.create("test");
        storage = system.actorOf(Props.create(StorageActor.class));
        return concat(
                get(() ->
                        parameter("url", url -> {
                            Future<Object> result = Patterns.ask(storage,
                                    new TestPackage(Long.parseLong(id)),
                                    5000);
                            return completeOKWithFuture(result, Jackson.marshaller());
                        })),
                post(() ->
                        entity(Jackson.unmarshaller(TestPackageMsg.class), msg -> {
                            storage.tell(msg, ActorRef.noSender());
                            return complete("Test started!\n");
                        })));
    }
    }
}
