package app.service;

import app.exception.InvalidSessionPasswordException;
import app.exception.SessionIsFullException;
import app.exception.SessionNameTakenException;
import app.exception.SessionNotFoundException;
import app.resources.CreateSessionResource;
import app.resources.SessionNamePasswordResource;
import app.resources.SessionTokenResource;
import app.security.JwtTokenProvider;
import app.repo.SessionRepo;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import io.openvidu.java.client.Connection;
import io.openvidu.java.client.ConnectionProperties;
import io.openvidu.java.client.ConnectionType;
import io.openvidu.java.client.OpenVidu;
import io.openvidu.java.client.OpenViduRole;
import io.openvidu.java.client.Session;
import io.openvidu.java.client.SessionProperties;

@Component("sessionService")
public class SessionServiceImpl implements SessionService {

	@Autowired
	private SessionRepo sessionRepo;

	@Autowired
	private JwtTokenProvider jwtTokenProvider;

	public void createSession(String token, CreateSessionResource createSessionResource, OpenVidu openVidu) {

		String sessionName = createSessionResource.getSessionName();
		String sessionPassword = createSessionResource.getSessionPassword();

		if (sessionRepo.sessionExists(sessionName)) {
			throw new SessionNameTakenException("Session name is already taken. Use another one.");
		}

		// Extract username from token
		token = token.substring(7);
		String username = jwtTokenProvider.getUsername(token);

		try {
			// Create a new OpenVidu Session
			SessionProperties sessionProperties = new SessionProperties.Builder().customSessionId(sessionName).build();

			Session session = openVidu.createSession(sessionProperties);

			sessionRepo.addSession(session, sessionName, sessionPassword, username);

		} catch (Exception e) {
			System.out.println("Unknown exception while creating new session. Make sure openvidu server is up and running");
			throw new RuntimeException("Unknown exception while creating new session");
		}
	}

	// public void removeUser(String token, RemoveUserResource removeUserResource) {

	// 	String sessionName = removeUserResource.getSessionName();

	// 	// Extract username from token
	// 	token = token.substring(7);
	// 	String username = jwtTokenProvider.getUsername(token);

	// 	if (sessionRepo.sessionExists(sessionName)) {
	// 		if (!sessionRepo.removeSessionUser(sessionName, username)) {
	// 			throw new UserNotConnectedException("User " + username + " not found in session" + sessionName);
	// 		}
	// 	} else {
	// 		throw new SessionNotFoundException("Session does not exist");
	// 	}
	// }

	public SessionTokenResource getSessionToken(String token, SessionNamePasswordResource sessionNamePasswordResource) {

		String sessionName = sessionNamePasswordResource.getSessionName();
		String sessionPassword = sessionNamePasswordResource.getSessionPassword();

		Session session = sessionRepo.getSession(sessionName);

		if (session == null) {
			throw new SessionNotFoundException("Session does not exist");
		}

		if (!sessionPassword.equals(sessionRepo.getSessionPassword(sessionName))) {
			throw new InvalidSessionPasswordException("Wrong session password");
		}

		// Extract username from token
		token = token.substring(7);
		String username = jwtTokenProvider.getUsername(token);

		OpenViduRole role;
		String sessionModerator = sessionRepo.getSessionModerator(sessionName);
		if (username.equals(sessionModerator)) {
			role = OpenViduRole.MODERATOR;
		} else {
			role = OpenViduRole.PUBLISHER;
		}

		if (role == OpenViduRole.PUBLISHER && sessionRepo.getNonModeratorUsersCounter(sessionName) == 10) {
			throw new SessionIsFullException("Session is currently full");
		}

		// Optional data to be passed to other users when this user connects to the
		// video-call.
		String serverData = "{\"username\": \"" + username + "\"}";

		// Build connectionProperties object with the serverData and the role
		ConnectionProperties connectionProperties = new ConnectionProperties.Builder().type(ConnectionType.WEBRTC)
				.data(serverData).role(role).build();

		String sessionToken;
		try {
			sessionToken = session.createConnection(connectionProperties).getToken();

		} catch (Exception e) {
			// We could get to this point if the session is closed on the openvidu server.
			// In this case we would get an OpenViduHttpException.
			// We assume that the Map holds only active session. This means that we should
			// never receive an exception here.
			System.out.println(
					"Unknown exception while creating new session. Make sure OpenVidu server is up and running.");
			throw new RuntimeException(
					"Unknown exception while creating new session. Make sure OpenVidu server is up and running.");
		}
		
		SessionTokenResource sessionTokenResource = new SessionTokenResource(sessionName, sessionModerator, sessionToken);

		return sessionTokenResource;

	}

