package ru.ifmo.se.asurkis.sd;

import io.grpc.stub.StreamObserver;

import java.util.ArrayList;
import java.util.List;

public class ChatServerImpl extends ChatServerGrpc.ChatServerImplBase {
    private List<StreamObserver<Message>> observers = new ArrayList<>();
    
    @Override
    public void sendMessage(Message request, StreamObserver<Empty> responseObserver) {
        System.out.println(request.getText());
        responseObserver.onNext(Empty.newBuilder().build());
        responseObserver.onCompleted();
    }

    @Override
    public void subscribe(Empty request, StreamObserver<Message> responseObserver) {
        observers.add(responseObserver);
    }

    public void emitMessage(String message) {
        Message msg = Message.newBuilder()
                .setText(message)
                .build();
        for (StreamObserver<Message> obs : observers) {
            obs.onNext(msg);
        }
    }

    public void shutdown() {
        for (StreamObserver<Message> obs : observers) {
            obs.onCompleted();
        }
    }
}
