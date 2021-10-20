package app.controller;

import javax.validation.Valid;
import app.resources.CreateSessionResource;
import app.resources.MessageResource;
import app.resources.SessionNamePasswordResource;
import app.resources.SessionTokenResource;
import app.service.SessionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import io.openvidu.java.client.OpenVidu;

@RestController
@RequestMapping("/sessions")
public class SessionController {

    // OpenVidu object as entrypoint of the SDK
    private OpenVidu openVidu;

    // URL where our OpenVidu server is listening
    private String OPENVIDU_URL;

    // Secret shared with our OpenVidu server
    private String SECRET;

    public SessionController(@Value("${openvidu.secret}") String secret, @Value("${openvidu.url}") String openviduUrl) {
        this.SECRET = secret;
        this.OPENVIDU_URL = openviduUrl;
        this.openVidu = new OpenVidu(OPENVIDU_URL, SECRET);
    }

    @Autowired
    private SessionService sessionService;

    @RequestMapping(value = "/create-session", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<MessageResource> createSession(@RequestHeader("Authorization") String token,
                                                         @Valid @RequestBody CreateSessionResource createSessionResource) {

        sessionService.createSession(token, createSessionResource, openVidu);

        String message = "Session '" + createSessionResource.getSessionName() + "' created successfully!";
        System.out.println(message);
        return ResponseEntity.status(HttpStatus.CREATED).body(new MessageResource(message));
    }

    // @RequestMapping(value = "/remove-user", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    // public ResponseEntity<MessageResource> removeUser(@RequestHeader("Authorization") String token, @Valid @RequestBody RemoveUserResource removeUserResource) {

    //     sessionService.removeUser(token, removeUserResource);

    //     MessageResource messageResource = new MessageResource("User was removed successfully!");

    //     return ResponseEntity.status(HttpStatus.OK).body(messageResource);
    // }

    @RequestMapping(value = "/get-session-token", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<SessionTokenResource> getSessionToken(@RequestHeader("Authorization") String token,
                                                                @Valid @RequestBody SessionNamePasswordResource sessionNamePasswordResource) {

        SessionTokenResource sessionTokenResource = sessionService.getSessionToken(token, sessionNamePasswordResource);

        return ResponseEntity.status(HttpStatus.OK).body(sessionTokenResource);
    }

}