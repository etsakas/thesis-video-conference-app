package app.exception;

public class UserNameTakenException extends RuntimeException {

    public UserNameTakenException() {
        super("UserNameTakenException");
    }

    public UserNameTakenException(String message) {
        super(message);
    }
}