package ru.ifmo.se.asurkis.sd;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.StatusRuntimeException;

import java.io.*;
import java.util.Iterator;

public class MainClient {
    private static void messageReader(Iterator<Message> messages) {
        while (messages.hasNext()) {
            final Message msg = messages.next();
            System.out.println(msg.getText());
        }
    }

    public static void main(String[] args) throws IOException, InterruptedException {
        ManagedChannel channel = ManagedChannelBuilder
                .forAddress("localhost", 4321)
                .usePlaintext()
                .build();
        ChatServerGrpc.ChatServerBlockingStub stub = ChatServerGrpc.newBlockingStub(channel);
        Iterator<Message> messages = stub.subscribe(Empty.newBuilder().build());
        Thread readerThread = new Thread(() -> messageReader(messages));
        readerThread.start();

        try (var reader = new BufferedReader(new InputStreamReader(System.in))) {
            String line;
            while ((line = reader.readLine()) != null) {
                stub.sendMessage(Message.newBuilder()
                        .setText(line)
                        .build());
            }
        }

        readerThread.join();
        channel.shutdown();
    }
}
