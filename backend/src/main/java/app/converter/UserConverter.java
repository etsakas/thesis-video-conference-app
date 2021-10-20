package app.converter;

import app.resources.SignUpResource;
import app.domain.User;
import org.springframework.stereotype.Component;

@Component
public class UserConverter {
    public User getUser(SignUpResource signUpResource) {
        String username = signUpResource.getUsername();
        String password = signUpResource.getPassword();
        String email = signUpResource.getEmail();
        User user = new User(username, password, email);
        return user;
    }
}
