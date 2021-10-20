package app.exception;

public class SessionObjectUsedException extends RuntimeException {
    public SessionObjectUsedException() {
        super("SessionObjectUsedException");
    }

    public SessionObjectUsedException(String message) {
        super(message);
    }
}
