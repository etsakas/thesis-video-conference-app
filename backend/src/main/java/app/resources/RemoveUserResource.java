package app.resources;

import javax.validation.constraints.NotNull;

public class RemoveUserResource {

    @NotNull
    String sessionName;

    public String getSessionName() {
        return this.sessionName;
    }
}
