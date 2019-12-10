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
import java.util.logging.Logger;

import static akka.actor.TypedActor.context;
import static akka.http.javadsl.server.Directives.*;

public class Anonymization {
    private static Logger log = Logger.getLogger(Anonymization.class.getName());
    AsyncHttpClient asyncHttpClient;
    Materializer materializer;
    ActorRef storage;
    ZooKeeper zoo;
    Http http;

    public Anonymization(AsyncHttpClient asyncHttpClient, ActorRef storage, ActorMaterializer materializer, ZooKeeper zoo, Http http) {
        this.materializer = materializer;
        this.asyncHttpClient = asyncHttpClient;
        this.storage = storage;
        this.zoo = zoo;
        this.http = http;
    }

    public Route createRoute() {
        return concat(
                get(() ->
                        parameter("url", url ->
                                parameter("count", count ->
                                            //int c = Integer.parseInt(count);
                                            getUrlCount(url, Integer.parseInt(count))
                                ))));
    }

    private Route getUrlCount(String url, int count) {
        CompletionStage<HttpResponse> responseCompletionStage;
        if (count == 0) {
            responseCompletionStage = fetch(url);
        }
        else {
            responseCompletionStage = redirectiv(url, count-1);
        }
        return completeOKWithFutureString(responseCompletionStage
        .thenApply(Response::getResponseBody));
    }

    private CompletionStage<HttpResponse> redirectiv(String url, int count) {
        FutureConverters.toJava(Patterns.ask(storage, new GetRandom(), 5000))
                .thenApply(s -> ((ReturnServerMsg)s))
                .thenApply(ser -> ser.getServer())
                .thenCompose(server -> fetch(createRequest(server, url, count))
                )
        .thenApply(r -> r.entity().toString());

    }

    private String createRequest(String server, String url, int count) {
        return  server + "?url=" + url + "&" + count;

    }

    private CompletionStage<HttpResponse> fetch(String  url) {
        return http.singleRequest(HttpRequest.create(url));
    }
}
