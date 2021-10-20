package app.service;

import app.resources.SignInResource;
import app.resources.Token;
import org.springframework.stereotype.Service;

@Service
public interface SignInService {

    public Token signIn(SignInResource signInResource);
}
