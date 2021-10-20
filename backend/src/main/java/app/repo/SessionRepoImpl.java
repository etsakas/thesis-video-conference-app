package app.repo;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import app.exception.SessionNotFoundException;
import app.exception.SessionObjectUsedException;
import org.springframework.stereotype.Component;
import io.openvidu.java.client.OpenViduRole;
import io.openvidu.java.client.Session;

@Component("sessionRepo")
public class SessionRepoImpl implements SessionRepo {

    // Collection to pair session names(=ids) and OpenVidu Session objects
    private Map<String, Session> mapSessions = new ConcurrentHashMap<>();

    // Pair session names and session passwords
    private Map<String, String> mapSessionPassword = new ConcurrentHashMap<>();

    // Pair session names and moderator's username
    private Map<String, String> mapSessionModerator = new ConcurrentHashMap<>();

    // Collection to pair session names and users (the inner Map pairs users and
    // role associated)
    private Map<String, Map<String, OpenViduRole>> mapSessionNameUsers = new ConcurrentHashMap<>();

    @Override
    public Session getSession(String sessionName) {
        return mapSessions.get(sessionName);
    }

    @Override
    public boolean sessionExists(String sessionName) {
        if (mapSessions.get(sessionName) == null) {
            return false;
        } else {
            return true;
        }
    }

    @Override
    public boolean addSession(Session session, String sessionName, String sessionPassword, String sessionModerator)
            throws SessionObjectUsedException {

        // We make sure that the session object is not already in the session repo
        if (mapSessions.containsValue(session)) {
            throw new SessionObjectUsedException("Session object is already used");
        } else if (mapSessions.get(sessionName) != null) {
            return false;
        } else {
            mapSessions.put(sessionName, session);
            mapSessionPassword.put(sessionName, sessionPassword);
            mapSessionModerator.put(sessionName, sessionModerator);
            mapSessionNameUsers.put(sessionName, new ConcurrentHashMap<>());
            return true;
        }
    }

    @Override
    public boolean addSessionUser(String sessionName, String userName, OpenViduRole role)
            throws SessionNotFoundException {

        if (mapSessions.get(sessionName) == null) {
            throw new SessionNotFoundException("Session does not exist");
        } else if (mapSessionNameUsers.get(sessionName).get(userName) != null) {
            return false;
        } else {
            mapSessionNameUsers.get(sessionName).put(userName, role);
            return true;
        }
    }

    @Override
    public boolean removeSessionUser(String sessionName, String userName) throws SessionNotFoundException {
        if (mapSessions.get(sessionName) == null) {
            throw new SessionNotFoundException("Session does not exist");
        } else if (mapSessionNameUsers.get(sessionName).get(userName) == null) {
            return false;
        } else {
            mapSessionNameUsers.get(sessionName).remove(userName);
            return true;
        }
    }

    @Override
    public String getSessionPassword(String sessionName) throws SessionNotFoundException {
        if (mapSessions.get(sessionName) == null) {
            throw new SessionNotFoundException("Session does not exist");
        }

        return mapSessionPassword.get(sessionName);
    }

    @Override
    public String getSessionModerator(String sessionName) throws SessionNotFoundException {
        if (mapSessions.get(sessionName) == null) {
            throw new SessionNotFoundException("Session does not exist");
        }

        return mapSessionModerator.get(sessionName);
    }

    @Override
    public boolean sessionIsEmpty(String sessionName) throws SessionNotFoundException {
        if (mapSessions.get(sessionName) == null) {
            throw new SessionNotFoundException("Session does not exist");
        }

        return mapSessionNameUsers.get(sessionName).isEmpty();
    }

    @Override
    public boolean removeEmptySession(String sessionName) throws SessionNotFoundException {
        if (mapSessions.get(sessionName) == null) {
            throw new SessionNotFoundException("Session does not exist");
        }

        if (!mapSessionNameUsers.get(sessionName).isEmpty()) {
            return false;
        } else {
            mapSessions.remove(sessionName);
            mapSessionPassword.remove(sessionName);
            mapSessionModerator.remove(sessionName);
            mapSessionNameUsers.remove(sessionName);
            return true;
        }
    }

    @Override
    public boolean removeSession(String sessionName) {
        if (mapSessions.get(sessionName) == null) {
            return false;
        }

        mapSessions.remove(sessionName);
        mapSessionPassword.remove(sessionName);
        mapSessionModerator.remove(sessionName);
        mapSessionNameUsers.remove(sessionName);
        return true;
    }

    @Override
    public Set<Session> getAllSessions() {
        return new HashSet<Session>(mapSessions.values());
    }

    @Override
    public boolean userIsInSession(String sessionName, String userName) throws SessionNotFoundException {
        if (mapSessions.get(sessionName) == null) {
            throw new SessionNotFoundException("Session does not exist");
        }

        if (mapSessionNameUsers.get(sessionName).keySet().contains(userName)) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public int getNonModeratorUsersCounter(String sessionName)
    throws SessionNotFoundException {
        if (mapSessions.get(sessionName) == null) {
            throw new SessionNotFoundException("Session does not exist");
        }

        Map<String, OpenViduRole> map = mapSessionNameUsers.get(sessionName);

        return (int)map.values().stream()
        .filter(role -> role == OpenViduRole.PUBLISHER).count();
    }
}
