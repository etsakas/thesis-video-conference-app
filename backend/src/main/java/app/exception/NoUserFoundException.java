package app.exception;

public class NoUserFoundException extends RuntimeException {
    public NoUserFoundException() {
        super("NoUserFoundException");
    }

    public NoUserFoundException(String message) {
        super(message);
    }
}
