# Master's thesis #
This is the source code for my master's thesis project:` `**` Development of a Video Conference Web Application
(Spring Boot, OpenVidu, WebRTC, React)`**` `



### Created using start.spring.io and npx create-react-app

1.  Run ```sudo docker-compose up``` inside the database folder.
2.  Run ```./gradlew bootRun``` inside the backend folder.
3. Run ```npm install``` , then ```npm start``` inside the frontend folder.
4. Run ```sudo docker run -p 4443:4443 --rm --network host -e OPENVIDU_SECRET=MY_SECRET -e OPENVIDU_SESSIONS_GARBAGE_INTERVAL=0 -e OPENVIDU_WEBHOOK=true -e OPENVIDU_WEBHOOK_ENDPOINT=http://localhost:8080/openvidu-webhook -e OPENVIDU_WEBHOOK_EVENTS=["sessionDestroyed","participantJoined","participantLeft"] -e OPENVIDU_WEBHOOK_HEADERS=[\"Authorization:\ MY_WEBHOOK_SECRET\"] openvidu/openvidu-server-kms:2.19.0```.
5. Visit ```https://localhost:4443``` and accept the certificate.
4. Visit ```http://localhost:3000```.

### Resources and useful links
#### Spring Boot
https://github.com/murraco/spring-boot-jwt  
https://blog.softtek.com/en/token-based-api-authentication-with-spring-and-jwt  
https://spring.io/guides/gs/accessing-data-jpa  
https://reflectoring.io/spring-boot-exception-handling  
https://blog.jayway.com/2013/02/03/improve-your-spring-rest-api-part-iii  
https://docs.spring.io/spring-security/site/docs/current/reference/html5/#multiple-httpsecurity  
https://spring.io/guides/gs/scheduling-tasks/  
https://www.marcobehler.com/guides/spring-security  
https://stackoverflow.com/questions/68145678/spring-custom-error-response-timestamp-vs-timestamp  
https://stackoverflow.com/questions/38282298/ambiguous-exceptionhandler-method-mapped-for-class-org-springframework-web-bin  
https://stackoverflow.com/questions/8367312/serializing-with-jackson-json-getting-no-serializer-found  
https://stackoverflow.com/questions/51048707/spring-boot-handling-nohandlerfoundexception  
https://stackoverflow.com/questions/35123835/spring-requestmapping-for-controllers-that-produce-and-consume-json  
https://stackoverflow.com/questions/68180568/does-having-a-custom-error-response-mean-that-you-have-to-catch-any-exception-in  
https://stackoverflow.com/questions/56527952/spring-boot-rest-responding-with-empty-body-for-exceptions-other-than-the-ones-o  
https://stackoverflow.com/questions/8969059/difference-between-onetomany-and-elementcollection  
https://stackoverflow.com/questions/65070223/how-to-throw-custom-exception-from-jwt-authentication-filter  
https://stackoverflow.com/questions/29106637/modify-default-json-error-response-from-spring-boot-rest-controller  
https://stackoverflow.com/questions/36795894/how-to-apply-spring-security-filter-only-on-secured-endpoints  
https://stackoverflow.com/questions/9787409/what-is-the-default-authenticationmanager-in-spring-security-how-does-it-authen  
https://stackoverflow.com/questions/57574981/what-is-httpbasic-method-in-spring-security  
https://stackoverflow.com/questions/17715921/exception-handling-for-filter-in-spring  
https://www.baeldung.com/exception-handling-for-rest-with-spring  
https://www.baeldung.com/global-error-handler-in-a-spring-rest-api  
https://www.baeldung.com/spring-bean-annotations  
https://www.baeldung.com/spring-core-annotations  
https://www.baeldung.com/spring-autowire  
https://www.baeldung.com/spring-controller-vs-restcontroller  
https://www.baeldung.com/spring-application-context  
https://www.baeldung.com/spring-valid-vs-validated  
https://www.baeldung.com/javax-validation-groups  
https://www.baeldung.com/spring-response-status-exception  
https://www.baeldung.com/spring-response-entity  
https://www.baeldung.com/spring-request-response-body  
https://www.baeldung.com/spring-validation-message-interpolation  
https://www.baeldung.com/spring-security-registration-password-encoding-bcrypt  
https://www.baeldung.com/javax-validation  
https://www.baeldung.com/spring-postconstruct-predestroy  
https://www.baeldung.com/hibernate-inheritance  
https://www.baeldung.com/spring-boot-add-filter  
https://www.baeldung.com/java-config-spring-security  
https://www.baeldung.com/spring-rest-http-headers  
https://www.baeldung.com/spring-security-custom-access-denied-page  
https://www.baeldung.com/spring-security-basic-authentication  
https://www.baeldung.com/spring-security-method-security  
https://www.baeldung.com/jackson-object-mapper-tutorial  

#### React

https://www.udemy.com/course/react-the-complete-guide-incl-redux  
https://reactjs.org/tutorial/tutorial.html  
https://www.taniarascia.com/getting-started-with-react  

#### OpenVidu
https://docs.openvidu.io/en/2.19.0/   
