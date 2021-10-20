package app.exception;

public class WrongCredentialsException extends RuntimeException {
    public WrongCredentialsException() {
        super("WrongCredentialsException");
    }

    public WrongCredentialsException(String message) {
        super(message);
    }
}
