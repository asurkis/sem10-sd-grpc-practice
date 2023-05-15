package ru.ifmo.se.asurkis.sd;

import io.grpc.Server;
import io.grpc.ServerBuilder;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class MainServer {
    public static void main(String[] args) throws IOException, InterruptedException {
        ChatServerImpl service = new ChatServerImpl();
        Server server = ServerBuilder
                .forPort(4321)
                .addService(service)
                .build();
        server.start();

        try (var reader = new BufferedReader(new InputStreamReader(System.in))) {
            String line;
            while ((line = reader.readLine()) != null) {
                service.emitMessage(line);
            }
        }

        server.shutdown();
    }
}
