package app.controller;

import javax.validation.Valid;
import app.exception.NoUserFoundException;
import app.exception.WrongCredentialsException;
import app.resources.SignInResource;
import app.resources.Token;
import app.service.SignInService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/")
public class SignInController {

    @Autowired
    SignInService signInService;

    @RequestMapping(value = "/signin", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Token> signUp(@Valid @RequestBody SignInResource signInResource) throws NoUserFoundException, WrongCredentialsException {
        
        Token token = signInService.signIn(signInResource);

        return ResponseEntity.status(HttpStatus.OK).body(token);
    }
}