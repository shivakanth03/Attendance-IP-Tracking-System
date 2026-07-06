package com.attendance.socket;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Java Socket Server for tracking connected client devices.
 * Runs as a background thread on a configurable port (default: 9090).
 *
 * <p>Maintains a map of connected clients and broadcasts updates
 * to the admin dashboard via WebSocket.
 */
@Slf4j
@Component
public class SocketServer {

    @Value("${app.socket.server.port:9090}")
    private int serverPort;

    @Value("${app.socket.server.max-connections:100}")
    private int maxConnections;

    @Value("${app.socket.server.enabled:true}")
    private boolean enabled;

    /** Map of clientId → ClientInfo for all connected clients */
    private final ConcurrentHashMap<String, ConnectedClient> connectedClients =
        new ConcurrentHashMap<>();

    private final AtomicBoolean running = new AtomicBoolean(false);
    private ServerSocket serverSocket;
    private ExecutorService executorService;

    /**
     * Start the socket server after Spring Boot finishes starting.
     */
    @EventListener(ApplicationReadyEvent.class)
    public void startServer() {
        if (!enabled) {
            log.info("Socket server is disabled");
            return;
        }

        executorService = Executors.newCachedThreadPool(r -> {
            Thread t = new Thread(r, "socket-client-handler");
            t.setDaemon(true);
            return t;
        });

        Thread serverThread = new Thread(this::runServer, "socket-server-main");
        serverThread.setDaemon(true);
        serverThread.start();
    }

    private void runServer() {
        try {
            serverSocket = new ServerSocket(serverPort);
            running.set(true);
            log.info("Socket Server started on port {}", serverPort);

            while (running.get()) {
                if (connectedClients.size() >= maxConnections) {
                    log.warn("Max connections reached ({}), waiting...", maxConnections);
                    Thread.sleep(1000);
                    continue;
                }

                try {
                    Socket clientSocket = serverSocket.accept();
                    String clientId = clientSocket.getInetAddress().getHostAddress()
                        + ":" + clientSocket.getPort();

                    ConnectedClient client = ConnectedClient.builder()
                        .clientId(clientId)
                        .ipAddress(clientSocket.getInetAddress().getHostAddress())
                        .hostname(clientSocket.getInetAddress().getHostName())
                        .port(clientSocket.getPort())
                        .connectedAt(java.time.LocalDateTime.now())
                        .status("ONLINE")
                        .build();

                    connectedClients.put(clientId, client);
                    log.info("Client connected: {} | Total: {}", clientId, connectedClients.size());

                    // Handle client in thread pool
                    executorService.submit(new ClientHandler(clientSocket, clientId, connectedClients));

                } catch (IOException e) {
                    if (running.get()) {
                        log.error("Error accepting connection: {}", e.getMessage());
                    }
                }
            }
        } catch (IOException e) {
            log.error("Socket server failed to start on port {}: {}", serverPort, e.getMessage());
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    public void stopServer() {
        running.set(false);
        try {
            if (serverSocket != null && !serverSocket.isClosed()) {
                serverSocket.close();
            }
            if (executorService != null) {
                executorService.shutdownNow();
            }
        } catch (IOException e) {
            log.error("Error stopping socket server: {}", e.getMessage());
        }
        log.info("Socket Server stopped");
    }

    public List<ConnectedClient> getConnectedClients() {
        return List.copyOf(connectedClients.values());
    }

    public int getConnectedClientCount() {
        return connectedClients.size();
    }

    public boolean isRunning() {
        return running.get();
    }
}
