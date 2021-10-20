package app.resources;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

public class CreateSessionResource {

    @NotNull
    @Size(min = 1, max = 16, message = "Session name should be less or equal than 16 characters.")
    @Pattern(regexp = "[^\\s]*", message = "Session name should not contain whitespaces.")
    @Pattern(regexp = "^[A-Za-zΑ-Ωα-ωΆ-Ώά-ώ].*$", message = "Session name should should start with a letter.")
    private String sessionName;

    @NotNull
    @Pattern(regexp = "[^\\s]*", message = "Password should not contain whitespaces.")
    @Size(min = 8, max = 16, message = "Password should be between 8 and 16 characters.")
    private String sessionPassword;

    public String getSessionName() {
        return this.sessionName;
    }

    public String getSessionPassword() {
        return this.sessionPassword;
    }
}
