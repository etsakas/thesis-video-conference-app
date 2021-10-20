package app.resources;

public class SessionTokenResource {

    private String token;
    private String sessionName;
    private String sessionModerator;

    private SessionTokenResource() {}

    public SessionTokenResource(String sessionName, String sessionModerator, String token) {
        this.sessionName = sessionName;
        this.sessionModerator = sessionModerator;
        this.token = token;
    }

    public String getSessionName() {
        return sessionName;
    }

    public void setSessionName(String sessionName) {
        this.sessionName = sessionName;
    }

    public String getSessionModerator() {
        return sessionModerator;
    }

    public void setSessionModerator(String sessionModerator) {
        this.sessionModerator = sessionModerator;
    }

    public String getToken() {
        return this.token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
