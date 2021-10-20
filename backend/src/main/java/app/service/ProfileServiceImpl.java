package app.service;

import app.resources.ProfileResource;
import app.security.JwtTokenProvider;
import app.domain.User;
import app.repo.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component("profileService")
public class ProfileServiceImpl implements ProfileService {

    @Autowired
    private UserRepo userRepo;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @Override
    public ProfileResource getUserProfile(String token) {
        // Remove "Bearer "
        token = token.substring(7);

        String username = jwtTokenProvider.getUsername(token);

        // At this point, the program got through jwt filter in which
        // MyUserDetails checks that the user exists and that
        // is not deleted. So, we can retrieve user's data immediately.

        User user = userRepo.findByUserName(username);
        
        // Here we return whatever user's profile consists of. It could be anything,
        // birthdate, sex, age etc.
        // We just return his username.
        ProfileResource profileResource = new ProfileResource(user.getUserName());
        
        return profileResource;
    }

    @Override
    public boolean deleteUser(String token) {
        // Remove "Bearer "
        token = token.substring(7);

        String username = jwtTokenProvider.getUsername(token);

        User user = userRepo.findByUserName(username);
        
        user.deleteUser();
        
        // update user as deleted
        userRepo.save(user);

        return true;
    }
}
