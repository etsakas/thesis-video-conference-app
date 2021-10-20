package app.resources;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

// This class is going to be what the backend receives. Its better to seperate
// the received user and the final user which is going to be kept in the db's table.
// This way for example we can edit the received user's fields.
// A real case: say for whatever reason you want the received password to have a
// length [8, 16]. Having only one entity class with the @Size(min = 8, max = 16)
// would make it impossible to encrypt the password into a, let's say, 60 characters
// string, because annotations not only define what we receive but also what we can
// insert into the db's table.

public class SignUpResource {
    @NotNull
    @Size(min = 1, max = 16, message = "Username should be less or equal than 16 characters.")
    @Pattern(regexp = "[^\\s]*", message = "Username should not contain whitespaces.")
    @Pattern(regexp = "^[A-Za-zΑ-Ωα-ωΆ-Ώά-ώ].*$", message = "Username should should start with a letter.")
    private String username;

    @NotNull
    @Pattern(regexp = "[^\\s]*", message = "Password should not contain whitespaces.")
    @Size(min = 8, max = 16, message = "Password should be between 8 and 16 characters.")
    private String password;
    
    @NotNull
    @Size(min = 5, max = 30, message = "Email should be between 5 and 30 characters.")
    @Pattern(regexp = "[^A-ZΑ-ΩΆ-Ώ]*", message = "Email should not contain uppercase letters.")
    @Email(message = "Email should be valid.")
    private String email;

    // private SignUpResource() {}

    // If we are going to have a constructor with more than one arguments,
    // we MUST have a no-arguments constructor (why?) like the one above.
    // Otherwise we get an error during a post request
    
    // public SignUpResource(String username, String password, String email) {
    //     this.username = username;
    //     this.password = password;
    //     this.email = email;
    // }

    public String getUsername() {
        return username;
    }
    
    public String getPassword() {
        return password;
    }

    public String getEmail() {
        return email;
    }
}
