package com.attendance.service.interfaces;

import com.attendance.dto.attendance.SessionRequest;
import com.attendance.dto.attendance.SessionResponse;
import java.util.List;

public interface SessionService {
    SessionResponse createSession(SessionRequest request);
    SessionResponse closeSession(Long id);
    SessionResponse pauseSession(Long id);
    SessionResponse resumeSession(Long id);
    SessionResponse getSessionById(Long id);
    List<SessionResponse> getActiveSessions();
    byte[] getSessionQrImage(Long id);
}
