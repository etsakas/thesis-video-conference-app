package app.exception;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.http.HttpStatus;

public class ErrorResponse {

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy hh:mm:ss")
    private final LocalDateTime timestamp;
    private final HttpStatus status;
    private final String message;
    private String path;
    private List<ValidationError> errors;

    public ErrorResponse(HttpStatus status, String message) {
        this.timestamp = LocalDateTime.now();
        this.status = status;
        this.message = message;
    }

    public void addValidationError(String field, String message, String rejectedValue) {
        if(this.errors == null){
            this.errors = new ArrayList<>();
        }
        
        this.errors.add(new ValidationError(field, message, rejectedValue));
    }

    // WARNING!
    // The getter is being used not only to retrieve the value (which is private)
    // but also to name the http error response field. So, having the getter's name
    // "getTimestamp" OR having no getter at all produces only one "timestamp" field
    // Having the getter's name "getFoo" creates the custom "timestamp" but also
    // a "foo" named field with the default date's format. That's odd.
    // Initially, getter's name was getTimeStamp so there were two timestamp fields
    // a "timestamp" and a "timeStamp".
    // Also, getters have to be public! Protected or package scope won't do.
    public LocalDateTime getTimestamp() {
        return this.timestamp;
    }

    public HttpStatus getStatus() {
        return this.status;
    }

    public String getMessage() {
        return this.message;
    }

    public List<ValidationError> getErrors() {
        return this.errors;
    }

    public String getPath() {
        return this.path;
    }

    public void setPath(String path) {
        this.path = path;
    }
}