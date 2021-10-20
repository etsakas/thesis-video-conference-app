package app.controller;

import javax.validation.Valid;

import app.domain.User;
import app.exception.EmailTakenException;
import app.exception.UserNameTakenException;
import app.resources.SignUpResource;
import app.service.SignUpService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

// We are going to use React so server won't send views
// For this reason we will use RestController instead of
// Controller and ResponseBody for each method
@RestController
@RequestMapping("/")
public class SignUpController {
    
    @Autowired
    SignUpService signUpService;

    @RequestMapping(value = "/signup", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<User> signUp(@Valid @RequestBody SignUpResource signUpResource) throws UserNameTakenException, EmailTakenException {
        
        User user = signUpService.saveUser(signUpResource);

        System.out.println(user);
        return ResponseEntity.status(HttpStatus.CREATED).body(user);
    }
}