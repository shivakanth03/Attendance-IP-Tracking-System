package com.attendance.socket;

import lombok.extern.slf4j.Slf4j;
import java.io.*;
import java.net.Socket;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
public class ClientHandler implements Runnable {
    private final Socket socket;
    private final String clientId;
    private final ConcurrentHashMap<String, ConnectedClient> clients;

    public ClientHandler(Socket socket, String clientId,
                         ConcurrentHashMap<String, ConnectedClient> clients) {
        this.socket = socket;
        this.clientId = clientId;
        this.clients = clients;
    }

    @Override
    public void run() {
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(socket.getInputStream()));
             PrintWriter writer = new PrintWriter(socket.getOutputStream(), true)) {

            writer.println("CONNECTED:" + clientId);
            String line;
            while ((line = reader.readLine()) != null) {
                log.debug("Message from {}: {}", clientId, line);
                ConnectedClient client = clients.get(clientId);
                if (client != null) {
                    client.setBytesReceived(client.getBytesReceived() + line.length());
                }
                if ("PING".equalsIgnoreCase(line.trim())) {
                    writer.println("PONG");
                }
            }
        } catch (IOException e) {
            log.debug("Client {} disconnected: {}", clientId, e.getMessage());
        } finally {
            ConnectedClient client = clients.get(clientId);
            if (client != null) {
                client.setStatus("OFFLINE");
                client.setDisconnectedAt(java.time.LocalDateTime.now());
            }
            clients.remove(clientId);
            log.info("Client {} removed. Active: {}", clientId, clients.size());
        }
    }
}
