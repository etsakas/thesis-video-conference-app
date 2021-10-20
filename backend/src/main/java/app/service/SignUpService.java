package app.service;

import app.resources.SignUpResource;
import app.domain.User;
import org.springframework.stereotype.Service;

@Service
public interface SignUpService {

    public User saveUser(SignUpResource signUpResource);
}
