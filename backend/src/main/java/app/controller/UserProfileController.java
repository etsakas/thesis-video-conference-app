package app.controller;

import app.resources.MessageResource;
import app.resources.ProfileResource;
import app.service.ProfileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/")
public class UserProfileController {

    @Autowired
    private ProfileService profileService;
 
    @RequestMapping(value = "/profile", method = RequestMethod.GET)
    public ResponseEntity<ProfileResource> UserProfile(@RequestHeader("Authorization") String token) {
        
        return ResponseEntity.status(HttpStatus.CREATED).body(profileService.getUserProfile(token));
    }

    @RequestMapping(value = "/profile", method = RequestMethod.DELETE)
    public ResponseEntity<MessageResource> DeleteProfile(@RequestHeader("Authorization") String token) {
        
        profileService.deleteUser(token);

        return ResponseEntity.status(HttpStatus.OK).body(new MessageResource("User was deleted successfully"));
    }
}
