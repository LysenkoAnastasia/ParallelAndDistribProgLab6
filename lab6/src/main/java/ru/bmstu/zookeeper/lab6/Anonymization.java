package ru.bmstu.zookeeper.lab6;

import akka.actor.ActorRef;
import akka.http.javadsl.Http;
import akka.http.javadsl.model.HttpRequest;
import akka.http.javadsl.server.Route;
import akka.pattern.Patterns;
import akka.stream.ActorMaterializer;
import akka.stream.Materializer;
import org.apache.zookeeper.ZooKeeper;
import org.asynchttpclient.AsyncHttpClient;
import org.asynchttpclient.Request;
import org.asynchttpclient.Response;
import scala.compat.java8.FutureConverters;

import java.util.concurrent.CompletionStage;
import java.util.logging.Logger;

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
        CompletionStage<Response> responseCompletionStage;
        if (count == 0) {
            responseCompletionStage = fetch(
                    asyncHttpClient
                    .prepareGet(url)
                    .build());
        }
        else {
            responseCompletionStage = redirectiv(url, count-1);
        }
        return completeOKWithFutureString(responseCompletionStage
        .thenApply(Response::getResponseBody));
    }

    private CompletionStage<Response> redirectiv(String url, int count) {
        return FutureConverters.toJava(Patterns.ask(storage, new GetRandom(), 5000))
                .thenApply(s -> ((ReturnServerMsg)s))
                .thenApply(ser -> ser.getServer())
                .thenCompose(server -> fetch(createRequest(getServerUrl(server), url, count))
                        .handle((response, exeption) -> {
                            storage.tell(new DeleteServer(server), ActorRef.noSender());
                            return response;
                        })
                );
    }

    private Request createRequest(String server, String url, int count) {
        return  asyncHttpClient.prepareGet(server)
                .addQueryParam("url", url)
                .addQueryParam("count", Integer.toString(count))
                .build();
    }

    private CompletionStage<Response> fetch(Request  request) {
        return asyncHttpClient.executeRequest(request).toCompletableFuture();
    }

    private String getServerUrl(String server){
        try {
            return new String(zoo.getData(server, false, null));
        } catch (Exception exeption){
            throw new RuntimeException(exeption);

        }
    }
}

