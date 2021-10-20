package app.exception;

public class SessionNotFoundException extends RuntimeException {
    public SessionNotFoundException() {
        super("SessionNotFoundException");
    }

    public SessionNotFoundException(String message) {
        super(message);
    }
}
