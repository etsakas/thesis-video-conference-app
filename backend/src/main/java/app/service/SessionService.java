package app.service;

import java.util.Map;

import app.resources.CreateSessionResource;
import app.resources.SessionNamePasswordResource;
import app.resources.SessionTokenResource;
import org.springframework.stereotype.Service;
import io.openvidu.java.client.OpenVidu;

@Service
public interface SessionService {

    public void createSession(String token, CreateSessionResource createSessionResource,
    OpenVidu openVidu);

    // public void removeUser(String token, RemoveUserResource removeUserResource);

    // Throws SessionIsFullException if there are already 10 non-moderator users in the session
    public SessionTokenResource getSessionToken(String token, SessionNamePasswordResource sessionNamePasswordResource);

    public void manageWebhookEvent(Map<String, String> eventMap);
}
