package com.mdev.messanger.connection;

import javafx.application.Platform;

import java.io.IOException;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.util.function.Consumer;

public class ClientThread {

    private final String username;
    private final String tag;
    private final int serverPort;
    private final String serverIp;
    private DatagramSocket socket;

    public ClientThread(String username, String tag, int serverPort, String serverIp) throws SocketException {
        this.username = username;
        this.tag = tag;
        this.serverPort = serverPort;
        this.serverIp = serverIp;
        this.socket = new DatagramSocket(); // random port
    }

    public void listen(Consumer<String> onMessageReceived) {
        new Thread(() -> {
            try {
                byte[] buffer = new byte[1024];
                while (true) {
                    DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
                    socket.receive(packet);
                    String received = new String(packet.getData(), 0, packet.getLength(), StandardCharsets.UTF_8);

                    Platform.runLater(() -> onMessageReceived.accept(received));
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
    }

    public void sendMessage(String messageText) {
        try {
            String fullMessage = username + "#" + tag + ":" + messageText;
            byte[] sendData = fullMessage.getBytes(StandardCharsets.UTF_8);
            DatagramPacket packet = new DatagramPacket(sendData, sendData.length,
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