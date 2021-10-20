package app.repo;

import java.util.Set;
import app.exception.SessionNotFoundException;
import app.exception.SessionObjectUsedException;
import org.springframework.stereotype.Repository;
import io.openvidu.java.client.OpenViduRole;
import io.openvidu.java.client.Session;

@Repository
public interface SessionRepo {

    // if session doesn't exist it returns null
    public Session getSession(String sessionName);

    public boolean sessionExists(String sessionName);
    
    // returns false if session name is already taken
    public boolean addSession(Session session, String sessionName, String sessionPassword, String sessionModerator)
    throws SessionObjectUsedException;

    // returns false if user is already in the session
    public boolean addSessionUser(String sessionName, String userName, OpenViduRole role)
    throws SessionNotFoundException;

    // returns false if user is not already in session
    public boolean removeSessionUser(String sessionName, String userName)
    throws SessionNotFoundException;

    public String getSessionPassword(String sessionName)
    throws SessionNotFoundException;

    public String getSessionModerator(String sessionName)
    throws SessionNotFoundException;

    public boolean sessionIsEmpty(String sessionName)
    throws SessionNotFoundException;

    // if session is non-empty it returns false
    public boolean removeEmptySession(String sessionName)
    throws SessionNotFoundException;

    // remove a session no matter if empty or not
    // returns false if the session doesn't exist
    // or true if removed successfully
    public boolean removeSession(String sessionName);

    public Set<Session> getAllSessions();

    public boolean userIsInSession(String sessionName, String userName)
    throws SessionNotFoundException;

    public int getNonModeratorUsersCounter(String sessionName)
    throws SessionNotFoundException;

}
