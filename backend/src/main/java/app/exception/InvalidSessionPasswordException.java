package app.exception;

public class InvalidSessionPasswordException extends RuntimeException {
    public InvalidSessionPasswordException() {
        super("InvalidSessionPasswordException");
    }

    public InvalidSessionPasswordException(String message) {
        super(message);
    }
}
