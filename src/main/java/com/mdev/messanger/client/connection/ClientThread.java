package com.mdev.messanger.client.connection;

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
            byte[] buffer = new byte[65507];
            try {
                while (true) {
                    DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
                    socket.receive(packet);

                    ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(packet.getData(), 0, packet.getLength());
                    ObjectInputStream objectInputStream = new ObjectInputStream(byteArrayInputStream);

                    MessageDTO receivedMessage = (MessageDTO) objectInputStream.readObject();

                    if (receivedMessage.getMessage().contains("__REGISTER__")){
                        continue;
                    }

                    messageDispatcher.onMessageReceived(receivedMessage);
                }
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            }
        }).start();
    }

    public void sendMessage(MessageDTO messageText) {
        try {
            ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
            ObjectOutputStream objectStream = new ObjectOutputStream(byteStream);
            objectStream.writeObject(messageText);
            objectStream.flush();

            byte[] sendData = byteStream.toByteArray();

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