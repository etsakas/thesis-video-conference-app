package app.sync;

import java.time.Instant;
import java.util.Iterator;
import java.util.Set;
import app.repo.SessionRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import io.openvidu.java.client.Session;

// This implementation makes sense only if we disable openvidu's default configuration
// that closes the sessions that never got a user
// (run with: -e OPENVIDU_SESSIONS_GARBAGE_INTERVAL=0)

@Component
public class EmptySessionTimer {
    
	@Autowired
	private SessionRepo sessionRepo;
    
	@Scheduled(fixedRate = 60000)
	public void removeNonUsedSession() {
		Set<Session> sessionSet = sessionRepo.getAllSessions();

        Iterator<Session> sessionIterator = sessionSet.iterator();

        while(sessionIterator.hasNext()) {
            Session session = sessionIterator.next();
            String sessionName = session.getSessionId();

            if (!sessionRepo.sessionIsEmpty(sessionName)) {
                continue;
            }

            long createdAt = session.createdAt();
            long currentTime = Instant.now().toEpochMilli();
            
            // 15 minutes (900000 ms) before closing a created session that no user was ever connected
            // to it. This means that a newly created session can be alive from
            // 15 minutes up to 15 minutes plus timer's period (1 minute)
            if (currentTime - createdAt > 900000) {
                try {
                    System.out.println("Removing empty session " + session.getSessionId());
                    session.close();
                    sessionRepo.removeEmptySession(session.getSessionId());
                } catch (Exception ex) {
                    // An exception could be thrown if the session is already closed
                    // on the openvidu side.
                    System.out.println("Timer could not close empty session!!!");
                }

            }
         }
	}

}
