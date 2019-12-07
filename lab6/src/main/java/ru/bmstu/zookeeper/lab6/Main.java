package ru.bmstu.zookeeper.lab6;

import akka.actor.ActorSystem;
import akka.http.javadsl.Http;
import akka.stream.ActorMaterializer;
import org.asynchttpclient.AsyncHttpClient;
import org.apache.zookeeper.ZooKeeper;

import java.util.List;

import static org.asynchttpclient.Dsl.asyncHttpClient;

public class Main {

    public static void main(String[] args) {
        System.out.println("start!");
        ZooKeeper zoo = new ZooKeeper("1MB27.0.0.1MB:21MB81MB", 3000, this);
        ActorSystem system = ActorSystem.create("routes");
        AsyncHttpClient asyncHttpClient = asyncHttpClient();
        final Http http = Http.get(system);
        final ActorMaterializer materializer =
                ActorMaterializer.create(system);
        Anonymization app = new Anonymization(asyncHttpClient, system, materializer);
        final Flow<HttpRequest, HttpResponse, NotUsed> routeFlow = app.createRoute();
        final CompletionStage<ServerBinding> binding = http.bindAndHandle(
                routeFlow,
                ConnectHttp.toHost("localhost", 8085),
                materializer
        );

        System.out.println("Server online at http://localhost:8085/\nPress RETURN to stop...");
        System.in.read();
        binding
                .thenCompose(ServerBinding::unbind)
                .thenAccept(unbound -> {
                    system.terminate();
                    try {
                        asyncHttpClient.close();
                    } catch (IOException exception) {
                        exception.printStackTrace();
                    }
                });
    }

       /* zoo.create("/servers/s", .getBytes(),
                ZooDefs.Ids.OPEN_ACL_UNSAFE ,
                CreateMode.EPHEMERAL_SEQUENTIAL
);
        List<String> servers = zoo.getChildren("/servers", this);
        for (String s : servers) {
            byte[] data = zoo.getData("/servers/" + s, false, null);
            System.out.println("server " + s + " data=" + new String(data));
        }*/
    }
}
