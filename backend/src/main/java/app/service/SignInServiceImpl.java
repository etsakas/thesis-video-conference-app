package app.service;

import app.exception.WrongCredentialsException;
import app.resources.SignInResource;
import app.resources.Token;
import app.security.JwtTokenProvider;
import app.domain.Role;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Component;

@Component("signInService")
public class SignInServiceImpl implements SignInService {

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Override
    public Token signIn(SignInResource signInResource) {

        String username = signInResource.getUsername();
        String password = signInResource.getPassword();

        // It seems that UsernamePasswordAuthenticationToken just holds the credentials.
        // When passed to authenticate method, UserDetails implementation is being used.
        // Because in our MyUserDetails we search of the user in the database, if the user
        // is not found we get a NoUserFoundException. We don't need to catch it,
        // GlobalExceptionHandler will resolve it.
        // If loadUserByUsername returns a user with .disabled(true) we also get a
        // AuthenticationException with message "User is disabled"
        try {
            Authentication auth = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));
            Token token = new Token(jwtTokenProvider.createToken(username, (Role)auth.getAuthorities().iterator().next()));
            token.setExpiresIn(jwtTokenProvider.getValidityInMilliseconds());
            return token;
        } catch (AuthenticationException e) {
            throw new WrongCredentialsException(e.getMessage());
        }
    }
}
