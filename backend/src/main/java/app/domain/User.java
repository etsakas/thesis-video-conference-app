package app.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

// Since we filter SignUpResource with its' own annotations, we actually
// don't need annotations in this class. But they define the table's
// constraints and we just keep them.

@Entity
@Table(name = "users")
public class User {

    @Id
    @Column(name = "name", nullable = false, length = 16, unique = true)
    @NotNull
    @Size(min = 1, max = 16, message = "Username should be less or equal than 16 characters.")
    @Pattern(regexp = "[^\\s]*", message = "Username should not contain whitespaces.")
    @Pattern(regexp = "^[A-Za-zΑ-Ωα-ωΆ-Ώά-ώ].*$", message = "Username should should start with a letter.")
    private String userName;

    // Notice that password's max length is now 60. This is because we keep
    // encrypted password in our db
    @Column(name = "password", nullable = false, length = 60, unique = false)
    @NotNull
    @Pattern(regexp = "[^\\s]*", message = "Password should not contain whitespaces.")
    @Size(min = 8, max = 60, message = "Password should be between 8 and 60 characters.")
    private String userPassword;

    @Column(name = "email", nullable = false, length = 30, unique = true)
    @NotNull
    @Size(min = 5, max = 30, message = "Email should be between 5 and 30 characters.")
    @Pattern(regexp = "[^A-ZΑ-ΩΆ-Ώ]*", message = "Email should not contain uppercase letters.")
    @Email(message = "Email should be valid.")
    private String userEmail;

    @Column(name = "role", nullable = false)
    Role userRole;

    @Column(name = "deleted")
    Boolean deleted;

    private User() {
    }

    // If we are going to have a constructor with more than one arguments,
    // we MUST have a no-arguments constructor (why?) like the one above.
    // Otherwise we get an error during a post request

    public User(String userName, String userPassword, String userEmail) {
        this.userName = userName;
        this.userPassword = userPassword;
        this.userEmail = userEmail;
        this.deleted = false;
    }

    @Override
    public String toString() {
        StringBuilder userInfo = new StringBuilder();
        userInfo.append("User [userName = ").append(userName).append(", userPassword = ").append(userPassword)
                .append(", userEmail = ").append(userEmail).append(", userRole = ").append(userRole).append("]");
        return userInfo.toString();
    }

    public void deleteUser() {
        this.deleted = true;
    }

    public Boolean isDeleted() {
        return this.deleted;
    }

    public String getUserName() {
        return userName;
    }

    public String getUserPassword() {
        return userPassword;
    }

    public void setUserPassword(String userPassword) {
        this.userPassword = userPassword;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public Role getRole() {
        return userRole;
    }

    public void setRole(Role userRole) {
        this.userRole = userRole;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        User user = (User) o;

        if (userEmail.equals(user.getUserEmail()))
            return true;
        if (userName.equals(user.getUserName()))
            return true;

        return false;
    }

    @Override
    public int hashCode() {
        return userEmail.hashCode() + userName.hashCode();
    }
}