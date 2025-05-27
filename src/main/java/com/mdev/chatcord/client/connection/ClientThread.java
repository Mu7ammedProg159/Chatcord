package com.mdev.chatcord.client.connection;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mdev.chatcord.client.message.dto.MessageDTO;
import lombok.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;

import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;

@Controller
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
public class ClientThread {

    @Value("${spring.application.udp.server.port}")
    private int serverPort;
    @Value("${spring.application.udp.server.ip}")
    private String serverIp;

    private DatagramSocket socket;

    @Setter
    private MessageDispatcher messageDispatcher;

    public void listen(MessageDispatcher onMessageReceived) {
        try {
            socket = new DatagramSocket();
        } catch (SocketException e) {
            throw new RuntimeException(e);
        }
        new Thread(() -> {
            System.out.println("Started listening...");
            try {
                byte[] buffer = new byte[65507];
                DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
                while (true) {
                    socket.receive(packet);

                    String json = new String(packet.getData(), 0, packet.getLength(), StandardCharsets.UTF_8);
                    ObjectMapper mapper = new ObjectMapper();
                    MessageDTO receivedMessage = mapper.readValue(json, MessageDTO.class);

                    if (receivedMessage.getContent().contains("__REGISTER__")){
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

            System.out.println("Sending message: " + json);

            socket.send(packet);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void close() {
        socket.close();
    }
}