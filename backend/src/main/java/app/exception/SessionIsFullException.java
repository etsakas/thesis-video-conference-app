package app.exception;

public class SessionIsFullException extends RuntimeException {
    public SessionIsFullException() {
        super("SessionIsFullException");
    }

    public SessionIsFullException(String message) {
        super(message);
    }
}
