package com.mdev.messanger.client.connection;

import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.application.Platform;
import javafx.scene.Node;
import javafx.scene.image.Image;
import lombok.Setter;

import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.util.function.Consumer;

public class ClientThread {

    private final String username;
    private final String tag;
    private final int serverPort;
    private final String serverIp;
    private DatagramSocket socket;

    @Setter
    private MessageDispatcher messageDispatcher;

    public ClientThread(String username, String tag, int serverPort, String serverIp) throws SocketException {
        this.username = username;
        this.tag = tag;
        this.serverPort = serverPort;
        this.serverIp = serverIp;
        this.socket = new DatagramSocket(); // random port
    }

    public void listen(MessageDispatcher onMessageReceived) {
        new Thread(() -> {
            try {
                byte[] buffer = new byte[65507];
                DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
                while (true) {
                    socket.receive(packet);

                    String json = new String(packet.getData(), 0, packet.getLength(), StandardCharsets.UTF_8);
                    ObjectMapper mapper = new ObjectMapper();
                    MessageDTO receivedMessage = mapper.readValue(json, MessageDTO.class);

                    if (receivedMessage.getMessage().contains("__REGISTER__")){
                        continue;
                    }

                    onMessageReceived.onMessageReceived(receivedMessage);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
    }

    public void sendMessage(MessageDTO messageText) {
        try {

            ObjectMapper mapper = new ObjectMapper();
            String json = mapper.writeValueAsString(messageText); // Serialize to JSON
            byte[] jsonBytes = json.getBytes(StandardCharsets.UTF_8);

            DatagramPacket packet = new DatagramPacket(
                    jsonBytes, jsonBytes.length,
                    InetAddress.getByName(serverIp), serverPort);
            socket.send(packet);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void close() {
        socket.close();
    }
}