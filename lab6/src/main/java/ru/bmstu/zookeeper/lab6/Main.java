package ru.bmstu.zookeeper.lab6;

import akka.NotUsed;
import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.http.javadsl.ConnectHttp;
import akka.http.javadsl.Http;
import akka.http.javadsl.ServerBinding;
import akka.http.javadsl.model.HttpRequest;
import akka.http.javadsl.model.HttpResponse;
import akka.stream.ActorMaterializer;
import akka.stream.javadsl.Flow;
import org.asynchttpclient.AsyncHttpClient;
import org.apache.zookeeper.ZooKeeper;

import java.util.concurrent.CompletionStage;
import java.util.logging.Logger;

import static org.asynchttpclient.Dsl.asyncHttpClient;

public class Main {

    public static void main(String[] args) throws Exception{

        if (args.length != 2) {
            System.out.println("Anonymizer");
        }

         Logger log = Logger.getLogger(Main.class.getName());
        final String host = args[0];
        final int port = Integer.parseInt(args[1]);

        System.out.println("start!");
        ZooKeeper zoo = new ZooKeeper("127.0.0.1:2181", 3000, e->log.info(e.toString()));
        ActorSystem system = ActorSystem.create("routes");
        ActorRef storage = system.actorOf(Props.create(StorageActor.class));
        AsyncHttpClient asyncHttpClient = asyncHttpClient();
        final Http http = Http.get(system);
        final ActorMaterializer materializer =
                ActorMaterializer.create(system);
        Server server = new Server(zoo, storage);
        server.createServer(host, port, "localhost:" + port);
        Anonymization app = new Anonymization(asyncHttpClient, storage, materializer, zoo, http);
        final Flow<HttpRequest, HttpResponse, NotUsed> routeFlow = app.createRoute().flow(system, materializer);
        final CompletionStage<ServerBinding> binding = http.bindAndHandle(
                routeFlow,
                ConnectHttp.toHost(host, port),
                materializer
        );

        System.out.println("Server online at" + host + port  +"/\nPress RETURN to stop...");
        System.in.read();
        asyncHttpClient.close();
        server.close();
        zoo.close();
        binding
                .thenCompose(ServerBinding::unbind)
                .thenAccept(unbound -> system.terminate());
    }
}
