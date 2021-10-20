package app.service;

import app.resources.ProfileResource;
import org.springframework.stereotype.Service;

@Service
public interface ProfileService {
    public ProfileResource getUserProfile(String token);
    public boolean deleteUser(String token);
}
