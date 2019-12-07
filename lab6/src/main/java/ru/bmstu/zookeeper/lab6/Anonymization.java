package ru.bmstu.zookeeper.lab6;

import akka.NotUsed;
import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.http.javadsl.Http;
import akka.http.javadsl.marshallers.jackson.Jackson;
import akka.http.javadsl.model.HttpRequest;
import akka.http.javadsl.model.HttpResponse;
import akka.http.javadsl.server.Route;
import akka.pattern.Patterns;
import akka.stream.ActorMaterializer;
import akka.stream.Materializer;
import akka.stream.javadsl.Flow;
import org.apache.zookeeper.ZooKeeper;
import org.asynchttpclient.AsyncHttpClient;
import org.asynchttpclient.Request;
import org.asynchttpclient.Response;
import scala.compat.java8.FutureConverters;
import scala.concurrent.Future;

import java.util.concurrent.CompletionStage;

import static akka.actor.TypedActor.context;
import static akka.http.javadsl.server.Directives.*;

public class Anonymization {
    AsyncHttpClient asyncHttpClient;
    Materializer materializer;
    ActorRef storage;
    ZooKeeper zoo;
    Http http;

    public Anonymization(AsyncHttpClient asyncHttpClient, ActorSystem system, ActorMaterializer materializer, ZooKeeper zoo, Http http) {
        this.materializer = materializer;
        this.asyncHttpClient = asyncHttpClient;
        this.storage = system.actorOf(Props.create(StorageActor.class));
        this.zoo = zoo;
        this.http = http;
    }

    public Route createRoute(ActorSystem system) {
        ActorRef actorRef = system.actorOf(Props.create(StorageActor.class));
        return concat(
                get(() ->
                        parameter("url", url ->
                                parameter("count", count -> {
                                            int c = Integer.parseInt(count);
                                        CompletionStage<String> responseCompletionStage;
                                            if (c == 0) {
                                               responseCompletionStage = fetch(url);
                                              return FutureConverters.toJava(Patterns.ask(storage, new GetRandom(), 5000))
                                                       .thenApply(s -> (String)s)
                                                       .thenCompose(server -> {

                                                       });
                                            }
                                            else {
                                                responseCompletionStage = null;
                                            }
                                            return completeOKWithFutureString(
                                                    http.singleRequest(HttpRequest.create(url))
                                                            .thenApply(r -> r.entity().toString())
                                            );
                                        }
                                ))));
    }

    private Route getUrlCount(String url, int count) {
        CompletionStage<String> responseCompletionStage;
        if (count == 0) {
            responseCompletionStage = fetch(url);
        }
        return completeOKWithFutureString(fetch(url)
        .thenApply(res -> res.entity().toString));
    }

    private CompletionStage<String> fetch(String  url) {
        return http.singleRequest(HttpRequest.create(url))
                .thenApply(r -> r.entity().toString());
    }
}
