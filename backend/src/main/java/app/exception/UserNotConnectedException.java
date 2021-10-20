package app.exception;

public class UserNotConnectedException extends RuntimeException {
    public UserNotConnectedException() {
        super("UserNotConnectedException");
    }

    public UserNotConnectedException(String message) {
        super(message);
    }
}
