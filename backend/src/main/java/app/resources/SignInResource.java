package app.resources;

import javax.validation.constraints.NotNull;

public class SignInResource {
    @NotNull
    private String username;

    @NotNull
    private String password;

    public String getUsername() {
        return username;
    }
    
    public String getPassword() {
        return password;
    }

}
