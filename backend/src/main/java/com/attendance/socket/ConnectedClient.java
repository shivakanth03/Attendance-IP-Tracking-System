package com.attendance.socket;

import lombok.*;
import java.time.LocalDateTime;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class ConnectedClient {
    private String clientId;
    private String ipAddress;
    private String hostname;
    private int port;
    private LocalDateTime connectedAt;
    private LocalDateTime disconnectedAt;
    private String status;  // ONLINE / OFFLINE
    private long bytesReceived;
    private long bytesSent;
}