	public void manageWebhookEvent(Map<String, String> eventMap) {
		String event = eventMap.get("event");

		if (event.equals("participantJoined")) {

			String sessionName = eventMap.get("sessionId");
			String userName = "";
			OpenViduRole role;

			String serverDataJson = eventMap.get("serverData");
			ObjectMapper objectMapper = new ObjectMapper();

			try {
				JsonNode jsonNode = objectMapper.readTree(serverDataJson);
				userName = jsonNode.get("username").asText();
			} catch (Exception ex) {} // Should never go here

			// Since a frontend client can receive multiple session tokens,
			// we must make sure that every user is only logged in once.
			// For this reason we check that the user is not already logged in.
			// If he is, we close the older connection.
			if (sessionRepo.userIsInSession(sessionName, userName)) {
				Session session = sessionRepo.getSession(sessionName);
				try {
					// I think we may get an exception if either session doesn't exists
					// or spring boot server won't be able to communicate with openvidu server.
					// We never want this to happen so we don't handle the error because it
					// indicates that something is wrong with our app.
					session.fetch();
				} catch (Exception ex) {};
				List<Connection> activeConnections = session.getActiveConnections();
				// There would be two active connections with the same username that we passed
				// as serverData. We need to compare the createdAt in order to close the
				// older one.
				
				List<Connection> userConnnections = new LinkedList<Connection>();
				for (Connection con: activeConnections) {
					try {
						JsonNode jsonNode = objectMapper.readTree(con.getServerData());
						String userNameAux = jsonNode.get("username").asText();
						if (userNameAux.equals(userName)) {
							userConnnections.add(con);
						}
					} catch (Exception ex) {};
				}
				
				Connection connection1 = userConnnections.get(0);
				Connection connection2 = userConnnections.get(1);

				try {
					if (connection1.createdAt() > connection2.createdAt()) {
						session.forceDisconnect(connection1);
					} else {
						session.forceDisconnect(connection2);
					}
				} catch (Exception ex) {};

				return;
			}

			// If the user happens to use the token in order to connect
			// when the session is full we have to close his connection.
			// I think that the conditions' flow matters.
			// If we first done the check sessionRepo.getNonModeratorUsersCounter(sessionName) == 10
			// followed by sessionRepo.userIsInSession(sessionName, userName)
			// it would be possible that we would kick a user that logged in twice
			// but we won't know which of the two connections we closed.
			if (sessionRepo.getNonModeratorUsersCounter(sessionName) == 10) {
				Session session = sessionRepo.getSession(sessionName);
				try {
					session.fetch();
				} catch (Exception ex) {};
				List<Connection> activeConnections = session.getActiveConnections();
				Connection userConnnection = null;
				for (Connection con: activeConnections) {
					try {
						JsonNode jsonNode = objectMapper.readTree(con.getServerData());
						String userNameAux = jsonNode.get("username").asText();
						if (userNameAux.equals(userName)) {
							userConnnection = con;
							break;
						}
					} catch (Exception ex) {};
				}
				try {
					session.forceDisconnect(userConnnection);
				} catch (Exception ex) {};

				return;
			}

			String sessionModerator = sessionRepo.getSessionModerator(sessionName);

			if (sessionModerator.equals(userName)) {
				role = OpenViduRole.MODERATOR;
			} else {
				role = OpenViduRole.PUBLISHER;
			}

			sessionRepo.addSessionUser(sessionName, userName, role);

		} else if (event.equals("participantLeft")) {

			// if the user left with event reason = "forceDisconnectByServer"
			// it means that the user logged in a second time and we diconnected
			// the older connection. The user is still connected so we must
			// keep him logged in.
			String reason = eventMap.get("reason");
			if (reason.equals("forceDisconnectByServer")) {
				return;
			}

			String sessionName = eventMap.get("sessionId");
			String userName = "";

			String serverDataJson = eventMap.get("serverData");
			ObjectMapper objectMapper = new ObjectMapper();

			try {
				JsonNode jsonNode = objectMapper.readTree(serverDataJson);
				userName = jsonNode.get("username").asText();
			} catch (Exception ex) {} // Should never go here

			sessionRepo.removeSessionUser(sessionName, userName);

			if (sessionRepo.sessionIsEmpty(sessionName)) {
				sessionRepo.removeEmptySession(sessionName);
			}

		} else if (event.equals("sessionDestroyed")) {
			// Normally we should not destroy a session on sessionDestroyed event
			// if the spring boot server receives every participantLeft event and 
			// removes the user. We can destroy however the session in case
			// we receive sessionDestroyed event for whatever reason
			String sessionName = eventMap.get("sessionId");
			if (sessionRepo.removeSession(sessionName)) {
				System.out.println("sessionDestroyed event: removed a possibly non-empty session");
			}
		}

	}
}
