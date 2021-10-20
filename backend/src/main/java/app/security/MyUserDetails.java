package app.security;

import app.exception.DeletedAccountException;
import app.exception.NoUserFoundException;
import app.domain.User;
import app.repo.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

@Service
public class MyUserDetails implements UserDetailsService {
    
    @Autowired
    private UserRepo userRepo;

    @Override
    public UserDetails loadUserByUsername(String username) throws NoUserFoundException {
      User user = userRepo.findByUserName(username);
  
      if (user == null) {
        throw new NoUserFoundException("User '" + username + "' not found");
      } else if (user.isDeleted()) {
        throw new DeletedAccountException("User '" + username + "' is deleted and can't be used");
      }
      
      return org.springframework.security.core.userdetails.User
          .withUsername(username)
          .password(user.getUserPassword())
          .authorities(user.getRole())
          .accountExpired(false)
          .accountLocked(false)
          .credentialsExpired(false)
          .disabled(false)
          .build();
    }
}

// In order for spring to authenticate users like we do in SignInServiceImpl
// (authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));)
// we need to provide 2 beans. The first one is the PasswordEncoder (see PasswordEncoderConfig.java)
// The second bean is a class that implements UserDetailsService interface.
// UserDetailsService has the method public UserDetails loadUserByUsername
// UserDetails is an interface that we can override, however spring provides a default
// implementation, the org.springframework.security.core.userdetails.User
// We use this implementation to map our database User fields with spring's User fields.