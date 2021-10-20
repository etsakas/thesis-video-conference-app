package app.resources;

import javax.validation.constraints.NotNull;

public class SessionNamePasswordResource {

    @NotNull
    private String sessionName;

    @NotNull
    private String sessionPassword;

    public String getSessionName() {
        return this.sessionName;
    }

    public String getSessionPassword() {
        return this.sessionPassword;
    }
}
