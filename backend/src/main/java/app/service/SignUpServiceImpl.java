package app.service;

import app.converter.UserConverter;
import app.exception.EmailTakenException;
import app.exception.UserNameTakenException;
import app.resources.SignUpResource;
import app.domain.Role;
import app.domain.User;
import app.repo.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component("signUpService")
public class SignUpServiceImpl implements SignUpService {
    
    @Autowired
    private UserRepo userRepo;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    UserConverter userConverter;

    @Override
    public User saveUser(SignUpResource signUpResource) {

        if (userRepo.findByUserName(signUpResource.getUsername()) != null)
            throw new UserNameTakenException("This username is already taken.");
        if (userRepo.findByUserEmail(signUpResource.getEmail()) != null)
            throw new EmailTakenException("This email is already taken.");

        User user = userConverter.getUser(signUpResource);
        
        // Always save new users with client roles. Maybe only admins could give
        // admin role to clients in the future.
        user.setRole(Role.ROLE_CLIENT);
        user.setUserPassword(passwordEncoder.encode(signUpResource.getPassword()));

        userRepo.save(user);

        return user;
    }
}