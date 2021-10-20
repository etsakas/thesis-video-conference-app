package app.controller;

import java.util.Map;
import app.service.SessionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/")
public class OpenViduWebhookController {

    @Autowired
    private SessionService sessionService;

    @Value("${WebhookSecret}")
    private String WebhookSecret;

    @RequestMapping(value = "/openvidu-webhook", method = RequestMethod.POST)
    public void getOpenViduRequest(@RequestHeader("Authorization") String secret, @RequestBody Map<String, String> eventMap) {
        
        if (!secret.equals(WebhookSecret)) {
            System.out.println("Could not authorize webhook request!");
            return;
        }

        System.out.println("Webhook request was authorized");
        System.out.println("Event: " + eventMap.get("event"));
        
        sessionService.manageWebhookEvent(eventMap);

    }
}
